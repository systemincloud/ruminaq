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
import org.eclipse.graphiti.features.ILayoutFeature;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.osgi.service.component.annotations.Reference;
import org.ruminaq.gui.api.GuiExtensionHandler;
import org.ruminaq.model.model.ruminaq.Task;
import org.ruminaq.tasks.features.LayoutTaskFeature;

public class LayoutFeatureProvider extends FeatureProvider {

    @Reference
    private GuiExtensionHandler extensions;

	public LayoutFeatureProvider(IFeatureProvider fp) {
		super(fp);
	}

	public ILayoutFeature getLayoutFeature(ILayoutContext context) {
	    PictogramElement pictogramElement = context.getPictogramElement();
	    Object bo = getFeatureProvider().getBusinessObjectForPictogramElement(pictogramElement);

	    ILayoutFeature feature = extensions.getLayoutFeature(context, getFeatureProvider());
		if(feature != null) return feature;
		else if (bo instanceof Task) return new LayoutTaskFeature(getFeatureProvider());
		else return null;
	}

}
