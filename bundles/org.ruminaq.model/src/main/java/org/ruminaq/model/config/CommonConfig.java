package org.ruminaq.model.config;

import static java.lang.Boolean.TRUE;
import static java.lang.Boolean.FALSE;

import java.util.ArrayList;
import java.util.List;

import org.ruminaq.model.ruminaq.InputPort;
import org.ruminaq.model.ruminaq.OutputPort;
import org.ruminaq.model.ruminaq.SimpleConnection;

public final class CommonConfig extends Config {

	private static CommonConfig instance = new CommonConfig();
	private CommonConfig() { }
	public static  CommonConfig getInstance() { return instance; }

	@Override public List<ConfigEntry> getElements() { return elements; }

	public enum CommonCategory implements ConfigCategory {
		CONNECTIONS, PORTS;
	}

	private List<ConfigEntry> elements = new ArrayList<ConfigEntry>() {
		private static final long serialVersionUID = 1L;
		{
			add(new ConfigEntry(SimpleConnection.class, CommonCategory.CONNECTIONS, FALSE, FALSE));
			add(new ConfigEntry(InputPort.class,        CommonCategory.PORTS,       TRUE,  TRUE));
			add(new ConfigEntry(OutputPort.class,       CommonCategory.PORTS,       TRUE,  TRUE));
		}
	};

}
