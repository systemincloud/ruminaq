/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.runner.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.ruminaq.model.ruminaq.PortsDescr;
import org.ruminaq.runner.impl.data.DataI;

public class PortMap extends HashMap<String, DataI> {

  private static final long serialVersionUID = 1L;

  public DataI get(PortsDescr portDesc) {
    return this.get(portDesc.getId());
  }

  public List<DataI> getAll(PortsDescr portDesc) {
    List<String> portIds = new LinkedList<>();
    for (String portId : this.keySet()) {
      if (portId.startsWith(portDesc.getId() + " ")) {
        try {
          Integer.parseInt(portId.substring(portDesc.getId().length() + 1));
          portIds.add(portId);
        } catch (Exception e) {
        }
      }
    }
    List<DataI> ret = new LinkedList<DataI>();
    for (String portId : portIds)
      ret.add(this.get(portId));
    return ret;
  }
}
