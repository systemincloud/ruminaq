/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.rtask.ui.debug;

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

public abstract class RTasksDebugElement extends DebugElement
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
    return RTasksDebugModelPresentation.ID;
  }

  protected RTasksDebugElement(IDebugTarget target) {
    super(target);
  }

  @Override
  public RTasksDebugTarget getDebugTarget() {
    return (RTasksDebugTarget) super.getDebugTarget();
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
    return ((RTasksDebugElement) getDebugTarget())
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
