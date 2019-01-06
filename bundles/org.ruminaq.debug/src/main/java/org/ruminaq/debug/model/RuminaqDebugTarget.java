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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IThread;
import org.ruminaq.debug.api.dispatcher.EventDispatchJob;
import org.ruminaq.logs.ModelerLoggerFactory;
import org.ruminaq.runner.impl.debug.events.IDebugEvent;
import org.ruminaq.runner.impl.debug.events.debugger.ResumedEvent;
import org.ruminaq.runner.impl.debug.events.debugger.SuspendedEvent;
import org.slf4j.Logger;

public class RuminaqDebugTarget extends RuminaqDebugElement implements IDebugTarget, ISicTarget {

	private final Logger logger = ModelerLoggerFactory.getLogger(RuminaqDebugTarget.class);

	private EventDispatchJob dispatcher;
	private ILaunch launch;
	private IFile mainFile;
	private RuminaqProcess process;
	private MainLoop mainLoop;

	@Override
	public RuminaqDebugTarget getDebugTarget() {
		return this;
	}

	@Override
	public void setState(IState state) {
		super.setState(state);
	}

	public RuminaqDebugTarget(ILaunch launch, IFile mainFile, EventDispatchJob dispatcher) {
		super(null);
		this.launch     = launch;
		this.mainFile   = mainFile;
		this.dispatcher = dispatcher;
		this.process    = new RuminaqProcess(this);
		this.mainLoop   = new MainLoop(this);

		dispatcher.addHost(new TerminateTargetDecoration(this, dispatcher));
	}

	@Override
	public void handleEvent(IDebugEvent event) {
		logger.trace("handleEvent() {}", event);
		if(event instanceof SuspendedEvent) {
		} else if(event instanceof ResumedEvent) {
		}
	}

	public void fireModelEvent(IDebugEvent event) { dispatcher.addEvent(event); }

	@Override
	public String getName() {
		return "Engine";
	}

	@Override
	public ILaunch getLaunch() {
		return launch;
	}

	@Override
	public IProcess getProcess() {
		return process;
	}

	@Override
	public boolean hasThreads() throws DebugException {
		return true;
	}

	@Override
	public IThread[] getThreads() throws DebugException {
		return new IThread[] { mainLoop };
	}

	@Override
	public boolean supportsBreakpoint(IBreakpoint breakpoint) {
		return false;
	}

	@Override
	public void breakpointAdded(IBreakpoint breakpoint) {
	}

	@Override
	public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta) {
	}

	@Override
	public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta) {
	}

	@Override
	public boolean supportsStorageRetrieval() {
		return false;
	}

	@Override
	public IMemoryBlock getMemoryBlock(long startAddress, long length) {
		return null;
	}

	public IFile getFile() { return mainFile; }
}
