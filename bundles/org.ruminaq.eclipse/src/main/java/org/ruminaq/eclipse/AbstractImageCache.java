/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.eclipse;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

/**
 * 
 * @author Marek Jagielski
 */
public abstract class AbstractImageCache {

	private static final Map<ImageDescriptor, Image> imageMap = new HashMap<>();

	protected static final Image getImage(ImageDescriptor imageDescriptor) {
		if (imageDescriptor == null) return null;
		Image image = imageMap.get(imageDescriptor);
		if (image == null) {
			image = imageDescriptor.createImage();
			imageMap.put(imageDescriptor, image);
		}
		return image;
	}

	public void dispose() {
		Iterator<Image> iter = imageMap.values().iterator();
		while (iter.hasNext()) iter.next().dispose();
		imageMap.clear();
	}
}
