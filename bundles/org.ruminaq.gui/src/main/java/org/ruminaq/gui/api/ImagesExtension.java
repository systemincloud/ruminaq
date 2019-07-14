/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.api;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.FrameworkUtil;


/**
 * Other plugins can contribute to Graphiti images.
 *
 * @author Marek Jagielski
 */
public interface ImagesExtension {
	
	default Map<String, String> getImageKeyPath() {
		return Arrays.stream(getImageDecriptors())
		    .collect(Collectors.toMap(ImageDescriptor::name, i -> FileLocator
		        .find(FrameworkUtil.getBundle(i.clazz()), new Path(i.path()), null)
		        .toString()));
	}

	default ImageDescriptor[] getImageDecriptors() {
		return new ImageDescriptor[] {};
	}
}
