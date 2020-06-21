package org.ruminaq.tasks.javatask.gui.debug;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.DebugElement;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IDisconnect;
import org.eclipse.debug.core.model.IStep;
import org.eclipse.debug.core.model.ISuspendResume;
import org.eclipse.debug.core.model.ITerminate;
import org.ruminaq.debug.model.IState;
import org.ruminaq.debug.model.IStateElement;
import org.ruminaq.debug.model.MainState;

public abstract class JavaTasksDebugElement extends DebugElement
    implements ISuspendResume, IDisconnect, ITerminate, IStep, IStateElement {

  protected IState state = MainState.NOT_STARTED;

  @Override
  public void setState(IState state) {
    this.state = state;
  }

  @Override
  public IState getState() {
    return this.state;
  }

  @Override
  public String getModelIdentifier() {
    return JavaTasksDebugModelPresentation.ID;
  }

  protected JavaTasksDebugElement(IDebugTarget target) {
    super(target);
  }

  @Override
  public JavaTasksDebugTarget getDebugTarget() {
    return (JavaTasksDebugTarget) super.getDebugTarget();
  }

  @Override
  public boolean canTerminate() {
    return false;
  }

  @Override
  public boolean canDisconnect() {
    return false;
  }

  @Override
  public boolean canResume() {
    return false;
  }

  @Override
  public boolean canSuspend() {
    return false;
  }

  @Override
  public boolean canStepInto() {
    return false;
  }

  @Override
  public boolean canStepOver() {
    return false;
  }

  @Override
  public boolean canStepReturn() {
    return false;
  }

  @Override
  public boolean isSuspended() {
    return false;
  }

  @Override
  public boolean isStepping() {
    return false;
  }

  @Override
  public boolean isDisconnected() {
    return false;
  }

  @Override
  public boolean isTerminated() {
    return ((JavaTasksDebugElement) getDebugTarget())
        .getState() == MainState.TERMINATED;
  }

  @Override
  public void resume() throws DebugException {
  }

  @Override
  public void suspend() throws DebugException {
  }

  @Override
  public void disconnect() throws DebugException {
  }

  @Override
  public void stepInto() throws DebugException {
  }

  @Override
  public void stepOver() throws DebugException {
  }

  @Override
  public void stepReturn() throws DebugException {
  }

  @Override
  public void terminate() throws DebugException {
  }
}
