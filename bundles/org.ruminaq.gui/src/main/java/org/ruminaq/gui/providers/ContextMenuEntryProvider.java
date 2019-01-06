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

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.tb.ContextMenuEntry;
import org.eclipse.graphiti.tb.IContextMenuEntry;
import org.osgi.service.component.annotations.Reference;
import org.ruminaq.consts.Constants;
import org.ruminaq.gui.api.GuiExtensionHandler;
import org.ruminaq.gui.features.create.CreateSimpleConnectionPointFeature;
import org.ruminaq.model.model.ruminaq.SimpleConnection;
import org.ruminaq.tasks.TaskProvider;
import org.ruminaq.util.GraphicsUtil;

public class ContextMenuEntryProvider extends FeatureProvider {

    @Reference
    private GuiExtensionHandler extensions;

	public ContextMenuEntryProvider(IFeatureProvider fp) {
		super(fp);
	}

	public IContextMenuEntry[] getContextMenu(ICustomContext context) {
		List<IContextMenuEntry> menu = new ArrayList<>();

		PictogramElement[] pes = context.getPictogramElements();
		List<Object> bos = new ArrayList<>();
		for(PictogramElement pe : pes) bos.add(getFeatureProvider().getBusinessObjectForPictogramElement(pe));

		ICustomFeature[] customFeatures = getFeatureProvider().getCustomFeatures(context);

		for(ICustomFeature customFeature : customFeatures) {
			ContextMenuEntry menuEntry = new ContextMenuEntry(customFeature, context);
			menuEntry.setText(customFeature.getName());
			if(customFeature.isAvailable(context)) {
				if(bos.size() == 1) {
					if(customFeature.getName().equals(CreateSimpleConnectionPointFeature.NAME)
						&& bos.get(0) instanceof SimpleConnection
						&& GraphicsUtil.distanceToConnection((FreeFormConnection) pes[0], context.getX(), context.getY(), Constants.INTERNAL_PORT) < 5) menu.add(menuEntry);
				} else if(bos.size() > 1) {

				}
			}
		}

		menu.addAll(extensions.getContextMenu(context, getFeatureProvider()));
		menu.addAll(TaskProvider.INSTANCE.getContextMenu(context, getFeatureProvider()));

		return menu.toArray(new IContextMenuEntry[menu.size()]);
	}

}
