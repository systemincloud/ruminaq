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

import org.eclipse.graphiti.features.IDirectEditingFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.services.Graphiti;
import org.osgi.service.component.annotations.Reference;
import org.ruminaq.consts.Constants;
import org.ruminaq.gui.api.GuiExtensionHandler;
import org.ruminaq.gui.label.DirectEditLabelFeature;

public class DirectEditingFeatureProvider extends FeatureProvider {

	public DirectEditingFeatureProvider(IFeatureProvider fp) {
		super(fp);
	}

	public IDirectEditingFeature getDirectEditingFeature(IDirectEditingContext context) {
		String labelProperty = Graphiti.getPeService().getPropertyValue(context.getPictogramElement(),	Constants.LABEL_PROPERTY);
		if (Boolean.parseBoolean(labelProperty)) {
			return new DirectEditLabelFeature(getFeatureProvider());
		}

		return extensions.getDirectEditingFeature(context, getFeatureProvider());
	}

}
