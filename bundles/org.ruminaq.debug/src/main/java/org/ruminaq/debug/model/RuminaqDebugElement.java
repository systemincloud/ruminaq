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
import org.eclipse.debug.core.model.DebugElement;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IDisconnect;
import org.eclipse.debug.core.model.IStep;
import org.eclipse.debug.core.model.ISuspendResume;
import org.eclipse.debug.core.model.ITerminate;
import org.ruminaq.runner.impl.debug.events.model.TerminateRequest;

public abstract class RuminaqDebugElement extends DebugElement implements ISuspendResume,
                                                                                IDisconnect,
                                                                                ITerminate,
                                                                                IStep {

	protected IState state = MainState.NOT_STARTED;

	protected void   setState(IState state) {        ((RuminaqDebugElement) getDebugTarget()).state = state; }
	protected IState getState()             { return ((RuminaqDebugElement) getDebugTarget()).state; }

	@Override public String getModelIdentifier() { return SicDebugModelPresentation.ID; }

	protected RuminaqDebugElement(IDebugTarget target) {
		super(target);
	}

	@Override
	public RuminaqDebugTarget getDebugTarget() {
		return (RuminaqDebugTarget) super.getDebugTarget();
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
		return getState() == MainState.TERMINATED;
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
		getDebugTarget().fireModelEvent(new TerminateRequest());
	}
}
