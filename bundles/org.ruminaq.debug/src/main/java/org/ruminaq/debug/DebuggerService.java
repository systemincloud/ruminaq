/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.debug;

import java.rmi.RemoteException;

import org.ruminaq.debug.api.dispatcher.EventDispatchJob;
import org.ruminaq.debug.api.dispatcher.IEventProcessor;
import org.ruminaq.logs.ModelerLoggerFactory;
import org.ruminaq.runner.impl.debug.IDebugIService;
import org.ruminaq.runner.impl.debug.IDebugService;
import org.ruminaq.runner.impl.debug.events.IDebugEvent;

import ch.qos.logback.classic.Logger;

public class DebuggerService implements IDebugService, IEventProcessor {

  private final Logger logger = ModelerLoggerFactory
      .getLogger(DebuggerService.class);

  private EventDispatchJob dispatcher;
  private IDebugIService dis;

  public DebuggerService(IDebugIService dis, EventDispatchJob dispatcher) {
    this.dis = dis;
    this.dispatcher = dispatcher;
  }

  @Override
  public void debuggerEvent(IDebugEvent event) throws RemoteException {
    dispatcher.addEvent(event);
  }

  @Override
  public void handleEvent(IDebugEvent event) {
    logger.trace("handleEvent {}", event);
    try {
      dis.modelEvent(event);
    } catch (RemoteException e) {
    }
  }
}
