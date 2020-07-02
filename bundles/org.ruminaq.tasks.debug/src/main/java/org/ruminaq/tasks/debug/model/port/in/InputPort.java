/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.debug.model.port.in;

import java.util.Map;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IVariable;
import org.ruminaq.debug.model.MainState;
import org.ruminaq.debug.model.vars.KeyValueGroupVariable;
import org.ruminaq.runner.impl.debug.VariableDebugVisitor.Variable;
import org.ruminaq.runner.impl.debug.events.AbstractPortEvent;
import org.ruminaq.runner.impl.debug.events.IDebugEvent;
import org.ruminaq.runner.impl.debug.events.debugger.DataQueueEvent;
import org.ruminaq.runner.impl.debug.events.debugger.InternalInputPortVariablesEvent;
import org.ruminaq.runner.impl.debug.events.model.FetchDataQueueRequest;
import org.ruminaq.runner.impl.debug.events.model.FetchPortVariablesRequest;
import org.ruminaq.tasks.debug.model.Task;
import org.ruminaq.tasks.debug.model.port.Port;

public class InputPort extends Port {

  private KeyValueGroupVariable config;
  private DataQueueVariable dq;
  protected boolean dqRetrived;

  public InputPort(IDebugTarget target, String id, Task task) {
    super(target, id, task);
    config = new KeyValueGroupVariable(getDebugTarget(), "configuration", this);
    dq = new DataQueueVariable(getDebugTarget(), this);
  }

  @Override
  public IThread getThread() {
    return task;
  }

  @Override
  public int getLineNumber() {
    return 0;
  }

  @Override
  public int getCharStart() {
    return -1;
  }

  @Override
  public int getCharEnd() {
    return -1;
  }

  @Override
  public String getName() {
    return id + " [IN]";
  }

  @Override
  public synchronized IVariable[] getVariables() {
    if (dirtyVars) {
      dirtyVars = false;
      getDebugTarget().fireModelEvent(new FetchPortVariablesRequest(this));
    }

    if (this.state == MainState.SUSPENDED && !dqRetrived) {
      getDebugTarget().fireModelEvent(new FetchDataQueueRequest(this));
      this.dqRetrived = true;
    }

    return new IVariable[] { config, dq };
  }

  @Override
  public IRegisterGroup[] getRegisterGroups() {
    return new IRegisterGroup[0];
  }

  @Override
  public boolean hasRegisterGroups() {
    return getRegisterGroups().length > 0;
  }

  @Override
  public void handleEvent(IDebugEvent event) {
    super.handleEvent(event);
    if (event instanceof InternalInputPortVariablesEvent
        && ((AbstractPortEvent) event).compare(this)) {
      config.clear();
      setVariables(((InternalInputPortVariablesEvent) event).getVariables());
      fireChangeEvent(DebugEvent.CONTENT);
    } else if (event instanceof DataQueueEvent
        && ((AbstractPortEvent) event).compare(this)) {
      dq.clear();
      dq.setData(((DataQueueEvent) event).getDataQueue());
      fireChangeEvent(DebugEvent.CONTENT);
    }
  }

  @Override
  protected void suspended() {
    this.dqRetrived = false;
  }

  public synchronized void setVariables(Map<Variable, Object> map) {
    for (Variable name : map.keySet()) {
      if (name.equals(Variable.QUEUE_INIT_SIZE))
        config.add(Variable.QUEUE_INIT_SIZE.getName(),
            (String) map.get(Variable.QUEUE_INIT_SIZE), false, null);
      else if (name.equals(Variable.HOLD_LAST))
        config.add(Variable.HOLD_LAST.getName(),
            (String) map.get(Variable.HOLD_LAST), false, null);
    }
  }

  @Override
  public synchronized void fireChangeEvent(int detail) {
    if (this.state != MainState.SUSPENDED)
      dirtyVars = true;
    super.fireChangeEvent(detail);
  }
}
