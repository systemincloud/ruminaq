/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.sipo.impl;

import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.model.ruminaq.DataType;
import org.ruminaq.model.ruminaq.PortData;
import org.ruminaq.model.ruminaq.PortInfo;
import org.ruminaq.model.ruminaq.PortType;
import org.ruminaq.model.ruminaq.Position;
import org.ruminaq.model.ruminaq.dt.Control;
import org.ruminaq.model.ruminaq.dt.Int32;

public enum Port implements PortsDescr {

  @PortInfo(portType = PortType.IN, id = "In", group = 0)
  @PortData(type = DataType.class)
  IN,

  @PortInfo(portType = PortType.IN, id = "CLK", opt = true,
      pos = Position.RIGHT, group = 0)
  @PortData(type = Control.class)
  CLK,

  @PortInfo(portType = PortType.IN, id = "Idx", opt = true,
      pos = Position.RIGHT, group = 1)
  @PortData(type = Int32.class)
  IDX,

  @PortInfo(portType = PortType.IN, id = "T", opt = true, pos = Position.RIGHT,
      group = 2)
  @PortData(type = Control.class)
  TRIGGER,

  @PortInfo(portType = PortType.OUT, id = "Out", n = -1, pos = Position.TOP)
  @PortData(type = DataType.class)
  OUT,

  @PortInfo(portType = PortType.OUT, id = "Out", opt = true, pos = Position.TOP)
  @PortData(type = DataType.class)
  LOUT,

  @PortInfo(portType = PortType.OUT, id = "N", opt = true, pos = Position.RIGHT)
  @PortData(type = Int32.class)
  SIZE;
}
