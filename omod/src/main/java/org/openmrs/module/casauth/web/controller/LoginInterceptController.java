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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginInterceptController {
	
	@RequestMapping( { "/login.htm" })
	public String redirectIntercepted(HttpServletRequest req, HttpServletResponse res) throws MalformedURLException,
	        IOException {
		String casTicket = req.getParameter("casticket");
		if (casTicket == null) {
			User autheticatedUser = Context.getAuthenticatedUser();
			if (autheticatedUser == null) {
				String casLoginUrl = Context.getAdministrationService().getGlobalProperty("casauth.endpoint.login");
				String casAppCode = Context.getAdministrationService().getGlobalProperty("casauth.endpoint.appcode");
				return "redirect:" + casLoginUrl + "?cassvc=" + casAppCode + "&casurl=" + req.getRequestURL();
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
			String user = new String();
			if (in.readLine().equals("yes")) {
				String superuserUsername = Context.getAdministrationService()
				        .getGlobalProperty("casauth.superuser.username");
				String superuserPAssword = Context.getAdministrationService()
				        .getGlobalProperty("casauth.superuser.password");
				user = in.readLine();
				Context.authenticate(superuserUsername, superuserPAssword);
				Context.becomeUser(user);
			}
			in.close();
		}
		return "index";
	}
}
