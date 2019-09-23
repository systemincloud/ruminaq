package org.ruminaq.runner.impl.debug.events.debugger;

import java.util.List;

import org.ruminaq.runner.impl.data.DataI;
import org.ruminaq.runner.impl.debug.events.AbstractPortEvent;
import org.ruminaq.runner.impl.debug.events.AbstractPortEventListener;
import org.ruminaq.runner.impl.debug.events.IDebuggerEvent;

public class DataQueueEvent extends AbstractPortEvent
    implements IDebuggerEvent {

  private static final long serialVersionUID = 1L;

  private List<DataI> dataQueue;

  public List<DataI> getDataQueue() {
    return dataQueue;
  }

  public DataQueueEvent(List<DataI> dataQueue, AbstractPortEventListener apel) {
    super(apel);
    this.dataQueue = dataQueue;
  }

  @Override
  public void preevaluate() {
  }
}
