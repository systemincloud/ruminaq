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
import org.eclipse.graphiti.features.IPasteFeature;
import org.eclipse.graphiti.features.context.IPasteContext;
import org.osgi.service.component.annotations.Reference;
import org.ruminaq.gui.PasteElementFeature;
import org.ruminaq.gui.api.GuiExtensionHandler;

public class PasteFeatureProvider extends FeatureProvider {

	@Reference
	private GuiExtensionHandler extensions;

	public PasteFeatureProvider(IFeatureProvider fp) {
		super(fp);
	}

	public IPasteFeature getPasteFeature(IPasteContext context) {
		IPasteFeature feature = extensions.getPasteFeature(context,
		    getFeatureProvider());
		if (feature != null)
			return feature;
		return new PasteElementFeature(getFeatureProvider());
	}
}
