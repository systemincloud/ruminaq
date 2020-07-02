/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.pythontask.impl;

public class PythonTaskProxy {

  private String id;
  private IPyInterpreter pyInterpreter;

  public PythonTaskProxy(String id, PyInterpreter pyInterpreter) {
    this.id = id;
    this.pyInterpreter = pyInterpreter;
  }

  public boolean atomic() {
    return pyInterpreter.atomic(id);
  }

  public boolean generator() {
    return pyInterpreter.generator(id);
  }

  public boolean externalSource() {
    return pyInterpreter.externalSource(id);
  }

  public boolean constant() {
    return pyInterpreter.constant(id);
  }

  public void runnerStart() {
    pyInterpreter.runnerStart(id);
  }

  public void runnerStop() {
    pyInterpreter.runnerStop(id);
  }

  public void execute(int i) {
    pyInterpreter.execute(id, i);
  }

  public void executeAsync(String portId) {
    pyInterpreter.executeAsync(id, portId);
  }

  public void executeExternalSrc() {
    pyInterpreter.executeExternalSrc(id);
  }

  public void generate() {
    pyInterpreter.generate(id);
  }
}
