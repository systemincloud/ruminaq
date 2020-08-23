/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.debug;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.Breakpoint;
import org.eclipse.debug.core.model.IBreakpoint;

/**
 *
 * @author Marek Jagielski
 */
public class InternalPortBreakpoint extends Breakpoint {

  public static final String ID = "org.ruminaq.debug.internalPortBreakpoint";
  public static final String INTERNAL_PORT_BREAKPOINT = "org.ruminaq.debug.internalPortBreakpointMarker";
  public static final String PATH = "path";
  public static final String TASK_ID = "taskId";
  public static final String PORT_ID = "portId";
  public static final String HIT_COUNT = "hitCount";
  public static final String SUSPEND_POLICY = "suspendPolicy";

  private IMarker marker;

  private String diagramPath;
  private String taskId;
  private String portId;

  public String getDiagramPath() {
    init();
    return diagramPath;
  }

  public String getTaskId() {
    init();
    return taskId;
  }

  public String getPortId() {
    init();
    return portId;
  }

  public enum SuspendPolicy {
    SUSPEND_PORT, SUSPEND_RUNNER
  }

  private int hitCount = -1;
  private SuspendPolicy suspendPolicy = SuspendPolicy.SUSPEND_PORT;

  public int getHitCount() {
    init();
    return hitCount;
  }

  public void setHitCount(int hitCount) {
    this.hitCount = hitCount;
    saveAttribute(HIT_COUNT, Integer.toString(hitCount));
  }

  public SuspendPolicy getSuspendPolicy() {
    init();
    return suspendPolicy;
  }

  public void setSuspendPolicy(SuspendPolicy suspendPolicy) {
    this.suspendPolicy = suspendPolicy;
    saveAttribute(SUSPEND_POLICY, suspendPolicy.toString());
  }

  private void init() {
    if (marker == null) {
      this.marker = getMarker();
      if (marker == null)
        return;

      this.diagramPath = marker.getAttribute(PATH, null);
      this.taskId = marker.getAttribute(TASK_ID, null);
      this.portId = marker.getAttribute(PORT_ID, null);

      try {
        this.hitCount = Integer.parseInt(marker.getAttribute(HIT_COUNT, null));
      } catch (NumberFormatException e) {
        this.hitCount = -1;
      }

      String sp = marker.getAttribute(SUSPEND_POLICY, null);
      if (sp != null)
        this.suspendPolicy = SuspendPolicy.valueOf(sp);
      if (this.suspendPolicy == null)
        this.suspendPolicy = SuspendPolicy.SUSPEND_PORT;
    }
  }

  private void saveAttribute(final String key, final String value) {
    IWorkspace workspace = ResourcesPlugin.getWorkspace();
    IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
      @Override
      public void run(IProgressMonitor monitor) throws CoreException {
        InternalPortBreakpoint.this.ensureMarker().setAttribute(key, value);
      }
    };
    try {
      workspace.run(runnable, getMarkerRule(), 0, null);
    } catch (CoreException e) {
    }
  }

  public InternalPortBreakpoint() {
  }

  public InternalPortBreakpoint(IResource resource, String taskId,
      String portId) throws DebugException {
    this.diagramPath = resource.getRawLocation().toOSString();
    this.taskId = taskId;
    this.portId = portId;
    IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
      @Override
      public void run(IProgressMonitor monitor) throws CoreException {
        InternalPortBreakpoint.this.marker = resource
            .createMarker(INTERNAL_PORT_BREAKPOINT);
        setMarker(marker);
        marker.setAttribute(IBreakpoint.ENABLED, true);
        marker.setAttribute(IBreakpoint.PERSISTED, true);
        marker.setAttribute(IBreakpoint.ID, getModelIdentifier());
        marker.setAttribute(IMarker.LOCATION, taskId + ":" + portId);
        marker.setAttribute(PATH, diagramPath);
        marker.setAttribute(TASK_ID, taskId);
        marker.setAttribute(PORT_ID, portId);
        marker.setAttribute(IMarker.MESSAGE, "InternalPort Breakpoint");
      }
    };
    run(getMarkerRule(resource), runnable);
  }

  @Override
  public String getModelIdentifier() {
    return ID;
  }

}
