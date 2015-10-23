/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0 + Health disclaimer. If a copy of the MPL was not distributed with
 * this file, You can obtain one at http://license.openmrs.org
 */
package org.openmrs.module.casauth.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmrs.api.context.Context;

/**
 * This is used to intercept the /logout URL which is normally dealt with the logoutServlet. 
 * Thus, this could not be done through a Spring controller and hence needs a module filter.
 * @author sunbiz
 */
//TODO: verify if local logout has not thrown an exception
//TODO: intercept the /session method DELETE call to deal with logout from REST client app
public class LogoutInterceptFilter implements Filter {
	
	@Override
	public void init(FilterConfig fc) throws ServletException {
		
	}
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		String requestURI = request.getRequestURI();
		if (requestURI.endsWith("/logout")) {
			Context.logout();
			HttpServletResponse httpResponse = (HttpServletResponse) res;
			httpResponse.sendRedirect(Context.getAdministrationService().getGlobalProperty("casauth.endpoint.logout"));
		} else {
			chain.doFilter(req, res);
		}
	}
	
	@Override
	public void destroy() {
	}
	
}
