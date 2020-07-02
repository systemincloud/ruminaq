/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.javatask.impl.service;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import org.ruminaq.runner.RunnerLoggerFactory;
import org.ruminaq.runner.impl.data.DataI;
import org.ruminaq.tasks.javatask.client.data.Data;
import ch.qos.logback.classic.Logger;

public enum JavaTaskServiceManager {
  INSTANCE;

  private final Logger logger = RunnerLoggerFactory
      .getLogger(JavaTaskServiceManager.class);

  private List<JavaTaskService> services = new ArrayList<>();

  private JavaTaskServiceManager() {
    ServiceLoader<JavaTaskService> sl = ServiceLoader
        .load(JavaTaskService.class);
    for (JavaTaskService srv : sl) {
      // TODO : Only project version tasks
      services.add(srv);
    }
    logger.trace("found {} extensions", services.size());
  }

  public Data toJavaTaskData(DataI dataI, Class<? extends Data> to) {
    for (JavaTaskService srv : services) {
      Data data = srv.toJavaTaskData(dataI, to);
      if (data != null)
        return data;
    }
    return null;
  }

  public Data toJavaTaskData(DataI dataI) {
    for (JavaTaskService srv : services) {
      Data data = srv.toJavaTaskData(dataI);
      if (data != null)
        return data;
    }
    return null;
  }

  public DataI fromJavaTaskData(Data data, boolean copy) {
    for (JavaTaskService srv : services) {
      DataI dataI = srv.fromJavaTaskData(data, copy);
      if (dataI != null)
        return dataI;
    }
    return null;
  }

}
