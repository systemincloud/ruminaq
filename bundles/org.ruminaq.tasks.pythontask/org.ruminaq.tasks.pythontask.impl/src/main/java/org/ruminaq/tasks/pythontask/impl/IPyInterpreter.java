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
