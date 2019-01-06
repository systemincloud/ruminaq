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
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IVariable;

public class Stats extends RuminaqDebugElement implements IStackFrame, DiagramSource {

	private final IThread thread;

	public Stats(IDebugTarget target, IThread thread) {
		super(target);
		this.thread = thread;
	}

	@Override public IThread getThread()     { return thread; }
	@Override public int     getLineNumber() { return  0; }
	@Override public int     getCharStart()  { return -1; }
	@Override public int     getCharEnd()    { return -1; }
	@Override public String  getName()       { return "Stats"; }

	@Override public IFile getSourceFile() { return getDebugTarget().getFile(); }

	@Override
	public IVariable[] getVariables() {
		return new IVariable[0];
	}

	@Override
	public boolean hasVariables() {
		return getVariables().length > 0;
	}

	@Override
	public IRegisterGroup[] getRegisterGroups() {
		return new IRegisterGroup[0];
	}

	@Override
	public boolean hasRegisterGroups() {
		return getRegisterGroups().length > 0;
	}

}
