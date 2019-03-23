package org.ruminaq.tasks.mux.impl;

import org.ruminaq.model.desc.IN;
import org.ruminaq.model.desc.NGroup;
import org.ruminaq.model.desc.OUT;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.model.desc.Position;
import org.ruminaq.model.model.dt.Int32;
import org.ruminaq.model.model.ruminaq.DataType;

public enum Port implements PortsDescr {
	@IN (name="In",  type=DataType.class, group=1, n=-1, ngroup=NGroup.DIFFERENT) IN,
	@IN (name="Idx", type=Int32.class,    group=0, pos=Position.BOTTOM, label=false) IDX,
	@OUT(name="Out", type=DataType.class, label=false) OUT;
}
