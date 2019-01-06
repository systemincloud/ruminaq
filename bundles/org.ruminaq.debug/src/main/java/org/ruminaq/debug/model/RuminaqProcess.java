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
