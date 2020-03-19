package org.ruminaq.tasks.rtask.impl.service;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.ruminaq.runner.RunnerLoggerFactory;
import org.ruminaq.runner.impl.data.DataI;
import ch.qos.logback.classic.Logger;

//import de.walware.rj.data.RObject;

public enum RRunnerServiceManager {
  INSTANCE;

  private final Logger logger = RunnerLoggerFactory
      .getLogger(RRunnerServiceManager.class);

  private List<RRunnerService> services = new ArrayList<>();

  private RRunnerServiceManager() {
    ServiceLoader<RRunnerService> sl = ServiceLoader.load(RRunnerService.class);
    for (RRunnerService srv : sl) {
      // TODO: check version
      logger.trace("Found Runner Service: {}", srv.toString());
      services.add(srv);
    }
  }

//  public RObject toRData(DataI dataI, RObject dims) {
//    for (RRunnerService srv : services) {
//      RObject d = srv.toRData(dataI, dims);
//      if (d != null)
//        return d;
//    }
//    return null;
//  }
//
//  public DataI fromRData(RObject data, RObject[] rValues, List<Integer> dims) {
//    for (RRunnerService srv : services) {
//      DataI d = srv.fromRData(data, rValues, dims);
//      if (d != null)
//        return d;
//    }
//    return null;
//  }
}
