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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.osgi.service.component.annotations.Reference;
import org.ruminaq.gui.api.GuiExtensionHandler;
import org.ruminaq.gui.features.create.CreateFeature;
import org.ruminaq.gui.features.util.FeatureUtil;
import org.ruminaq.model.config.CommonConfig;
import org.ruminaq.tasks.TaskProvider;

public class CreateFeatureProvider extends FeatureProvider {

    @Reference
    private GuiExtensionHandler extensions;

	public CreateFeatureProvider(IFeatureProvider fp) { super(fp); }

	public List<ICreateFeature> getCreateFeatures() {
		List<ICreateFeature> standardFeatures = new ArrayList<>();
		standardFeatures.addAll(extensions.getCreateFeatures(getFeatureProvider()));
		standardFeatures.addAll(TaskProvider.INSTANCE.getCreateFeatures(getFeatureProvider()));
		standardFeatures.addAll(FeatureUtil.getAllLocalFeatures(CreateFeature.class, ICreateFeature.class, CommonConfig.getInstance(), fp));
		return standardFeatures;
	}

}
