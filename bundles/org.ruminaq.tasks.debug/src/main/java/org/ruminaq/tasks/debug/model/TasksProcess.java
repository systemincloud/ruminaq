package org.ruminaq.tasks.debug.model;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamsProxy;

public class TasksProcess extends TasksDebugElement implements IProcess {

//	private TasksDebugTarget target;
	
	public TasksProcess(final TasksDebugTarget target) {
		super(target);
//		this.target = target;
	}

	@Override
	public String getAttribute(String paramString) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getExitValue() throws DebugException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStreamsProxy getStreamsProxy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAttribute(String paramString1, String paramString2) {
		// TODO Auto-generated method stub
		
	}
}
