/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.randomgenerator.impl;

import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.model.ruminaq.PortData;
import org.ruminaq.model.ruminaq.PortInfo;
import org.ruminaq.model.ruminaq.PortType;
import org.ruminaq.model.ruminaq.dt.Int32;

public enum Port implements PortsDescr {

  @PortInfo(portType = PortType.OUT, id = "Out", label = false)
  @PortData(type = Int32.class)
  OUT;
}
