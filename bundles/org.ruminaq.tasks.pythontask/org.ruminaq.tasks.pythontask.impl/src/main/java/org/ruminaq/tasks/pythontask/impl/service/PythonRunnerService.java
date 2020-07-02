/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.pythontask.impl.service;

import java.util.List;

import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;
import org.ruminaq.runner.impl.data.DataI;

public interface PythonRunnerService {
  void importDatas(PythonInterpreter pi);

  PyObject toJythonData(PythonInterpreter pi, DataI dataI, PyList pyDims);

  DataI fromJythonData(PythonInterpreter pi, PyObject data, PyObject[] pyValues,
      List<Integer> dims);
}
