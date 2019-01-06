package org.ruminaq.tasks;

import java.util.HashMap;
import java.util.Map;

import org.ruminaq.logs.ModelerLoggerFactory;
import org.slf4j.Logger;

public enum Windows {
	INSTANCE;

	private final Logger logger = ModelerLoggerFactory.getLogger(Windows.class);

	private Map<Object, SicWindow> asociations = new HashMap<>();

	public void connectWindow(Class<?> clazz, Object o) {
		SicWindow window = asociations.get(o);
		if (window != null) {
			window.requestFocus();
			return;
		} else {
			try {
				logger.trace("create {}", clazz.getSimpleName());
				window = (SicWindow) clazz.newInstance();
				window.init(o);
				asociations.put(o, window);
			} catch (InstantiationException | IllegalAccessException e) { }
		}
	}

	public void disconnectWindow(Object o) {
		asociations.remove(o);
	}

}
