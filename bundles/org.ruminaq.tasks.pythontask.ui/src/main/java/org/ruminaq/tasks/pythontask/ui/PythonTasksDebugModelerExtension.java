package org.ruminaq.tasks.pythontask.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.ruminaq.debug.api.dispatcher.EventDispatchJob;
import org.ruminaq.tasks.pythontask.ui.debug.PythonTasksDebugTarget;

public class PythonTasksDebugModelerExtension implements ModelerDebugExtension {

	@Override
	public Collection<? extends IDebugTarget> getDebugTargets(ILaunch launch, IProject project, EventDispatchJob dispatcher) {
		List<IDebugTarget> targets = new ArrayList<>();
    	targets.add(new PythonTasksDebugTarget(launch, project, dispatcher));
		return targets;
	}
}
