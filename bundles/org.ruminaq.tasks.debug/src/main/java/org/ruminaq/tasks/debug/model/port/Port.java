package org.ruminaq.tasks.debug.model.port;

import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointListener;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IStackFrame;
import org.ruminaq.debug.api.dispatcher.IEventProcessor;
import org.ruminaq.debug.model.DiagramSource;
import org.ruminaq.debug.model.MainState;
import org.ruminaq.logs.ModelerLoggerFactory;
import org.ruminaq.runner.impl.debug.events.AbstractPortEvent;
import org.ruminaq.runner.impl.debug.events.AbstractPortEventListener;
import org.ruminaq.runner.impl.debug.events.IDebugEvent;
import org.ruminaq.runner.impl.debug.events.debugger.ResumedPortEvent;
import org.ruminaq.runner.impl.debug.events.debugger.StartedEvent;
import org.ruminaq.runner.impl.debug.events.debugger.SuspendedPortEvent;
import org.ruminaq.runner.impl.debug.events.debugger.TerminatedEvent;
import org.ruminaq.runner.impl.debug.events.model.PortBreakpointRequest;
import org.ruminaq.runner.impl.debug.events.model.ResumePortRequest;
import org.ruminaq.runner.impl.debug.events.model.SuspendPortRequest;
import org.ruminaq.tasks.debug.model.Task;
import org.ruminaq.tasks.debug.model.TasksDebugElement;
import org.ruminaq.tasks.debug.ui.InternalPortBreakpoint;
import org.ruminaq.tasks.debug.ui.InternalPortBreakpoint.SuspendPolicy;
import ch.qos.logback.classic.Logger;

public abstract class Port extends TasksDebugElement
    implements IStackFrame, DiagramSource, IEventProcessor, IBreakpointListener,
    AbstractPortEventListener {

  private final Logger logger = ModelerLoggerFactory.getLogger(Task.class);

  protected final Task task;
  protected final String id;

  public Task getTask() {
    return task;
  }

  public String getId() {
    return id;
  }

  private List<IBreakpoint> breakpoints = new LinkedList<>();

  public List<IBreakpoint> getBreakpoints() {
    return breakpoints;
  }

  protected boolean dirtyVars = true;

  protected Port(IDebugTarget target, String id, Task task) {
    super(target);
    this.task = task;
    this.id = id;
    setState(MainState.RUNNING);
  }

  @Override
  public IFile getSourceFile() {
    return task.getFile();
  }

  @Override
  public boolean hasVariables() throws DebugException {
    return getVariables().length > 0;
  }

  @Override
  public boolean canResume() {
    return isSuspended();
  }

  @Override
  public boolean canSuspend() {
    return !isSuspended() && !isTerminated();
  }

  @Override
  public boolean canStepOver() {
    return isSuspended();
  }

  @Override
  public boolean isSuspended() {
    return this.state == MainState.SUSPENDED && !isTerminated();
  }

  @Override
  public boolean isStepping() {
    return this.state == MainState.STEPPING && !isTerminated();
  }

  @Override
  public void resume() throws DebugException {
    logger.trace("resume");
    dirtyVars = true;
    getDebugTarget().fireModelEvent(new ResumePortRequest(this));
  }

  @Override
  public void suspend() throws DebugException {
    logger.trace("suspend");
    getDebugTarget().fireModelEvent(new SuspendPortRequest(this));
  }

  @Override
  public void stepOver() {
    getDebugTarget().fireModelEvent(
        new ResumePortRequest(ResumePortRequest.Type.STEP_OVER, this));
  }

  @Override
  public void handleEvent(IDebugEvent event) {
    logger.trace("handleEvent {}", event.getClass().getSimpleName());
    if (event instanceof StartedEvent) {
      DebugPlugin.getDefault().getBreakpointManager()
          .addBreakpointListener(this);
      IBreakpoint[] breakpoints = DebugPlugin.getDefault()
          .getBreakpointManager().getBreakpoints(InternalPortBreakpoint.ID);
      for (IBreakpoint breakpoint : breakpoints)
        breakpointAdded(breakpoint);

    } else if (event instanceof TerminatedEvent)
      DebugPlugin.getDefault().getBreakpointManager()
          .removeBreakpointListener(this);

    if (event instanceof ResumedPortEvent
        && ((AbstractPortEvent) event).compare(this)) {
      if (((ResumedPortEvent) event).getType()
          .equals(ResumedPortEvent.Type.NORMAL)) {
        this.state = MainState.RUNNING;
        fireResumeEvent(DebugEvent.UNSPECIFIED);
      } else if (((ResumedPortEvent) event).getType()
          .equals(ResumedPortEvent.Type.STEP_OVER)) {
        this.state = MainState.STEPPING;
        fireResumeEvent(DebugEvent.STEP_OVER);
      }

    } else if (event instanceof SuspendedPortEvent
        && ((AbstractPortEvent) event).compare(this)) {
      switch (((SuspendedPortEvent) event).getType()) {
        case CLIENT:
          this.state = MainState.SUSPENDED;
          break;
        case STEP_OVER:
          this.state = MainState.SUSPENDED;
          break;
        case BREAKPOINT:
          this.state = MainState.SUSPENDED;
          break;
        default:
          this.state = MainState.SUSPENDED;
          break;
      }
      fireSuspendEvent(((SuspendedPortEvent) event).getType().getDebugType());
      suspended();
    }
  }

  protected abstract void suspended();

  private boolean supportsBreakpoint(InternalPortBreakpoint bp) {
    logger.trace("task.getParentPath() : {}", task.getParentPath());
    logger.trace("bp.getDiagramPath()  : {}", bp.getDiagramPath());
    logger.trace("task.getId() : {}, bp.getTaskId() : {}", task.getId(),
        bp.getTaskId());
    logger.trace("id  : {}, bp.getPortId()) : {}", id, bp.getPortId());
    return Paths.get(task.getParentPath())
        .equals(Paths.get(bp.getDiagramPath()))
        && task.getId().equals(bp.getTaskId()) && id.equals(bp.getPortId());
  }

  @Override
  public void breakpointAdded(IBreakpoint breakpoint) {
    if (breakpoint instanceof InternalPortBreakpoint
        && supportsBreakpoint((InternalPortBreakpoint) breakpoint)
        && getDebugTarget().isEnabledBreakpoint(breakpoint)) {
      InternalPortBreakpoint bp = (InternalPortBreakpoint) breakpoint;
      getDebugTarget().fireModelEvent(new PortBreakpointRequest(
          PortBreakpointRequest.Type.ADDED, bp.getHitCount(),
          bp.getSuspendPolicy() == SuspendPolicy.SUSPEND_RUNNER, this));
      breakpoints.add(breakpoint);
    }
  }

  @Override
  public void breakpointRemoved(final IBreakpoint breakpoint,
      final IMarkerDelta delta) {
    if (breakpoint instanceof InternalPortBreakpoint
        && supportsBreakpoint((InternalPortBreakpoint) breakpoint)) {
      getDebugTarget().fireModelEvent(
          new PortBreakpointRequest(PortBreakpointRequest.Type.REMOVED, this));
      breakpoints.remove(breakpoint);
    }
  }

  @Override
  public void breakpointChanged(final IBreakpoint breakpoint,
      final IMarkerDelta delta) {
    breakpointRemoved(breakpoint, delta);
    breakpointAdded(breakpoint);
  }

  @Override
  public String getDiagramPath() {
    return task.getParentPath();
  }

  @Override
  public String getTaskId() {
    return task.getId();
  }

  @Override
  public String getPortId() {
    return id;
  }
}
