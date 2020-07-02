/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.runner.impl.debug.events.debugger;

import org.ruminaq.model.ruminaq.InternalPort;
import org.ruminaq.runner.impl.SynchronizationI;
import org.ruminaq.runner.impl.debug.events.AbstractPortEvent;
import org.ruminaq.runner.impl.debug.events.AbstractPortEventListener;
import org.ruminaq.runner.impl.debug.events.IDebuggerEvent;

public class WaitingForEvent extends AbstractPortEvent
    implements IDebuggerEvent {

  private static final long serialVersionUID = 1L;

  private boolean waiting;
  private String waitingFor;

  public boolean isWaiting() {
    return waiting;
  }

  public String getWaitingFor() {
    return waitingFor;
  }

  public WaitingForEvent(boolean waiting, SynchronizationI s,
      AbstractPortEventListener apel) {
    super(apel);
    this.waiting = waiting;
    if (s != null) {
      InternalPort ip = s.getSyncPort();
      if (ip != null)
        this.waitingFor = ip.getId();
    }
  }

  @Override
  public void preevaluate() {
  }
}
