/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.tasks.console.model;

import org.ruminaq.gui.model.PortDiagram;
import org.ruminaq.gui.model.Position;
import org.ruminaq.model.ruminaq.PortData;
import org.ruminaq.model.ruminaq.PortInfo;
import org.ruminaq.model.ruminaq.PortType;
import org.ruminaq.model.ruminaq.PortsDescr;
import org.ruminaq.model.ruminaq.dt.Text;

public enum Port implements PortsDescr {

  @PortInfo(portType = PortType.IN, id = "In", opt = true)
  @PortDiagram(label = false, pos = Position.RIGHT)
  @PortData(type = Text.class)
  IN,

  @PortInfo(portType = PortType.OUT, id = "Out", opt = true)
  @PortDiagram(label = false, pos = Position.LEFT)
  @PortData(type = Text.class)
  OUT;

}
