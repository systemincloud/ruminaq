/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.pythontask.impl.service;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;
import org.ruminaq.runner.RunnerLoggerFactory;
import org.ruminaq.runner.impl.data.DataI;
import ch.qos.logback.classic.Logger;

public enum PythonRunnerServiceManager {
  INSTANCE;

  private final Logger logger = RunnerLoggerFactory
      .getLogger(PythonRunnerServiceManager.class);

  private List<PythonRunnerService> services = new ArrayList<>();

  private PythonRunnerServiceManager() {
    ServiceLoader<PythonRunnerService> sl = ServiceLoader
        .load(PythonRunnerService.class);
    for (PythonRunnerService srv : sl) {
      // TODO: check version
      logger.trace("Found Runner Service: {}", srv.toString());
      services.add(srv);
    }
  }

  public void importDatas(PythonInterpreter pi) {
    for (PythonRunnerService srv : services)
      srv.importDatas(pi);
  }

  public PyObject toJythonData(PythonInterpreter pi, DataI dataI,
      PyList pyDims) {
    for (PythonRunnerService srv : services) {
      PyObject d = srv.toJythonData(pi, dataI, pyDims);
      if (d != null)
        return d;
    }
    return null;
  }

  public DataI fromJythonData(PythonInterpreter pi, PyObject data,
      PyObject[] pyValues, List<Integer> dims) {
    for (PythonRunnerService srv : services) {
      DataI d = srv.fromJythonData(pi, data, pyValues, dims);
      if (d != null)
        return d;
    }
    return null;
  }
}
