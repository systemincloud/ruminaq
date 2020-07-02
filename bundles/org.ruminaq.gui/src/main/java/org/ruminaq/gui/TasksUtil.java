/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui;

import java.util.List;
import java.util.stream.Collectors;

import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.model.ruminaq.InternalInputPort;
import org.ruminaq.model.ruminaq.InternalOutputPort;
import org.ruminaq.model.ruminaq.Task;

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

  public static List<InternalInputPort> getAllMutlipleInternalInputPorts(
      Task task, String prefix) {
    return task.getInputPort().stream()
        .filter(
            ip -> ip.getId().matches(String.format("%s [1-9][0-9]*", prefix)))
        .collect(Collectors.toList());
  }

  public static List<InternalOutputPort> getAllMutlipleInternalOutputPorts(
      Task task, String prefix) {
    return task.getOutputPort().stream()
        .filter(
            ip -> ip.getId().matches(String.format("%s [1-9][0-9]*", prefix)))
        .collect(Collectors.toList());
  }
}
