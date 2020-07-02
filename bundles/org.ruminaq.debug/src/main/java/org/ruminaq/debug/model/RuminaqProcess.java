/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.debug.model;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamsProxy;

public class RuminaqProcess extends RuminaqDebugElement implements IProcess {

  public RuminaqProcess(final RuminaqDebugTarget debugTarget) {
    super(debugTarget);
  }

  @Override
  public String getAttribute(String paramString) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int getExitValue() throws DebugException {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public String getLabel() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public IStreamsProxy getStreamsProxy() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setAttribute(String paramString1, String paramString2) {
    // TODO Auto-generated method stub

  }
}
