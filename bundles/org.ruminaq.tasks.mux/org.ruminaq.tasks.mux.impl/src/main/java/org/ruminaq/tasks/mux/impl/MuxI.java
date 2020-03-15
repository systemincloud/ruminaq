package org.ruminaq.tasks.mux.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.ruminaq.model.desc.PortsDescrUtil;
import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.runner.RunnerLoggerFactory;
import org.ruminaq.runner.impl.BasicTaskI;
import org.ruminaq.runner.impl.EmbeddedTaskI;
import org.ruminaq.runner.impl.InternalInputPortI;
import org.ruminaq.runner.impl.PortMap;
import org.ruminaq.runner.impl.data.DataI;
import org.ruminaq.runner.impl.data.Int32I;
import org.ruminaq.tasks.util.TasksUtil;
import org.ruminaq.tasks.mux.model.mux.Mux;
import ch.qos.logback.classic.Logger;

public class MuxI extends BasicTaskI {

  private final Logger logger = RunnerLoggerFactory.getLogger(MuxI.class);

  private int buffer = 0;

  private boolean intialized = false;
  private int idx = 0;

  private Map<Integer, LinkedList<DataI>> idxDatas = new HashMap<>();

  public MuxI(EmbeddedTaskI parent, Task task) {
    super(parent, task);

    this.buffer = ((Mux) task).getPortBuffer();

    int i = 0;
    for (Entry<String, InternalInputPortI> iip : internalInputPorts.entrySet())
      if (TasksUtil.isMultiplePortId(iip.getKey(), Port.IN))
        this.idxDatas.put(new Integer(i++), new LinkedList<DataI>());
    logger.trace("Mux has {} input ports", this.idxDatas.size());
  }

  @Override
  protected void execute(PortMap portIdData, int grp) {
    if (grp == PortsDescrUtil.getGroup(Port.IDX)) {
      this.idx = portIdData.get(Port.IDX).get(Int32I.class).getValues()[0];
      logger.trace("Change index to {}", this.idx);
      LinkedList<DataI> d = idxDatas.get(new Integer(this.idx));
      if (d != null) {
        if (d.size() > 0)
          while (d.size() > 0)
            putData(Port.OUT, d.remove(0));
      }
      if (!intialized)
        intialized = true;
    } else {
      String portId = portIdData.keySet().iterator().next();
      DataI data = portIdData.get(portId);
      int idx = TasksUtil.getMultiplePortIdIdx(portId, Port.IN);
      logger.trace("Data received on {}", idx);
      if (idx == this.idx && intialized)
        putData(Port.OUT, portIdData.values().iterator().next());
      else {
        List<DataI> datas = idxDatas.get(new Integer(idx));

        if (buffer > 0) {
          if (datas.size() == buffer) {
            dataOverwrittenOrLost(internalInputPorts.get(portId));
            datas.remove(datas.size() - 1);
          }
          datas.add(data);
        }
      }
    }
  }
}
