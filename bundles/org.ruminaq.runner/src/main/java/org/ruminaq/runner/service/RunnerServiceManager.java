/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.runner.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;

import org.apache.commons.cli.Options;
import org.osgi.framework.Version;
import org.ruminaq.consts.Constants;
import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.runner.RunnerLoggerFactory;
import org.ruminaq.runner.impl.EmbeddedTaskI;
import org.ruminaq.runner.impl.TaskI;
import org.ruminaq.runner.impl.data.DataI;
import org.ruminaq.runner.thrift.RemoteData;
import org.slf4j.Logger;

public enum RunnerServiceManager {

  INSTANCE;

  public static final String QUALIFIER = ".qualifier";
  public static final String SNAPSHOT = "-SNAPSHOT";

  private final Logger logger = RunnerLoggerFactory
      .getLogger(RunnerServiceManager.class);

  private List<RunnerService> services = new ArrayList<>();

  private RunnerServiceManager() {
    ServiceLoader<RunnerService> sl = ServiceLoader.load(RunnerService.class);
    for (RunnerService srv : sl) {
      logger.trace("Found Runner Service: {}", srv.toString());
      services.add(srv);
    }
  }

  public void initModelPackages() {
    services.forEach(RunnerService::initModelPackages);
  }

  public TaskI getImplementation(EmbeddedTaskI parent, Task task) {
    for (RunnerService srv : services) {
      if (srv.getVersion() == null)
        continue;
//      Version v1 = Version.parseVersion(
//          task.getVersion().replace(Constants.SNAPSHOT, Constants.QUALIFIER));
//      Version v2 = Version.parseVersion(
//          srv.getVersion().replace(Constants.SNAPSHOT, Constants.QUALIFIER));
//      if (task.getBundleName().equals(srv.getBundleName())
//          && v1.getMajor() == v2.getMajor() && v1.getMinor() == v2.getMinor()
//          && v1.getMicro() == v2.getMicro()) {
        TaskI taskI = srv.getImplementation(parent, task);
        if (taskI != null)
          return taskI;
//      }
    }
    return null;
  }

  public void addOptions(Options options) {
    services.forEach(srv -> srv.addOptions(options));
  }

  public Optional<Class<DataI>> getDataFromName(String name) {
    return services.stream().map(srv -> srv.getDataFromName(name))
        .filter(Objects::nonNull).findFirst();
  }

  public Optional<RemoteData> toRemoteData(DataI dataI) {
    return services.stream().map(srv -> srv.toRemoteData(dataI))
        .filter(Objects::nonNull).findFirst();
  }

  public Optional<DataI> fromRemoteData(RemoteData data) {
    return services.stream().map(srv -> srv.fromRemoteData(data))
        .filter(Objects::nonNull).findFirst();
  }
}
