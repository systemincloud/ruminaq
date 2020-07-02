/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.debug.model.port.out;

import java.util.Map;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IVariable;
import org.ruminaq.debug.model.MainState;
import org.ruminaq.debug.model.vars.Data;
import org.ruminaq.debug.model.vars.KeyValueGroupVariable;
import org.ruminaq.debug.model.vars.StringVariable;
import org.ruminaq.runner.impl.debug.VariableDebugVisitor.Variable;
import org.ruminaq.runner.impl.debug.events.AbstractPortEvent;
import org.ruminaq.runner.impl.debug.events.IDebugEvent;
import org.ruminaq.runner.impl.debug.events.debugger.DataEvent;
import org.ruminaq.runner.impl.debug.events.debugger.InternalOutputPortVariablesEvent;
import org.ruminaq.runner.impl.debug.events.debugger.WaitingForEvent;
import org.ruminaq.runner.impl.debug.events.model.FetchDataRequest;
import org.ruminaq.runner.impl.debug.events.model.FetchPortVariablesRequest;
import org.ruminaq.runner.impl.debug.events.model.FetchWaitingForRequest;
import org.ruminaq.tasks.debug.model.Task;
import org.ruminaq.tasks.debug.model.port.Port;

public class OutputPort extends Port {

  private KeyValueGroupVariable config;
  private Data data;
  private StringVariable waiting;
  private StringVariable waitingFor;
  protected boolean dataRetrived;
  protected boolean waitingForRetrived;

  public OutputPort(IDebugTarget target, String id, Task task) {
    super(target, id, task);
    config = new KeyValueGroupVariable(getDebugTarget(), "configuration", this);
    data = new Data(getDebugTarget(), null, this);
    waiting = new StringVariable(getDebugTarget(), "waiting", "false", false,
        this, MainState.SUSPENDED);
    waitingFor = new StringVariable(getDebugTarget(), "waiting for", "", false,
        this, MainState.SUSPENDED);
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
    return id + " [OUT]";
  }

  @Override
  public synchronized IVariable[] getVariables() {
    if (dirtyVars) {
      dirtyVars = false;
      getDebugTarget().fireModelEvent(new FetchPortVariablesRequest(this));
    }

    if (this.state == MainState.SUSPENDED && !dataRetrived) {
      getDebugTarget().fireModelEvent(new FetchDataRequest(this));
      this.dataRetrived = true;
    }

    if (this.state == MainState.SUSPENDED && !waitingForRetrived) {
      getDebugTarget().fireModelEvent(new FetchWaitingForRequest(this));
      this.waitingForRetrived = true;
    }

    return new IVariable[] { config, data, waiting, waitingFor };
  }

  @Override
  public boolean hasVariables() {
    return getVariables().length > 0;
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
    if (event instanceof InternalOutputPortVariablesEvent
        && ((AbstractPortEvent) event).compare(this)) {
      setVariables(((InternalOutputPortVariablesEvent) event).getVariables());
    } else if (event instanceof DataEvent
        && ((AbstractPortEvent) event).compare(this)) {
      this.data = new Data(getDebugTarget(), ((DataEvent) event).getData(),
          this);
      fireChangeEvent(DebugEvent.CONTENT);
    } else if (event instanceof WaitingForEvent
        && ((AbstractPortEvent) event).compare(this)) {
      WaitingForEvent we = (WaitingForEvent) event;
      this.waiting.setValue(Boolean.toString(we.isWaiting()));
      this.waitingFor.setValue(we.getWaitingFor());
      fireChangeEvent(DebugEvent.CONTENT);
    }
  }

  @Override
  protected void suspended() {
    this.dataRetrived = false;
    this.waitingForRetrived = false;
  }

  public synchronized void setVariables(Map<Variable, Object> map) {
    for (Variable name : map.keySet()) {
      if (name.equals(Variable.IS_SYNC))
        config.add(Variable.IS_SYNC.getName(),
            (String) map.get(Variable.IS_SYNC), false, null);
    }
  }

  @Override
  public synchronized void fireChangeEvent(int detail) {
    dirtyVars = true;
    super.fireChangeEvent(detail);
  }
}
