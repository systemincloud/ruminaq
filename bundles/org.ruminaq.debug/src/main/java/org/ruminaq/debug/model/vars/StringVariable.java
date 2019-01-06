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
import org.ruminaq.debug.model.IState;
import org.ruminaq.debug.model.IStateElement;

public class StringVariable extends SicVariable {

	private IStateElement se;
	private IState        showWhen;

    protected String type;
    protected String value;

	private boolean editable;

	public StringVariable(IDebugTarget target, String name, String value, boolean editable, IStateElement se, IState showWhen) {
		super(target, name, "StringType");
		this.editable = editable;
		this.se       = se;
		this.showWhen = showWhen;
		setValue(value);
	}

	@Override public void    setValue(String value)      { this.value = value; }
	@Override public boolean supportsValueModification() { return editable; }
	@Override public String  getValueString()            { return showWhen == null ? value :
		                                                                             se.getState().equals(showWhen) ? value :
		                                                                            	                              "see only when " + showWhen.toString(); }
}
