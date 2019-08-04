package org.ruminaq.gui.api;

import java.util.Collection;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.palette.IPaletteCompartmentEntry;

public interface PaletteCompartmentEntryExtension {
	Collection<IPaletteCompartmentEntry> getPalette(IFeatureProvider fp, boolean isTest);
}
