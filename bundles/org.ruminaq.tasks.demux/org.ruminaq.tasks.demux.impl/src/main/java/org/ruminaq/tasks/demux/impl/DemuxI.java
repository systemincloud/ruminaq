/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.demux.impl;

import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.runner.RunnerLoggerFactory;
import org.ruminaq.runner.impl.BasicTaskI;
import org.ruminaq.runner.impl.EmbeddedTaskI;
import org.ruminaq.runner.impl.PortMap;
import org.ruminaq.runner.impl.data.DataI;
import org.ruminaq.runner.impl.data.Int32I;
import org.ruminaq.tasks.mux.model.DemuxPort;

import ch.qos.logback.classic.Logger;

public class DemuxI extends BasicTaskI {

  private final Logger logger = RunnerLoggerFactory.getLogger(DemuxI.class);

  private int idx = 0;

  public DemuxI(EmbeddedTaskI parent, Task task) {
    super(parent, task);
  }

  @Override
  protected void execute(PortMap portIdData, int grp) {
    if (grp == DemuxPort.IDX.getGroup()) {
      this.idx = portIdData.get(DemuxPort.IDX).get(Int32I.class).getValues()[0];
      logger.trace("Change index to {}", this.idx);
    } else if (grp == DemuxPort.IN.getGroup()) {
      DataI data = portIdData.get(DemuxPort.IN);
      putData(DemuxPort.OUT, idx, data);
    }
  }
}
