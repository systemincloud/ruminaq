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
package org.ruminaq.gui.palette;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.palette.IPaletteCompartmentEntry;
import org.eclipse.graphiti.palette.impl.ConnectionCreationToolEntry;
import org.eclipse.graphiti.palette.impl.ObjectCreationToolEntry;
import org.eclipse.graphiti.palette.impl.PaletteCompartmentEntry;
import org.eclipse.graphiti.palette.impl.StackEntry;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.PaletteCompartmentEntryExtension;
import org.ruminaq.gui.features.create.PaletteCreateFeature;

@Component(property = { "service.ranking:Integer=5" })
public class CommonPaletteCompartmentEntry
    implements PaletteCompartmentEntryExtension {

	public static final String DEFAULT_COMPARTMENT = "Commons";
	public static final String CONNECTIONS_STACK = "Connections";
	public static final String PORTS_STACK = "Ports";
	public static final String SOURCES_STACK = "Sources";
	public static final String FLOW_STACK = "Flow";
	public static final String LOGIC_STACK = "Logic";
	public static final String USERDEFINED_STACK = "User defined";
	public static final String SINKS_STACK = "Sinks";

	@Override
	public Collection<IPaletteCompartmentEntry> getPalette(IFeatureProvider fp,
	    boolean isTest) {
		IPaletteCompartmentEntry commonCompartmentEntry = new PaletteCompartmentEntry(
		    DEFAULT_COMPARTMENT, null);
		commonCompartmentEntry.setInitiallyOpen(false);

		ICreateConnectionFeature[] createConnectionFeatures = fp
		    .getCreateConnectionFeatures();

		Stream.of(CONNECTIONS_STACK).forEachOrdered(stackName -> {
			StackEntry connectionsStackEntry = new StackEntry(CONNECTIONS_STACK, "",
			    null);
			Stream.of(createConnectionFeatures)
			    .filter(cf -> cf instanceof PaletteCreateFeature)
			    .map(cf -> (PaletteCreateFeature & ICreateConnectionFeature) cf)
			    .filter(cf -> isTest ? cf.isTestOnly() : true)
			    .filter(cf -> DEFAULT_COMPARTMENT.equals(cf.getCompartment()))
			    .filter(cf -> stackName.equals(cf.getStack())).forEach(cf -> {
				    ConnectionCreationToolEntry cte = new ConnectionCreationToolEntry(
				        cf.getCreateName(), cf.getCreateDescription(),
				        cf.getCreateImageId(), cf.getCreateLargeImageId());
				    cte.addCreateConnectionFeature(cf);
				    connectionsStackEntry.addCreationToolEntry(cte);
			    });

			if (connectionsStackEntry.getCreationToolEntries().size() > 0) {
				commonCompartmentEntry.getToolEntries().add(connectionsStackEntry);
			}
		});

		ICreateFeature[] createFeatures = fp.getCreateFeatures();

		Stream.of(PORTS_STACK, SOURCES_STACK, SINKS_STACK).forEachOrdered(stackName -> {
			StackEntry stackEntry = new StackEntry(stackName, "", null);
			Stream.of(createFeatures).filter(cf -> cf instanceof PaletteCreateFeature)
			    .map(cf -> (PaletteCreateFeature & ICreateFeature) cf)
			    .filter(cf -> isTest ? !cf.isTestOnly() : true)
			    .filter(cf -> DEFAULT_COMPARTMENT.equals(cf.getCompartment()))
			    .filter(cf -> stackName.equals(cf.getStack())).forEach(cf -> {
				    stackEntry.addCreationToolEntry(new ObjectCreationToolEntry(
				        cf.getCreateName(), cf.getCreateDescription(),
				        cf.getCreateImageId(), cf.getCreateLargeImageId(), cf));
			    });

			commonCompartmentEntry.getToolEntries().add(stackEntry);
		});

		return Arrays.asList(commonCompartmentEntry);
	}
}
