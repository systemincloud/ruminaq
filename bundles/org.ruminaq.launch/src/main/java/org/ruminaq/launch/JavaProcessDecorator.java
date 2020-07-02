/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.launch;

import java.rmi.RemoteException;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.ruminaq.runner.dirmi.DirmiServer;
import org.ruminaq.runner.impl.debug.IDebugIService;
import org.ruminaq.runner.impl.debug.events.model.TerminateRequest;

public class JavaProcessDecorator implements IProcess {

  private IProcess p;

  public JavaProcessDecorator(IProcess p) {
    this.p = p;
  }

  private boolean sigkill = false;

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public Object getAdapter(Class arg) {
    return p.getAdapter(arg);
  }

  @Override
  public boolean canTerminate() {
    return p.canTerminate();
  }

  @Override
  public boolean isTerminated() {
    return p.isTerminated();
  }

  @Override
  public String getAttribute(String s) {
    return p.getAttribute(s);
  }

  @Override
  public int getExitValue() throws DebugException {
    return p.getExitValue();
  }

  @Override
  public String getLabel() {
    return p.getLabel();
  }

  @Override
  public ILaunch getLaunch() {
    return p.getLaunch();
  }

  @Override
  public IStreamsProxy getStreamsProxy() {
    return p.getStreamsProxy();
  }

  @Override
  public void setAttribute(String s1, String s2) {
    p.setAttribute(s1, s2);
  }

  @Override
  public void terminate() throws DebugException {
    if (!sigkill) {
      try {
        IDebugIService cs = DirmiServer.INSTANCE.getRemote("main",
            IDebugIService.class);
        if (cs != null)
          cs.modelEvent(new TerminateRequest());
      } catch (RemoteException e) {
      }
      this.sigkill = true;
    } else
      p.terminate();
  }
}
