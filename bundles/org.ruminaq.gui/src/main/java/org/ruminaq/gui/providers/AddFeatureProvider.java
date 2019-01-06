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

import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.osgi.service.component.annotations.Reference;
import org.ruminaq.gui.api.GuiExtensionHandler;
import org.ruminaq.gui.features.add.AddFeature;
import org.ruminaq.gui.features.util.FeatureUtil;
import org.ruminaq.model.config.CommonConfig;
import org.ruminaq.tasks.TaskProvider;

public class AddFeatureProvider extends FeatureProvider {

    @Reference
    private GuiExtensionHandler extensions;

	public AddFeatureProvider(IFeatureProvider fp) { super(fp);	}

	public IAddFeature getAddFeature(IAddContext context) {
		IAddFeature extensionFeature = extensions.getAddFeature(context, getFeatureProvider());
		if(extensionFeature != null) {
			return extensionFeature;
		}
		IAddFeature taskFeature = TaskProvider.INSTANCE.getAddFeature(context, getFeatureProvider());
		if(taskFeature != null) {
			return taskFeature;
		}

		IAddFeature localFeature = FeatureUtil.getLocalFeature(
				AddFeature.class, IAddFeature.class,
				CommonConfig.getInstance(),
				context.getNewObject(), getFeatureProvider());
		return localFeature;
	}
}
