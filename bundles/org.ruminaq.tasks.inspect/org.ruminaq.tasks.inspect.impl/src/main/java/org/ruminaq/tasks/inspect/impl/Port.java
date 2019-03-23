package org.ruminaq.tasks.inspect.impl;

import org.ruminaq.model.desc.IN;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.model.ruminaq.DataType;

public enum Port implements PortsDescr {
	@IN (name="In", type=DataType.class, label = false) IN,
}
