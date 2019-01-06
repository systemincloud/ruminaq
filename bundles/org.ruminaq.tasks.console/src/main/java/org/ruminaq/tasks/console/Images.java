/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.tasks.console;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.FrameworkUtil;

public class Images {

	public enum K {
		IMG_CONSOLE_PALETTE("/icons/palette.console.png"),
		IMG_CONSOLE_DIAGRAM("/icons/diagram.console.png"),
		;

        private String path;
		K(String path) {
		    this.path =  path;
        }
	}

    private static Map<String, String> images = Arrays.stream(K.values())
            .collect(Collectors.toMap(
                    K::name,
                    i -> FileLocator.find(
                            FrameworkUtil.getBundle(Images.class),
                            new Path(i.path),
                            null).toString()));

	public static Map<String, String> getImageKeyPath() {
	    return images;
	}
}
