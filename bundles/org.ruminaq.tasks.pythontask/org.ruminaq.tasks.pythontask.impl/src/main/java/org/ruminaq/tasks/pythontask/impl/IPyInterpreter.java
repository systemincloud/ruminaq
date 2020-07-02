/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.pythontask.impl;

public interface IPyInterpreter {
  PythonTaskProxy createTask(String implFile, String id,
      PythonTaskI pythonTaskI);

  boolean atomic(String id);

  boolean generator(String id);

  boolean externalSource(String id);

  boolean constant(String id);

  void runnerStart(String id);

  void runnerStop(String id);

  void execute(String id, int i);

  void executeAsync(String id, String portId);

  void executeExternalSrc(String id);

  void generate(String id);
}
