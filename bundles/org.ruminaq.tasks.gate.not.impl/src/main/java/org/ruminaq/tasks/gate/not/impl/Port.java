package org.ruminaq.tasks.gate.not.impl;

import org.ruminaq.model.desc.IN;
import org.ruminaq.model.desc.OUT;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.model.model.dt.Bool;

public enum Port implements PortsDescr {
	@IN (name="In",  type=Bool.class, label=false) IN,
	@OUT(name="Out", type=Bool.class, label=false) OUT;
}
