package org.ruminaq.runner.service;

import org.apache.commons.cli.Options;
import org.ruminaq.model.model.ruminaq.Task;
import org.ruminaq.runner.impl.EmbeddedTaskI;
import org.ruminaq.runner.impl.TaskI;
import org.ruminaq.runner.impl.data.DataI;
import org.ruminaq.runner.thrift.RemoteData;

public interface RunnerService {

    default void initModelPackages() {
    }

    String getBundleName();
    String getVersion();

    default TaskI  getImplementation(EmbeddedTaskI parent, Task task) {
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
