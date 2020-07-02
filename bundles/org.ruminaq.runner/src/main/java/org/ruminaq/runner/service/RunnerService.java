/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.runner.service;

import org.apache.commons.cli.Options;
import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.runner.impl.EmbeddedTaskI;
import org.ruminaq.runner.impl.TaskI;
import org.ruminaq.runner.impl.data.DataI;
import org.ruminaq.runner.thrift.RemoteData;

public interface RunnerService {

  default void initModelPackages() {
  }

  String getBundleName();

  String getVersion();

  default TaskI getImplementation(EmbeddedTaskI parent, Task task) {
    return null;
  }

  default void addOptions(Options options) {
  }

  default Class<DataI> getDataFromName(String name) {
    return null;
  }

  default RemoteData toRemoteData(DataI dataI) {
    return null;
  }

  default DataI fromRemoteData(RemoteData data) {
    return null;
  }
}
