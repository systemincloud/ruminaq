/*
 * (C) Copyright 2018 Marek Jagielski.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
