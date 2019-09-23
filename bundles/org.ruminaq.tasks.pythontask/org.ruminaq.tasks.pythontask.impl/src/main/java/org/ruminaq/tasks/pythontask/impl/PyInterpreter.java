package org.ruminaq.tasks.pythontask.impl;

import java.util.HashMap;
import java.util.Map;

import org.python.pydev.core.IPythonNature;
import org.ruminaq.runner.impl.EmbeddedTaskI;
import org.ruminaq.tasks.pythontask.impl.cpython.CPythonInterpreter;
import org.ruminaq.tasks.pythontask.impl.jython.JythonInterpreter;

public abstract class PyInterpreter implements IPyInterpreter {

  private boolean initialized = false;

  protected Map<String, PythonTaskI> idToPythonTaskMap = new HashMap<>();

  public synchronized static PyInterpreter getInstance(int type,
      EmbeddedTaskI parent, String processJar) {
    PyInterpreter ret;
    if (type == IPythonNature.INTERPRETER_TYPE_PYTHON)
      ret = CPythonInterpreter.getInstance();
    else
      ret = JythonInterpreter.getInstance();
    if (!ret.initialized) {
      ret.initialized = true;
      ret.init(parent, processJar);
    }
    return ret;
  }

  protected abstract void init(EmbeddedTaskI parent, String processJar);

  @Override
  public synchronized PythonTaskProxy createTask(String implFile, String id,
      PythonTaskI pythonTaskI) {
    idToPythonTaskMap.put(id, pythonTaskI);
    this.createTask(id, implFile);
    return new PythonTaskProxy(id, this);
  }

  protected abstract void createTask(String id, String implFile);
}
