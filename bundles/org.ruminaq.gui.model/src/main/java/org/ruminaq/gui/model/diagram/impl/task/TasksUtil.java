/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.model.diagram.impl.task;

import org.ruminaq.model.desc.PortsDescr;

public class TasksUtil {

  public static boolean isMultiplePortId(String id, String prefix) {
    return id.matches(prefix + " [1-9][0-9]*");
  }

  public static boolean isMultiplePortId(String id, PortsDescr pd) {
    return isMultiplePortId(id, pd.getId());
  }

  public static int getMultiplePortIdIdx(String id, PortsDescr pd) {
    return Integer.parseInt(id.substring(pd.getId().length() + 1));
  }

}
