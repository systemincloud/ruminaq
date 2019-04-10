package org.ruminaq.tasks.randomgenerator.impl;

import org.ruminaq.model.desc.OUT;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.model.dt.Int32;

public enum Port implements PortsDescr {
	@OUT(name="Out", type=Int32.class, label=false) OUT;
}
