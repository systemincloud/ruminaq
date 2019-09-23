/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.debug.model;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.ruminaq.logs.ModelerLoggerFactory;
import org.slf4j.Logger;

public class MainLoop extends RuminaqDebugElement implements IThread {

  private final Logger logger = ModelerLoggerFactory.getLogger(MainLoop.class);

  protected MainLoop(IDebugTarget target) {
    super(target);
  }

  @Override
  public boolean canResume() {
    return this.state == MainState.SUSPENDED;
  }

  @Override
  public boolean canSuspend() {
    return this.state != MainState.SUSPENDED
        && this.state != MainState.TERMINATED;
  }

  @Override
  public void resume() throws DebugException {
    logger.trace("resume");
    System.out.println("MainLoop : resume");
  }

  @Override
  public void suspend() throws DebugException {
    logger.trace("suspend");
    System.out.println("MainLoop : suspend");
  }

  @Override
  public IBreakpoint[] getBreakpoints() {
    return null;
  }

  @Override
  public String getName() throws DebugException {
    return "main loop";
  }

  @Override
  public int getPriority() throws DebugException {
    return 0;
  }

  @Override
  public IStackFrame[] getStackFrames() throws DebugException {
    return new IStackFrame[] { new Stats(getDebugTarget(), this) };
  }

  @Override
  public IStackFrame getTopStackFrame() throws DebugException {
    return null;
  }

  @Override
  public boolean hasStackFrames() throws DebugException {
    return true;
  }
}
