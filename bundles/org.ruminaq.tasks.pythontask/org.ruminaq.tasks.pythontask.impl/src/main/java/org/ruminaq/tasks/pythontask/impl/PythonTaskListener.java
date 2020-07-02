/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.pythontask.impl;

import org.ruminaq.runner.impl.data.DataI;

public interface PythonTaskListener {

  // PythonTask
  void externalData(int i);

  void sleep(long l);

  void generatorPause();

  boolean generatorIsPaused();

  void generatorResume();

  void generatorEnd();

  void exitRunner();

  String getParameter(String key);

  Object runExpression(String expression);

  void log(String level, String msg);

  // InputPort
  DataI getData(String ipName);

  void cleanQueue(String ipName);

  // OutputPort
  void putData(String opName, DataI dataI);
}
