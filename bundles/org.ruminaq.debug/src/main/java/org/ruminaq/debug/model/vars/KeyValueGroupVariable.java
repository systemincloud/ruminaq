/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

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

  public KeyValueGroupVariable(IDebugTarget target, String name,
      IStateElement se) {
    super(target, name, "");
    this.se = se;
  }

  @Override
  public boolean hasValueChanged() {
    return false;
  }

  @Override
  public String getValueString() {
    return "";
  }

  @Override
  public IVariable[] getVariables() {
    return map.values().toArray(new IVariable[map.values().size()]);
  }

  @Override
  public boolean hasVariables() {
    return !map.isEmpty();
  }

  public void add(String key, String value, boolean editable, IState showWhen) {
    map.put(key, new StringVariable(getDebugTarget(), key, value, editable, se,
        showWhen));
  }

  public void clear() {
    map.clear();
  }
}
