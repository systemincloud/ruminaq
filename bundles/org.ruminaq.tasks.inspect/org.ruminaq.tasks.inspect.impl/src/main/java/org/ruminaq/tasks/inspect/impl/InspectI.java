package org.ruminaq.tasks.inspect.impl;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;

import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.runner.RunnerLoggerFactory;
import org.ruminaq.runner.dirmi.DirmiClient;
import org.ruminaq.runner.impl.BasicTaskI;
import org.ruminaq.runner.impl.EmbeddedTaskI;
import org.ruminaq.runner.impl.PortMap;
import org.ruminaq.runner.impl.data.DataI;
import org.ruminaq.runner.impl.data.TextI;
import org.ruminaq.tasks.inspect.model.Port;
import org.ruminaq.tasks.inspect.model.inspect.Inspect;
import org.ruminaq.runner.util.Util;
import ch.qos.logback.classic.Logger;

public class InspectI extends BasicTaskI {

  private final Logger logger = RunnerLoggerFactory.getLogger(InspectI.class);

  private Inspect inspect;
  private boolean pretty;

  private String lastValue;

  private List<InspectWindowService> listeners = new LinkedList<>();

  public InspectI(EmbeddedTaskI parent, Task task) {
    super(parent, task);

    this.inspect = (Inspect) task;
    this.pretty = inspect.isMatricesPretty();

//    DirmiClient.INSTANCE.register(
//        inspect.getBundleName() + ":" + inspect.getVersion(),
//        Util.getUniqueId(inspect, parent.getBasePath()), new InspectIService() {
//          @Override
//          public void addListener(InspectWindowService tvWindowService)
//              throws RemoteException {
//            if (!listeners.contains(tvWindowService))
//              listeners.add(tvWindowService);
//          }
//
//          @Override
//          public void removeListener(InspectWindowService tvWindowService)
//              throws RemoteException {
//            if (listeners.contains(tvWindowService))
//              listeners.remove(tvWindowService);
//          }
//
//          @Override
//          public String getLastValue() throws RemoteException {
//            return lastValue;
//          }
//        });
  }

  @Override
  protected void execute(PortMap portIdData, int grp) {
    logger.trace("execute");

    DataI data = portIdData.get(Port.IN);
    TextI text = data.get(TextI.class);
    if (text == null) {
      logger.error("No text in port");
      return;
    }
    lastValue = text.toString(pretty, 0, TextI.Separator.SPACE);

    for (InspectWindowService service : listeners) {
      try {
        service.newValue(lastValue);
      } catch (RemoteException e) {
        logger.error(e.getMessage());
      }
    }
  }
}
