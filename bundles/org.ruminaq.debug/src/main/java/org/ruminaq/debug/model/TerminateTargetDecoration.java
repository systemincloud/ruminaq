/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.debug.model;

import org.ruminaq.debug.api.dispatcher.EventDispatchJob;
import org.ruminaq.debug.api.dispatcher.IEventProcessor;
import org.ruminaq.runner.impl.debug.events.IDebugEvent;
import org.ruminaq.runner.impl.debug.events.debugger.StartedEvent;
import org.ruminaq.runner.impl.debug.events.debugger.TerminatedEvent;

public class TerminateTargetDecoration implements IEventProcessor {

  private ISicTarget       target;
  private EventDispatchJob dispatcher;

  public TerminateTargetDecoration(ISicTarget target, EventDispatchJob dispatcher) {
    this.target     = target;
    this.dispatcher = dispatcher;
  }

  @Override
  public void handleEvent(IDebugEvent event) {
       target.handleEvent(event);
       if(event instanceof StartedEvent) target.setState(MainState.RUNNING);
       else if(event instanceof TerminatedEvent) {
         target.setState(MainState.TERMINATED);
         if(!dispatcher.isTerminated()) dispatcher.terminate();
         target.fireTerminateEvent();
       }
  }
}
