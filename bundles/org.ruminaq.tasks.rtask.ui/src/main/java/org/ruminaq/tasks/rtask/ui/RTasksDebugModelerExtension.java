package org.ruminaq.tasks.rtask.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.ruminaq.debug.extension.ModelerDebugExtension;
import org.ruminaq.tasks.api.dispatcher.EventDispatchJob;
import org.ruminaq.tasks.rtask.ui.debug.RTasksDebugTarget;

public class RTasksDebugModelerExtension implements ModelerDebugExtension {

    @Override
    public Collection<? extends IDebugTarget> getDebugTargets(ILaunch launch, IProject project, EventDispatchJob dispatcher) {
        List<IDebugTarget> targets = new ArrayList<>();
        targets.add(new RTasksDebugTarget(launch, project, dispatcher));
        return targets;
    }
}
