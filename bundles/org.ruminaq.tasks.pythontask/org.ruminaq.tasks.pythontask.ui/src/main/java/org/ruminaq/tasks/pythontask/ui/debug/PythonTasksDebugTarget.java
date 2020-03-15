package org.ruminaq.tasks.pythontask.ui.debug;

import java.util.Collections;
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
import ch.qos.logback.classic.Logger;

public class PythonTasksDebugTarget extends PythonTasksDebugElement
    implements IDebugTarget, ISicTarget {

  private final Logger logger = ModelerLoggerFactory
      .getLogger(PythonTasksDebugTarget.class);

  protected EventDispatchJob dispatcher;
  protected ILaunch launch;
  protected IProject project;
  protected PythonTasksProcess process;
  protected List<PythonTask> tasks = Collections
      .synchronizedList(new LinkedList<PythonTask>());

  @Override
  public PythonTasksDebugTarget getDebugTarget() {
    return this;
  }

  @Override
  public void setState(IState state) {
    super.setState(state);
  }

  public PythonTasksDebugTarget(ILaunch launch, IProject project,
      EventDispatchJob dispatcher) {
    super(null);
    this.launch = launch;
    this.project = project;
    this.dispatcher = dispatcher;
    this.process = new PythonTasksProcess(this);

    dispatcher.addHost(new TerminateTargetDecoration(this, dispatcher));
  }

  public boolean hasSuspended() {
    return false;
  }

  @Override
  public void handleEvent(IDebugEvent event) {
    logger.trace("handleEvent() {}", event);
  }

  public void fireModelEvent(IDebugEvent event) {
    dispatcher.addEvent(event);
  }

  @Override
  public String getName() {
    return "Python Tasks";
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
    return false;
  }

  @Override
  public IThread[] getThreads() throws DebugException {
    return new IThread[0];
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
