package org.ruminaq.gui.features.create;

public interface PaletteCreateFeature {
	default boolean isTestOnly() {
		return false;
	}

	String getCompartment();

	String getStack();
}
