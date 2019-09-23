package org.ruminaq.tasks.demux.impl;

import org.ruminaq.model.desc.IN;
import org.ruminaq.model.desc.OUT;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.model.desc.Position;
import org.ruminaq.model.dt.Int32;
import org.ruminaq.model.ruminaq.DataType;

public enum Port implements PortsDescr {
  @IN(name = "In", type = DataType.class, group = 1, label = false)
  IN, @IN(name = "Idx", type = Int32.class, group = 0, pos = Position.BOTTOM,
      label = false)
  IDX, @OUT(name = "Out", type = DataType.class, n = -1)
  OUT;
}
