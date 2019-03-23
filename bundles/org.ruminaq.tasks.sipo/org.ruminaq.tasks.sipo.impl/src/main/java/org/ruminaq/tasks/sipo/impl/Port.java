package org.ruminaq.tasks.sipo.impl;

import org.ruminaq.model.desc.IN;
import org.ruminaq.model.desc.OUT;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.model.desc.Position;
import org.ruminaq.model.dt.Control;
import org.ruminaq.model.dt.Int32;
import org.ruminaq.model.ruminaq.DataType;

public enum Port implements PortsDescr {
	@IN (name="In",  type=DataType.class, group=0)                               IN,
	@IN (name="CLK", type=Control.class,  opt=true, pos=Position.RIGHT, group=0) CLK,
	@IN (name="Idx", type=Int32.class,    opt=true, pos=Position.RIGHT, group=1) IDX,
	@IN (name="T",   type=Control.class,  opt=true, pos=Position.RIGHT, group=2) TRIGGER,
	@OUT(name="Out", type=DataType.class, n=-1,     pos=Position.TOP)            OUT,
	@OUT(name="Out", type=DataType.class, opt=true, pos=Position.TOP)            LOUT,
	@OUT(name="N",   type=Int32.class,    opt=true, pos=Position.RIGHT)          SIZE;
}
