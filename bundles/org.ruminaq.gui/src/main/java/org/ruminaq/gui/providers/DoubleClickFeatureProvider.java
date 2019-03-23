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

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDoubleClickContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.services.Graphiti;
import org.osgi.service.component.annotations.Reference;
import org.ruminaq.gui.api.GuiExtensionHandler;
import org.ruminaq.gui.features.doubleclick.DoubleClickBaseElementFeature;
import org.ruminaq.gui.features.doubleclick.DoubleClickFeature;
import org.ruminaq.gui.features.util.FeatureUtil;
import org.ruminaq.model.config.CommonConfig;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.tasks.TaskProvider;

public class DoubleClickFeatureProvider extends FeatureProvider {

    @Reference
    private GuiExtensionHandler extensions;

	public DoubleClickFeatureProvider(IFeatureProvider fp) {
		super(fp);
	}

	public ICustomFeature getDoubleClickFeature(IDoubleClickContext context) {
		Object bo = null;
		for(Object o : Graphiti.getLinkService().getAllBusinessObjectsForLinkedPictogramElement(context.getPictogramElements()[0]))
			if(o instanceof BaseElement) {
				bo = o;
				break;
			}

		ICustomFeature feature = extensions.getDoubleClickFeature(context, getFeatureProvider());
		if(feature != null) return feature;
        ICustomFeature taskFeature = TaskProvider              .INSTANCE.getDoubleClickFeature(context, getFeatureProvider());
		if(taskFeature != null) return taskFeature;

		ICustomFeature localFeature = FeatureUtil.getLocalFeature(DoubleClickFeature.class, ICustomFeature.class,
                                                                  CommonConfig.getInstance(),
                                                                  bo, getFeatureProvider());
        if(localFeature != null) return localFeature;

		if(bo instanceof BaseElement) return new DoubleClickBaseElementFeature(getFeatureProvider());

		return null;
	}

}
