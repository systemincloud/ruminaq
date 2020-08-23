/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.pythontask.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.debug.api.DebugExtension;
import org.ruminaq.debug.api.EventDispatchJob;
import org.ruminaq.tasks.pythontask.ui.debug.PythonTasksDebugTarget;

@Component(immediate = true)
public class PythonTasksDebugModelerExtension implements DebugExtension {

  @Override
  public Collection<? extends IDebugTarget> getDebugTargets(ILaunch launch,
      IProject project, EventDispatchJob dispatcher) {
    List<IDebugTarget> targets = new ArrayList<>();
    targets.add(new PythonTasksDebugTarget(launch, project, dispatcher));
    return targets;
  }
}
