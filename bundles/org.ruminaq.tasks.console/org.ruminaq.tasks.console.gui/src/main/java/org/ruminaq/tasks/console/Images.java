/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.console;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.ImagesExtension;

/**
 *
 * @author Marek Jagielski
 */
@Component(property = { "service.ranking:Integer=5" })
public class Images implements ImagesExtension {

	public enum K {
		IMG_CONSOLE_PALETTE("/icons/palette.console.png"),
		IMG_CONSOLE_DIAGRAM("/icons/diagram.console.png"),;

		private String path;

		K(String path) {
			this.path = path;
		}
	}

	public Map<String, String> getImageKeyPath() {
		return Arrays.stream(K.values())
		    .collect(Collectors.toMap(K::name, i -> FileLocator
		        .find(FrameworkUtil.getBundle(Images.class), new Path(i.path), null)
		        .toString()));
	}
}
