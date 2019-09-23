package org.ruminaq.tasks.debug.model.port.in;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IVariable;
import org.ruminaq.debug.model.IStateElement;
import org.ruminaq.debug.model.MainState;
import org.ruminaq.debug.model.vars.Data;
import org.ruminaq.debug.model.vars.SicVariable;
import org.ruminaq.runner.impl.data.DataI;

public class DataQueueVariable extends SicVariable {

  private IStateElement se;
  private List<Data> dataQueue = new LinkedList<>();

  protected DataQueueVariable(IDebugTarget target, IStateElement se) {
    super(target, "data queue", "");
    this.se = se;
    setValue("");
  }

  @Override
  public String getValueString() {
    return se.getState() != MainState.SUSPENDED ? "see only when suspended"
        : "";
  }

  public void setData(List<DataI> dq) {
    int i = 0;
    for (DataI d : dq) {
      dataQueue.add(new Data(getDebugTarget(), d, i));
      i++;
    }
  }

  @Override
  public boolean hasVariables() {
    return se.getState() != MainState.SUSPENDED ? false : dataQueue.size() > 0;
  }

  @Override
  public IVariable[] getVariables() {
    return dataQueue.toArray(new IVariable[dataQueue.size()]);
  }

  public void clear() {
    dataQueue.clear();
  }
}
