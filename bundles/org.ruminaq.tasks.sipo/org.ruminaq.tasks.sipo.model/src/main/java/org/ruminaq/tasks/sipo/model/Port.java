/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.sipo.model;

import org.ruminaq.gui.model.PortDiagram;
import org.ruminaq.gui.model.Position;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.model.ruminaq.DataType;
import org.ruminaq.model.ruminaq.PortData;
import org.ruminaq.model.ruminaq.PortInfo;
import org.ruminaq.model.ruminaq.PortType;
import org.ruminaq.model.ruminaq.dt.Control;
import org.ruminaq.model.ruminaq.dt.Int32;

public enum Port implements PortsDescr {

  @PortInfo(portType = PortType.IN, id = "In", group = 0)
  @PortData(type = DataType.class)
  IN,

  @PortInfo(portType = PortType.IN, id = "CLK", opt = true, group = 0)
  @PortDiagram(pos = Position.RIGHT)
  @PortData(type = Control.class)
  CLK,

  @PortInfo(portType = PortType.IN, id = "Idx", opt = true, group = 1)
  @PortDiagram(pos = Position.RIGHT)
  @PortData(type = Int32.class)
  IDX,

  @PortInfo(portType = PortType.IN, id = "T", opt = true, group = 2)
  @PortDiagram(pos = Position.RIGHT)
  @PortData(type = Control.class)
  TRIGGER,

  @PortInfo(portType = PortType.OUT, id = "Out", n = -1)
  @PortDiagram(pos = Position.TOP)
  @PortData(type = DataType.class)
  OUT,

  @PortInfo(portType = PortType.OUT, id = "Out", opt = true)
  @PortDiagram(pos = Position.TOP)
  @PortData(type = DataType.class)
  LOUT,

  @PortInfo(portType = PortType.OUT, id = "N", opt = true)
  @PortDiagram(pos = Position.RIGHT)
  @PortData(type = Int32.class)
  SIZE;
}
