/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.mux.impl;

import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.model.ruminaq.DataType;
import org.ruminaq.model.ruminaq.NGroup;
import org.ruminaq.model.ruminaq.PortData;
import org.ruminaq.model.ruminaq.PortInfo;
import org.ruminaq.model.ruminaq.PortType;
import org.ruminaq.model.ruminaq.Position;
import org.ruminaq.model.ruminaq.dt.Int32;

public enum Port implements PortsDescr {

  @PortInfo(portType = PortType.IN, id = "In", group = 1, n = -1,
      ngroup = NGroup.DIFFERENT)
  @PortData(type = DataType.class)
  IN,

  @PortInfo(portType = PortType.IN, id = "Idx", group = 0,
      pos = Position.BOTTOM, label = false)
  @PortData(type = Int32.class)
  IDX,

  @PortInfo(portType = PortType.OUT, id = "Out", label = false)
  @PortData(type = DataType.class)
  OUT;
}
