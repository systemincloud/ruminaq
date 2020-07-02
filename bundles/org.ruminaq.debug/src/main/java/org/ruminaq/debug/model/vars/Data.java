/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.debug.model.vars;

import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IVariable;
import org.ruminaq.debug.model.IStateElement;
import org.ruminaq.debug.model.MainState;
import org.ruminaq.runner.impl.data.DataI;

public class Data extends SicVariable {

  private IStateElement se;
  private DataI data;

  public Data(IDebugTarget target, DataI data, int i) {
    super(target, "[" + i + "]", "");
    this.data = data;
  }

  public Data(IDebugTarget target, DataI data, IStateElement se) {
    super(target, "data", "");
    this.data = data;
    this.se = se;
  }

  @Override
  public String getDetailText() {
    return data == null ? "" : data.toString();
  }

  @Override
  public String getValueString() {
    return se != null && se.getState() != MainState.SUSPENDED
        ? "see only when suspended"
        : data == null ? "no data" : data.toShortString();
  }

  // TODO : if the variable can be split to smaller ones
  @Override
  public IVariable[] getVariables() {
    return new IVariable[0];
  }

  @Override
  public boolean hasVariables() {
    return false;
  }
}
