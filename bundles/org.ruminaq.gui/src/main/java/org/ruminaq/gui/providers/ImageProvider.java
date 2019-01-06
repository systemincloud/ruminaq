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
package org.ruminaq.gui.providers;

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.graphiti.ui.platform.AbstractImageProvider;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.component.annotations.Reference;
import org.ruminaq.gui.api.GuiExtensionHandler;
import org.ruminaq.gui.features.Images;
import org.ruminaq.tasks.TaskProvider;

public class ImageProvider extends AbstractImageProvider {

    @Reference
    private GuiExtensionHandler extensions;

	@Override
	protected void addAvailableImages() {
//
//		for (final PluginImage pluginImage : PluginImage.values()) {
//			addImageFilePath(pluginImage.getImageKey(),	pluginImage.getImagePath());
//		}
		Bundle b = FrameworkUtil.getBundle(Images.class);
		if(Bundle.ACTIVE != b.getState()) {
			try { b.start(); } catch (BundleException e) { }
		}

		// add image path from general features
		Map<String, String> imgs = Images.getImageKeyPath();
		for (Entry<String, String> img : imgs.entrySet()) {
			addImageFilePath(img.getKey(), img.getValue());
		}

		// add image path from tasks
		imgs = TaskProvider.INSTANCE.getImageKeyPath();
		for (Entry<String, String> img : imgs.entrySet()) {
			addImageFilePath(img.getKey(), img.getValue());
		}

		// add image path from installed extensions
		imgs = extensions.getImageKeyPath();
		for (Entry<String, String> img : imgs.entrySet()) {
			addImageFilePath(img.getKey(), img.getValue());
		}
	}

}
