package org.ruminaq.gui;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.model.ruminaq.InternalInputPort;
import org.ruminaq.model.ruminaq.InternalOutputPort;
import org.ruminaq.model.ruminaq.Task;

public class TasksUtil {

  public static boolean isMultiplePortId(String id, String prefix) {
    return id.startsWith(prefix) && id.length() > prefix.length() + 1
        && StringUtils.isNumeric(id.substring(prefix.length() + 1));
  }

  public static boolean isMultiplePortId(String id, PortsDescr pd) {
    return isMultiplePortId(id, pd.getId());
  }

  public static int getMultiplePortIdIdx(String id, PortsDescr pd) {
    return Integer
        .parseInt(id.substring(pd.getId().length() + 1));
  }

  public static List<InternalInputPort> getAllMutlipleInternalInputPorts(
      Task task, String prefix) {
    List<InternalInputPort> ret = new LinkedList<>();

    for (InternalInputPort iip : task.getInputPort())
      if (isMultiplePortId(iip.getId(), prefix))
        ret.add(iip);

    return ret;
  }

  public static List<InternalOutputPort> getAllMutlipleInternalOutputPorts(
      Task task, String prefix) {
    List<InternalOutputPort> ret = new LinkedList<>();

    for (InternalOutputPort iip : task.getOutputPort())
      if (isMultiplePortId(iip.getId(), prefix))
        ret.add(iip);

    return ret;
  }
}
