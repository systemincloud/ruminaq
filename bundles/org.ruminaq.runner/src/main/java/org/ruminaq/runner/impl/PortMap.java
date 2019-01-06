package org.ruminaq.runner.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.model.desc.PortsDescrUtil;
import org.ruminaq.runner.impl.data.DataI;

public class PortMap extends HashMap<String, DataI> {

	private static final long serialVersionUID = 1L;

	public DataI get(PortsDescr portDesc) {
		return this.get(PortsDescrUtil.getName(portDesc));
	}

	public List<DataI> getAll(PortsDescr portDesc) {
		String baseName = PortsDescrUtil.getName(portDesc);
		List<String> portIds = new LinkedList<>();
		for(String portId : this.keySet()) {
			if(portId.startsWith(baseName + " ")) {
				try {
					Integer.parseInt(portId.substring(baseName.length() + 1));
					portIds.add(portId);
				} catch(Exception e) { }
			}
		}
		List<DataI> ret = new LinkedList<DataI>();
		for(String portId : portIds) ret.add(this.get(portId));
		return ret;
	}
}
