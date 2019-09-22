/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.debug.model.vars;

import org.eclipse.debug.core.model.IDebugTarget;
import org.ruminaq.debug.model.IState;
import org.ruminaq.debug.model.IStateElement;

public class StringVariable extends SicVariable {

  private IStateElement se;
  private IState showWhen;

  protected String type;
  protected String value;

  private boolean editable;

  public StringVariable(IDebugTarget target, String name, String value,
      boolean editable, IStateElement se, IState showWhen) {
    super(target, name, "StringType");
    this.editable = editable;
    this.se = se;
    this.showWhen = showWhen;
    setValue(value);
  }

  @Override
  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public boolean supportsValueModification() {
    return editable;
  }

  @Override
  public String getValueString() {
    return showWhen == null ? value
        : se.getState().equals(showWhen) ? value
            : "see only when " + showWhen.toString();
  }
}
