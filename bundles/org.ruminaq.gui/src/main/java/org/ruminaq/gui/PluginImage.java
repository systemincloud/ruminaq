/*
 * (C) Copyright 2018 Marek Jagielski.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ruminaq.gui;

import org.ruminaq.model.model.ruminaq.SimpleConnection;

public enum PluginImage {

	// Connection :
	                  TEST    (SimpleConnection.class,     PluginImageType.CONTEXT),
	// Ports :
//	                  IMG_PALETTE_INPUTPORT            (InputPort.class,            PluginImageType.PALETTE),
//	                  IMG_PALETTE_OUTPUTPORT           (OutputPort.class,           PluginImageType.PALETTE),
	;

	private static final String DEFAULT_IMAGE_DIR = "icons/";

	private final String imageKey;
	private final String imagePath;

	public String getImageKey()  { return imageKey; }
	public String getImagePath() { return imagePath; }

	private PluginImage(final Class<?> clazz, final PluginImageType type) {
		String name = "";
		name += type.toString().toLowerCase() + ".";
		name += clazz.getSimpleName().toLowerCase();
		this.imageKey = Activator.PLUGIN_ID + "." + name;

		String fileName = name;
		switch(type) {
			case CONTEXT : fileName += ".gif"; break;
			case PALETTE : fileName += ".png"; break;
			case DIAGRAM : fileName += ".png"; break;
		    default:                           break;
		}
		this.imagePath = DEFAULT_IMAGE_DIR + fileName;
	}
}
