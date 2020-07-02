/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.gate.gui;

import org.ruminaq.gui.model.PortDiagram;
import org.ruminaq.gui.model.Position;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.model.ruminaq.dt.Bool;
import org.ruminaq.model.ruminaq.PortData;
import org.ruminaq.model.ruminaq.PortInfo;
import org.ruminaq.model.ruminaq.PortType;

public enum Port implements PortsDescr {
  
  @PortInfo(portType = PortType.IN, id = "In", n = -1)
  @PortDiagram(label = false)
  @PortData(type = Bool.class)
  IN, 
  
  @PortInfo(portType = PortType.OUT, id = "Out")
  @PortDiagram(pos = Position.RIGHT, label = false)
  @PortData(type = Bool.class)
  OUT;
}
