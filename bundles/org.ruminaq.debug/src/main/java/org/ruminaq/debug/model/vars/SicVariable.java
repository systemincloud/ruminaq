/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.debug.model.vars;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

public abstract class SicVariable extends PlatformObject implements IVariable, IValue {

  private final IDebugTarget target;
  private final String       name;
  private final String       type;

  @Override public IDebugTarget getDebugTarget()       { return target; }
  @Override public ILaunch      getLaunch()            { return target.getLaunch(); }
  @Override public String       getModelIdentifier()   { return target.getModelIdentifier(); }
  @Override public String       getName()              { return name; }
  @Override public String       getReferenceTypeName() { return type; }

  @Override public IValue  getValue()                  { return this; }
  @Override public boolean supportsValueModification() { return false; }
  @Override public void    setValue(IValue value)      { }
  @Override public void    setValue(String value)      { }
  @Override public boolean hasValueChanged()           { return false; }

  @Override public boolean verifyValue(String value) { return false; }
  @Override public boolean verifyValue(IValue value) { return false; }

  @Override public IVariable[] getVariables() { return new IVariable[0]; }
  @Override public boolean     hasVariables() { return false; }

  @Override public boolean isAllocated() { return true; }

  public SicVariable(IDebugTarget target, String name, String type) {
    this.target = target;
    this.name   = name;
    this.type   = type;
  }

  public String getDetailText() {
    try { return getValueString();
    } catch (DebugException e) { return ""; }
  }
}
