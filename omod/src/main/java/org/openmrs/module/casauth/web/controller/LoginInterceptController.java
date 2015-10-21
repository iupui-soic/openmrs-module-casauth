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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class LoginInterceptController extends AbstractController {
	
	@Override
	public ModelAndView handleRequestInternal(HttpServletRequest req, HttpServletResponse res) throws MalformedURLException,
	        IOException {
		String casTicket = req.getParameter("casticket");
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
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			if (in.readLine().equals("yes")) {
				String superuserUsername = Context.getAdministrationService()
				        .getGlobalProperty("casauth.superuser.username");
				String superuserPassword = Context.getAdministrationService().getGlobalProperty(
				    "casauth.superuser.zpassword");
				String user = in.readLine();
				Context.authenticate(superuserUsername, superuserPassword);
				Context.becomeUser(user);
			}
			in.close();
		}
		return new ModelAndView("index");
	}
}
