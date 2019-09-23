package org.ruminaq.tasks.debug;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.debug.api.DebugExtension;
import org.ruminaq.debug.api.dispatcher.EventDispatchJob;
import org.ruminaq.tasks.debug.model.TasksDebugTarget;

@Component
public class TasksDebugModelerExtension implements DebugExtension {

  @Override
  public Collection<? extends IDebugTarget> getDebugTargets(ILaunch launch,
      IProject project, EventDispatchJob dispatcher) {
    List<IDebugTarget> targets = new ArrayList<>();
    targets.add(new TasksDebugTarget(launch, project, dispatcher));
    return targets;
  }
}
