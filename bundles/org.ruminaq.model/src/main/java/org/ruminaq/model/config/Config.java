package org.ruminaq.model.config;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.ruminaq.model.model.ruminaq.BaseElement;

public abstract class Config {

	public abstract List<ConfigEntry> getElements();

	public boolean containsClass(Type type) {
		for(ConfigEntry ce : getElements())
			if(ce.entry.getValue0().equals(type)) return true;
		return false;
	}

	public List<Class<? extends BaseElement>> getAllClasses() {
		List<Class<? extends BaseElement>> ret = new ArrayList<>();
		for(ConfigEntry ce : getElements()) ret.add(ce.entry.getValue0());
		return ret;
	}

	public ConfigEntry getEntryForClassName(String name) {
		for(ConfigEntry ce : getElements())
			if(ce.entry.getValue0().getSimpleName().equals(name)) return ce;
		return null;
	}
}
