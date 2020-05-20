package org.ruminaq.tasks.gate.or.impl;

import java.util.List;

import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.runner.impl.BasicTaskI;
import org.ruminaq.runner.impl.EmbeddedTaskI;
import org.ruminaq.runner.impl.PortMap;
import org.ruminaq.runner.impl.data.BoolI;
import org.ruminaq.runner.impl.data.DataI;
import org.ruminaq.tasks.gate.gui.Port;

public class OrI extends BasicTaskI {

  public OrI(EmbeddedTaskI parent, Task task) {
    super(parent, task);
  }

  @Override
  protected void execute(PortMap portIdData, int grp) {
    List<BoolI> datas = DataI.get(portIdData.getAll(Port.IN), BoolI.class);
    int n = datas.get(0).getNumberOfElements();
    if (n == 1) {
      for (BoolI d : datas)
        if (d.getValues()[0]) {
          putData(Port.OUT, new BoolI(true));
          return;
        }
      putData(Port.OUT, new BoolI(false));
      return;
    } else {
      boolean[] ret = new boolean[n];
      for (int i = 0; i < n; i++)
        ret[i] = false;
      for (BoolI d : datas) {
        boolean[] bs = d.getValues();
        for (int i = 0; i < n; i++)
          ret[i] = bs[i] || ret[i];
      }
      putData(Port.OUT, new BoolI(ret, datas.get(0).getDimensions()));
      return;
    }
  }
}
