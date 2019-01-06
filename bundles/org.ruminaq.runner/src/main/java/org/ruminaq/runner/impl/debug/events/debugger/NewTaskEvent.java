package org.ruminaq.runner.impl.debug.events.debugger;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.ruminaq.runner.impl.EmbeddedTaskI;
import org.ruminaq.runner.impl.InternalInputPortI;
import org.ruminaq.runner.impl.InternalOutputPortI;
import org.ruminaq.runner.impl.TaskI;
import org.ruminaq.runner.impl.debug.events.AbstractEvent;
import org.ruminaq.runner.impl.debug.events.IDebuggerEvent;

public final class NewTaskEvent extends AbstractEvent implements IDebuggerEvent {

	private static final long serialVersionUID = 1L;

	private static final String DELIMITER = " " + Character.toString ((char) 1633) + " ";

	private transient TaskI taskI;

	private String id;
	private String fullId;
	private String parentPath;

	private List<NewInputPort>  inPorts = new LinkedList<>();
	private List<NewOutputPort> outPorts = new LinkedList<>();

	public String getId()     { return this.id; }
	public String getFullId() { return this.fullId; }

	public String              getParentPath()  { return parentPath; }
	public List<NewInputPort>  getInputPorts()  { return inPorts; }
	public List<NewOutputPort> getOutputPorts() { return outPorts; }

	public NewTaskEvent(TaskI taskI, String parentPath) {
		this.taskI      = taskI;
		this.parentPath = parentPath;
	}

	@Override
	public void preevaluate() {
		this.id = taskI.getId();
		LinkedList<String> names = new LinkedList<>();
		EmbeddedTaskI parent = taskI.getParent();
		while(parent.getParent().getParent() != null) {
			names.addFirst(parent.getId());
			parent = parent.getParent();
		}
		this.fullId = "";
		if(names.size() > 0) {
			this.fullId = StringUtils.join(names, DELIMITER);
			this.fullId += DELIMITER;
		}
		this.fullId += this.id;
		this.fullId += " [" + taskI.getModel().getClass().getInterfaces()[0].getSimpleName() + "]";

		for(InternalInputPortI  p : taskI.getInternalInputPorts())  inPorts .add(new NewInputPort (p.getPortId()));
		for(InternalOutputPortI p : taskI.getInternalOutputPorts()) outPorts.add(new NewOutputPort(p.getPortId()));
	}
}
