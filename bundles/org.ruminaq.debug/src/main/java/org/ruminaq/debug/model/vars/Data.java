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
package org.ruminaq.debug.model.vars;

import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IVariable;
import org.ruminaq.debug.model.IStateElement;
import org.ruminaq.debug.model.MainState;
import org.ruminaq.runner.impl.data.DataI;

public class Data extends SicVariable {

	private IStateElement se;
	private DataI         data;

	public Data(IDebugTarget target, DataI data, int i) {
		super(target, "[" + i + "]", "");
		this.data = data;
	}

	public Data(IDebugTarget target, DataI data, IStateElement se) {
		super(target, "data", "");
		this.data = data;
		this.se   = se;
	}

	@Override public String getDetailText()  { return data == null ? "" : data.toString(); }
	@Override public String getValueString() { return se != null && se.getState() != MainState.SUSPENDED ? "see only when suspended" : data == null ? "no data" : data.toShortString(); }

	// TODO : if the variable can be split to smaller ones
	@Override public IVariable[] getVariables() { return new IVariable[0]; }
	@Override public boolean     hasVariables() { return false; }
}
