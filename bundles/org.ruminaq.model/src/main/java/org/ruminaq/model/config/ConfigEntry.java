package org.ruminaq.model.config;

import org.javatuples.Quartet;
import org.ruminaq.model.model.ruminaq.BaseElement;

public class ConfigEntry {
	public Quartet<Class<? extends BaseElement>, ConfigCategory, Boolean, Boolean> entry;

	public ConfigEntry(Class<? extends BaseElement> clazz, ConfigCategory category, Boolean inPalette,
			Boolean inTestPalette) {
		entry = new Quartet<Class<? extends BaseElement>, ConfigCategory, Boolean, Boolean>(clazz, category, inPalette,
				inTestPalette);
	}
}