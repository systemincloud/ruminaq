/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.runner.impl.debug;

import java.util.HashMap;
import java.util.Map;

import org.ruminaq.runner.impl.InternalInputPortI;
import org.ruminaq.runner.impl.InternalOutputPortI;

public class VariableDebugVisitor implements DebugVisitor {

  public enum Variable {
    DATA_QUEUE("data queue"), QUEUE_INIT_SIZE("queue max size"),
    HOLD_LAST("hold last"), IS_SYNC("synchronized");

    private String name;

    public String getName() {
      return name;
    }

    Variable(String name) {
      this.name = name;
    }
  }

  private Map<Variable, Object> variables = new HashMap<>();

  public Map<Variable, Object> getVariables() {
    return variables;
  }

  @Override
  public void visit(InternalInputPortI elem) {
    variables.put(Variable.DATA_QUEUE, elem.getDataQueue());
    variables.put(Variable.QUEUE_INIT_SIZE,
        Integer.toString(elem.getQueueSize()));
    variables.put(Variable.HOLD_LAST, Boolean.toString(elem.isHoldLast()));
  }

  @Override
  public void visit(InternalOutputPortI elem) {
    variables.put(Variable.IS_SYNC,
        Boolean.toString(elem.getFirstSync() != null));
  }

  @Override
  public void visit(Debug elem) {
  }
}
