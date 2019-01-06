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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.palette.IPaletteCompartmentEntry;
import org.eclipse.graphiti.palette.impl.PaletteCompartmentEntry;
import org.osgi.service.component.annotations.Reference;
import org.ruminaq.consts.Constants.SicPlugin;
import org.ruminaq.gui.Messages;
import org.ruminaq.gui.api.GuiExtensionHandler;
import org.ruminaq.gui.features.util.FeatureUtil;
import org.ruminaq.model.config.CommonConfig;
import org.ruminaq.tasks.TaskProvider;

public class PaletteCompartmentEntryProvider extends FeatureProvider {

    @Reference
    private GuiExtensionHandler extensions;

	public PaletteCompartmentEntryProvider(IFeatureProvider fp) { super(fp); }

	public IPaletteCompartmentEntry[] getPalette() {
		List<IPaletteCompartmentEntry>        palette      = new ArrayList<>();
		Map<String, IPaletteCompartmentEntry> compartments = new HashMap<>();

	    IPaletteCompartmentEntry commonCompartmentEntry = new PaletteCompartmentEntry(Messages.UI_Pallete_compartment_name, null);
	    commonCompartmentEntry.setInitiallyOpen(false);
	    // Add general non-task create feature //
	    commonCompartmentEntry.getToolEntries().addAll(FeatureUtil.getStackEntries(CommonConfig.CommonCategory.values(),
                                                       getFeatureProvider().getCreateFeatures(),
                                                       CommonConfig.getInstance(),
                                                       false));

	    palette.add(commonCompartmentEntry);
	    compartments.put(SicPlugin.GUI_ID.s(), commonCompartmentEntry);

	    Map<String, IPaletteCompartmentEntry> extsCompartmentEntries = extensions.getPaletteCompartmentEntries(getFeatureProvider().getCreateFeatures());

	    palette.addAll(extsCompartmentEntries.values());
	    compartments.putAll(extsCompartmentEntries);

	    TaskProvider.INSTANCE.getPaletteCompartmentEntries(getFeatureProvider().getCreateFeatures(), compartments);

		return palette.toArray(new IPaletteCompartmentEntry[palette.size()]);
	}
}
