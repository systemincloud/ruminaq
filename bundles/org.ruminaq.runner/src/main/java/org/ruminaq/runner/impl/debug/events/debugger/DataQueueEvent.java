/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
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
