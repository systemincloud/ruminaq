/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.mux.model;

import org.ruminaq.gui.model.PortDiagram;
import org.ruminaq.gui.model.Position;
import org.ruminaq.model.ruminaq.DataType;
import org.ruminaq.model.ruminaq.PortData;
import org.ruminaq.model.ruminaq.PortInfo;
import org.ruminaq.model.ruminaq.PortType;
import org.ruminaq.model.ruminaq.PortsDescr;
import org.ruminaq.model.ruminaq.dt.Int32;

public enum DemuxPort implements PortsDescr {

  @PortInfo(portType = PortType.IN, id = "In", group = 1)
  @PortDiagram(label = false, pos = Position.LEFT)
  @PortData(type = DataType.class)
  IN,

  @PortInfo(portType = PortType.IN, id = "Idx", group = 0)
  @PortDiagram(label = false, pos = Position.BOTTOM)
  @PortData(type = Int32.class)
  IDX,

  @PortInfo(portType = PortType.OUT, id = "Out", n = -1)
  @PortData(type = DataType.class)
  OUT;
}
