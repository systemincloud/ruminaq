/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.runner.impl.debug.events.debugger;

import java.util.Map;

import org.ruminaq.runner.impl.debug.VariableDebugVisitor;
import org.ruminaq.runner.impl.debug.VariableDebugVisitor.Variable;
import org.ruminaq.runner.impl.debug.events.AbstractPortEvent;
import org.ruminaq.runner.impl.debug.events.AbstractPortEventListener;
import org.ruminaq.runner.impl.debug.events.IDebuggerEvent;

public class InternalInputPortVariablesEvent extends AbstractPortEvent
    implements IDebuggerEvent {

  private static final long serialVersionUID = 1L;

  private Map<Variable, Object> variables;

  public Map<Variable, Object> getVariables() {
    return variables;
  }

  public InternalInputPortVariablesEvent(VariableDebugVisitor visitor,
      AbstractPortEventListener apel) {
    super(apel);
    this.variables = visitor.getVariables();
  }

  @Override
  public void preevaluate() {
  }
}
