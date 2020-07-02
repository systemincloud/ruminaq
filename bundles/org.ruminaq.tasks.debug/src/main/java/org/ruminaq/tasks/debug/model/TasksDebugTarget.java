/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.debug.model;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IThread;
import org.ruminaq.debug.api.dispatcher.EventDispatchJob;
import org.ruminaq.debug.model.ISicTarget;
import org.ruminaq.debug.model.IState;
import org.ruminaq.debug.model.TerminateTargetDecoration;
import org.ruminaq.logs.ModelerLoggerFactory;
import org.ruminaq.runner.impl.debug.events.IDebugEvent;
import org.ruminaq.runner.impl.debug.events.debugger.NewTaskEvent;
import org.ruminaq.runner.impl.debug.events.debugger.StartedEvent;
import ch.qos.logback.classic.Logger;

public class TasksDebugTarget extends TasksDebugElement
    implements IDebugTarget, ISicTarget {

  private final Logger logger = ModelerLoggerFactory
      .getLogger(TasksDebugTarget.class);

  protected EventDispatchJob dispatcher;
  protected ILaunch launch;
  protected IProject project;
  protected TasksProcess process;
  protected List<Task> tasks = Collections
      .synchronizedList(new LinkedList<Task>());

  @Override
  public TasksDebugTarget getDebugTarget() {
    return this;
  }

  @Override
  public void setState(IState state) {
    super.setState(state);
  }

  public TasksDebugTarget(ILaunch launch, IProject project,
      EventDispatchJob dispatcher) {
    super(null);
    this.launch = launch;
    this.project = project;
    this.dispatcher = dispatcher;
    this.process = new TasksProcess(this);

    dispatcher.addHost(new TerminateTargetDecoration(this, dispatcher));
  }

  @Override
  public boolean canResume() {
    for (Task t : tasks)
      if (t.canResume())
        return true;
    return false;
  }

  @Override
  public boolean canSuspend() {
    for (Task t : tasks)
      if (t.canSuspend())
        return true;
    return false;
  }

  @Override
  public boolean isSuspended() {
    if (tasks.size() == 0)
      return false;
    for (Task t : tasks)
      if (!t.isSuspended())
        return false;
    return true;
  }

  public boolean hasSuspended() {
    if (tasks.size() == 0)
      return false;
    for (Task t : tasks)
      if (t.hasSuspended())
        return true;
    return false;
  }

  @Override
  public boolean canStepOver() {
    if (tasks.size() == 0)
      return false;
    for (Task t : tasks)
      if (t.canStepOver())
        return true;
    return false;
  }

  @Override
  public void resume() throws DebugException {
    logger.trace("resume");
    for (Task t : tasks)
      if (t.canResume())
        t.resume();
  }

  @Override
  public void suspend() throws DebugException {
    logger.trace("suspend");
    for (Task t : tasks)
      if (t.canSuspend())
        t.suspend();
  }

  @Override
  public void stepOver() {
    for (Task t : tasks)
      if (t.canStepOver())
        t.stepOver();
  }

  @Override
  public void handleEvent(IDebugEvent event) {
    logger.trace("handleEvent() {}", event);
    if (event instanceof NewTaskEvent) {
      Task t = new Task(this, project, (NewTaskEvent) event);
      tasks.add(t);
      t.fireCreationEvent();
    } else {
      for (Task t : tasks)
        t.handleEvent(event);
    }

    if (event instanceof StartedEvent)
      try {
        resume();
      } catch (DebugException e) {
      }
  }

  public void fireModelEvent(IDebugEvent event) {
    dispatcher.addEvent(event);
  }

  @Override
  public String getName() {
    return "Tasks";
  }

  @Override
  public ILaunch getLaunch() {
    return launch;
  }

  @Override
  public IProcess getProcess() {
    return process;
  }

  @Override
  public boolean hasThreads() throws DebugException {
    return !tasks.isEmpty();
  }

  @Override
  public IThread[] getThreads() throws DebugException {
    Collections.sort(tasks, new Comparator<Task>() {
      @Override
      public int compare(Task o1, Task o2) {
        try {
          return Collator.getInstance().compare(o1.getName(), o2.getName());
        } catch (DebugException e) {
        }
        return 0;
      }
    });
    return tasks.toArray(new IThread[tasks.size()]);
  }

  @Override
  public boolean supportsBreakpoint(IBreakpoint breakpoint) {
    return true;
  }

  @Override
  public void breakpointAdded(IBreakpoint breakpoint) {
  }

  @Override
  public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta) {
  }

  @Override
  public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta) {
  }

  public boolean isEnabledBreakpoint(IBreakpoint breakpoint) {
    try {
      return breakpoint.isEnabled()
          && DebugPlugin.getDefault().getBreakpointManager().isEnabled();
    } catch (CoreException e) {
    }
    return false;
  }

  @Override
  public boolean supportsStorageRetrieval() {
    return false;
  }

  @Override
  public IMemoryBlock getMemoryBlock(long startAddress, long length) {
    return null;
  }
}
