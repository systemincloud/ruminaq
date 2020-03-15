package org.ruminaq.tasks.demux.impl;

import org.ruminaq.model.desc.PortsDescrUtil;
import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.runner.RunnerLoggerFactory;
import org.ruminaq.runner.impl.BasicTaskI;
import org.ruminaq.runner.impl.EmbeddedTaskI;
import org.ruminaq.runner.impl.PortMap;
import org.ruminaq.runner.impl.data.DataI;
import org.ruminaq.runner.impl.data.Int32I;
import ch.qos.logback.classic.Logger;

public class DemuxI extends BasicTaskI {

  private final Logger logger = RunnerLoggerFactory.getLogger(DemuxI.class);

  private int idx = 0;

  public DemuxI(EmbeddedTaskI parent, Task task) {
    super(parent, task);
  }

  @Override
  protected void execute(PortMap portIdData, int grp) {
    if (grp == PortsDescrUtil.getGroup(Port.IDX)) {
      this.idx = portIdData.get(Port.IDX).get(Int32I.class).getValues()[0];
      logger.trace("Change index to {}", this.idx);
    } else if (grp == PortsDescrUtil.getGroup(Port.IN)) {
      DataI data = portIdData.get(Port.IN);
      putData(Port.OUT, idx, data);
    }
  }
}
