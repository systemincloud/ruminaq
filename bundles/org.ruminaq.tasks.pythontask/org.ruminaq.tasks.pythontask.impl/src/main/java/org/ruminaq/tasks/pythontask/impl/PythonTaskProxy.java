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
