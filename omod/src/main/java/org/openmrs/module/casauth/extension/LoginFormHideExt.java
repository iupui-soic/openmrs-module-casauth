/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0 + Health disclaimer. If a copy of the MPL was not distributed with
 * this file, You can obtain one at http://license.openmrs.org
 */
package org.openmrs.module.casauth.extension;

import org.openmrs.module.Extension;

/**
 * The extension adds a JavaScript call that hides the login form
 * @author sunbiz
 */
public class LoginFormHideExt extends Extension {
	
	/**
	 * @return HTML
	 * @see org.openmrs.module.Extension#getMediaType()
	 */
	@Override
	public Extension.MEDIA_TYPE getMediaType() {
		return Extension.MEDIA_TYPE.html;
	}
	
	@Override
	public String getOverrideContent(String bodyContent) {
		return "<script>$j(\"form[action*='loginServlet']\").hide()</script>";
	}
}
