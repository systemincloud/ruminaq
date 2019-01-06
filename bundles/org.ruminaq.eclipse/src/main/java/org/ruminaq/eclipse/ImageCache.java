/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.eclipse;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.ruminaq.consts.Constants.SicPlugin;

/**
 * 
 * @author Marek Jagielski
 */
public class ImageCache extends AbstractImageCache {

	public static final ImageDescriptor getImageDescriptor(PluginImage image) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(
				SicPlugin.ECLIPSE_ID.s(), image.getImagePath());
	}

	public final Image getImage(PluginImage image) {
		final ImageDescriptor descriptor = getImageDescriptor(image);
		return getImage(descriptor);
	}

}
