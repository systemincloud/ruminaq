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

	@Override public boolean canResume()  { return this.state == MainState.SUSPENDED; }
	@Override public boolean canSuspend() { return this.state != MainState.SUSPENDED && this.state != MainState.TERMINATED; }

	@Override public void resume() throws DebugException {
		logger.trace("resume");
		System.out.println("MainLoop : resume");
	}

	@Override public void suspend() throws DebugException {
		logger.trace("suspend");
		System.out.println("MainLoop : suspend");
	}

	@Override public IBreakpoint[] getBreakpoints() { return null; }

	@Override public String        getName()        throws DebugException { return "main loop"; }
	@Override public int           getPriority()    throws DebugException { return 0; }
	@Override public IStackFrame[] getStackFrames() throws DebugException {
		return new IStackFrame[] { new Stats(getDebugTarget(), this)};
	}

	@Override
	public IStackFrame getTopStackFrame() throws DebugException {
		return null;
	}

	@Override public boolean hasStackFrames() throws DebugException { return true; }
}
