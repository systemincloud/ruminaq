package org.ruminaq.tasks.util;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.model.desc.PortsDescrUtil;
import org.ruminaq.model.ruminaq.InternalInputPort;
import org.ruminaq.model.ruminaq.InternalOutputPort;
import org.ruminaq.model.ruminaq.InternalPort;
import org.ruminaq.model.ruminaq.Task;

public class TasksUtil {

	public static boolean isMultiplePortId(String id, String prefix) {
		return id.startsWith(prefix) && id.length() > prefix.length() + 1 && StringUtils.isNumeric(id.substring(prefix.length() + 1));
	}

	public static boolean isMultiplePortId(String id, PortsDescr pd) {
		return isMultiplePortId(id, PortsDescrUtil.getName(pd));
	}

	public static int getMultiplePortIdIdx(String id, PortsDescr pd) {
		return Integer.parseInt(id.substring(PortsDescrUtil.getName(pd).length() + 1));
	}

	public static List<InternalInputPort> getAllMutlipleInternalInputPorts(Task task, String prefix) {
		List<InternalInputPort> ret = new LinkedList<>();

		for(InternalInputPort iip : task.getInputPort())
			if(isMultiplePortId(iip.getId(), prefix)) ret.add(iip);

		return ret;
	}

	public static InternalPort getInternalPort(Task task, String name) {
		for(InternalInputPort iip : task.getInputPort())
			if(iip.getId().equals(name)) return iip;
		for(InternalOutputPort iop : task.getOutputPort())
			if(iop.getId().equals(name)) return iop;
		return null;
	}

	public static List<InternalOutputPort> getAllMutlipleInternalOutputPorts(Task task, String prefix) {
		List<InternalOutputPort> ret = new LinkedList<>();

		for(InternalOutputPort iip : task.getOutputPort())
			if(isMultiplePortId(iip.getId(), prefix)) ret.add(iip);

		return ret;
	}
}
