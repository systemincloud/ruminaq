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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IVariable;
import org.ruminaq.debug.model.IState;
import org.ruminaq.debug.model.IStateElement;

public class KeyValueGroupVariable extends SicVariable {

	private IStateElement se;

	private Map<String, StringVariable> map = new HashMap<>();

	public KeyValueGroupVariable(IDebugTarget target, String name, IStateElement se) {
		super(target, name, "");
		this.se = se;
	}

	@Override public boolean hasValueChanged() { return false; }

	@Override public String getValueString() { return ""; }

	@Override public IVariable[] getVariables() { return map.values().toArray(new IVariable[map.values().size()]); }
	@Override public boolean     hasVariables() { return !map.isEmpty(); }

	public void add(String key, String value, boolean editable, IState showWhen) { map.put(key, new StringVariable(getDebugTarget(), key, value, editable, se, showWhen)); }
	public void clear()                                                          { map.clear(); }
}
