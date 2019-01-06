/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.eclipse;

/**
 * 
 * @author Marek Jagielski
 */
public enum PluginImage {

	RUMINAQ_LOGO_16x16("icons/logo/ruminaq.logo.16x16.png"),     //$NON-NLS-1$
	RUMINAQ_LOGO_32x32("icons/logo/ruminaq.logo.32x32.png"),     //$NON-NLS-1$
	RUMINAQ_LOGO_48x48("icons/logo/ruminaq.logo.48x48.png"),     //$NON-NLS-1$
	RUMINAQ_LOGO_64x64("icons/logo/ruminaq.logo.64x64.png"),     //$NON-NLS-1$
	RUMINAQ_LOGO_128x128("icons/logo/ruminaq.logo.128x128.png"), //$NON-NLS-1$

	RUMINAQ_DIAGRAM("icons/system.png"),                            //$NON-NLS-1$
	RUMINAQ_TEST("icons/test.png");                                 //$NON-NLS-1$

	private final String imagePath;

	private PluginImage(final String imagePath) {
	    this.imagePath = imagePath;
	}

	public String getImagePath() {
	    return imagePath;
	}
}
