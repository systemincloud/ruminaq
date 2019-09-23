package org.ruminaq.tasks.debug.model;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.ruminaq.debug.api.dispatcher.IEventProcessor;
import org.ruminaq.debug.model.MainState;
import org.ruminaq.logs.ModelerLoggerFactory;
import org.ruminaq.runner.impl.debug.events.IDebugEvent;
import org.ruminaq.runner.impl.debug.events.debugger.NewInputPort;
import org.ruminaq.runner.impl.debug.events.debugger.NewOutputPort;
import org.ruminaq.runner.impl.debug.events.debugger.NewTaskEvent;
import org.ruminaq.tasks.debug.model.port.Port;
import org.ruminaq.tasks.debug.model.port.in.InputPort;
import org.ruminaq.tasks.debug.model.port.out.OutputPort;
import org.slf4j.Logger;

public class Task extends TasksDebugElement
    implements IThread, IEventProcessor {

  private final Logger logger = ModelerLoggerFactory.getLogger(Task.class);

  private String id;
  private String name;
  private String parentPath;
  private IFile file;

  public String getParentPath() {
    return parentPath;
  }

  public String getId() {
    return id;
  }

  private final List<Port> ports = new LinkedList<>();

  protected Task(IDebugTarget target, IProject project, NewTaskEvent event) {
    super(target);
    this.id = event.getId();
    this.name = event.getFullId();
    this.parentPath = event.getParentPath();
    for (IProject p : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
      String prefix = p.getLocation().removeLastSegments(1).toFile()
          .getAbsolutePath();
      if (parentPath.startsWith(prefix + File.separator)) {
        logger.trace("prefix : ", prefix);
        logger.trace("parentPath : ", parentPath);
        this.file = ResourcesPlugin.getWorkspace().getRoot()
            .getFile(new Path(parentPath.replace(prefix, "")));
        break;
      }
    }

    setState(MainState.RUNNING);

    for (NewInputPort np : event.getInputPorts())
      ports.add(new InputPort(target, np.getId(), this));
    for (NewOutputPort np : event.getOutputPorts())
      ports.add(new OutputPort(target, np.getId(), this));
  }

  @Override
  public boolean canResume() {
    for (Port p : ports)
      if (p.canResume())
        return true;
    return false;
  }

  @Override
  public boolean canSuspend() {
    for (Port p : ports)
      if (p.canSuspend())
        return true;
    return false;
  }

  @Override
  public boolean isSuspended() {
    if (ports.size() == 0)
      return false;
    for (Port p : ports)
      if (!p.isSuspended())
        return false;
    return true;
  }

  public boolean hasSuspended() {
    if (ports.size() == 0)
      return false;
    for (Port p : ports)
      if (p.isSuspended())
        return true;
    return false;
  }

  @Override
  public boolean canStepOver() {
    if (ports.size() == 0)
      return false;
    for (Port p : ports)
      if (p.canStepOver())
        return true;
    return false;
  }

  @Override
  public void stepOver() {
    for (Port p : ports)
      if (p.canStepOver())
        p.stepOver();
  }

  @Override
  public boolean isStepping() {
    if (ports.size() == 0)
      return false;
    for (Port p : ports)
      if (p.isStepping())
        return true;
    return false;
  }

  @Override
  public void resume() throws DebugException {
    logger.trace("resume");
    for (Port p : ports)
      if (p.canResume())
        p.resume();
  }

  @Override
  public void suspend() throws DebugException {
    logger.trace("suspend");
    for (Port p : ports)
      if (p.canSuspend())
        p.suspend();
  }

  @Override
  public String getName() throws DebugException {
    return name;
  }

  @Override
  public int getPriority() throws DebugException {
    return 0;
  }

  @Override
  public IStackFrame[] getStackFrames() throws DebugException {
    return ports.toArray(new IStackFrame[ports.size()]);
  }

  @Override
  public IStackFrame getTopStackFrame() throws DebugException {
    return !ports.isEmpty() ? ports.get(0) : null;
  }

  @Override
  public boolean hasStackFrames() throws DebugException {
    return !ports.isEmpty();
  }

  @Override
  public IBreakpoint[] getBreakpoints() {
    List<IBreakpoint> ret = new LinkedList<>();
    for (Port p : ports)
      ret.addAll(p.getBreakpoints());
    return ret.toArray(new IBreakpoint[ret.size()]);
  }

  public IFile getFile() {
    return file;
  }

  @Override
  public void handleEvent(IDebugEvent event) {
    for (Port p : ports)
      p.handleEvent(event);
  }
}
