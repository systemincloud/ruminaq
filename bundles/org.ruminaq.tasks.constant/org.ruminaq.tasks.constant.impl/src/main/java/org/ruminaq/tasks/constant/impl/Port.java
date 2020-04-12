/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.constant.impl;

import org.ruminaq.model.desc.OUT;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.model.dt.Int32;

public enum Port implements PortsDescr {
  @OUT(name = "Out", type = Int32.class, label = false)
  OUT;
}
