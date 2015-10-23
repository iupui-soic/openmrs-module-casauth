/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0 + Health disclaimer. If a copy of the MPL was not distributed with
 * this file, You can obtain one at http://license.openmrs.org
 */
package org.openmrs.module.casauth.web.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

/**
 * This controller intercepts the /login.htm URL and handles that request. This takes 
 * precedence over annotation-based controller that is used in the Reference application. 
 * The controller checks if a casticket parameter is passed. If this is passed then 
 * casticket is validation by using a HttpURLConnection and if the response contains "yes" 
 * and username, then it logins as that username.
 * See details of CAS authentication - https://kb.iu.edu/d/atfc
 * @author sunbiz
 */
public class LoginInterceptController extends AbstractController {
	
	@Override
	public ModelAndView handleRequestInternal(HttpServletRequest req, HttpServletResponse res) throws MalformedURLException,
	        IOException {
		String casTicket = req.getParameter("casticket");
		//CAS ticket is only available after login has completed and needs validation
		if (casTicket == null) {
			User autheticatedUser = Context.getAuthenticatedUser();
			if (autheticatedUser == null) {
				String casLoginUrl = Context.getAdministrationService().getGlobalProperty("casauth.endpoint.login");
				String casAppCode = Context.getAdministrationService().getGlobalProperty("casauth.endpoint.appcode");
				return new ModelAndView("redirect:" + casLoginUrl + "?cassvc=" + casAppCode + "&casurl="
				        + req.getRequestURL());
			}
		} else {
			String casValidateUrl = Context.getAdministrationService().getGlobalProperty("casauth.endpoint.validate");
			String casAppCode = Context.getAdministrationService().getGlobalProperty("casauth.endpoint.appcode");
			URL url = new URL(casValidateUrl + "?cassvc=" + casAppCode + "&casticket=" + casTicket + "&casurl="
			        + req.getRequestURL());
			//a HTTP connection is created to validate the casticket
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			try {
				/**
				 * The CAS validate response is yes on first line, followed by username on next line
				 */
				if (in.readLine().equals("yes")) {
					String superuserUsername = Context.getAdministrationService().getGlobalProperty(
					    "casauth.superuser.username");
					String superuserPassword = Context.getAdministrationService().getGlobalProperty(
					    "casauth.superuser.zpassword");
					String user = in.readLine();
					Context.authenticate(superuserUsername, superuserPassword);
					Context.becomeUser(user);
				}
			}
			catch (ContextAuthenticationException casex) {
				Context.logout();
			}
			in.close();
		}
		return new ModelAndView("redirect:/index.htm");
	}
}
