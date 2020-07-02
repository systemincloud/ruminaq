/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.pythontask.impl.jython;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.python.core.PyBoolean;
import org.python.core.PyClass;
import org.python.core.PyInteger;
import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;
import org.ruminaq.runner.RunnerLoggerFactory;
import org.ruminaq.runner.impl.EmbeddedTaskI;
import org.ruminaq.runner.impl.data.DataI;
import org.ruminaq.runner.impl.data.DataManager;
import org.ruminaq.tasks.pythontask.impl.PyInterpreter;
import org.ruminaq.tasks.pythontask.impl.PythonTaskI;
import ch.qos.logback.classic.Logger;

public class JythonInterpreter extends PyInterpreter {

  private final Logger logger = RunnerLoggerFactory
      .getLogger(JythonInterpreter.class);

  public static String JYTHON_VERSION;

  static {
    Properties prop = new Properties();
    try {
      prop.load(PythonTaskI.class.getResourceAsStream("jython.properties"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    JYTHON_VERSION = prop.getProperty("jython-version");
  }

  private JythonInterpreter() {
  }

  private static class LazyHolder {
    private static final JythonInterpreter INSTANCE = new JythonInterpreter();
  }

  public static JythonInterpreter getInstance() {
    return LazyHolder.INSTANCE;
  }

  private PythonInterpreter pi;
  private Map<String, PyObject> tasks = new HashMap<>();

  @Override
  protected void init(EmbeddedTaskI parent, String processJar) {
    String path = parent.getInputArgument(PythonTaskI.ATTR_PY_PATH);

    Properties props = new Properties();
    props.put("python.path", path.replace(";", File.pathSeparator));
    props.put("python.console.encoding", "UTF-8");
    props.put("python.security.respectJavaAccessibility", "false");
    props.put("python.import.site", "false");
    PythonInterpreter.initialize(System.getProperties(), props, new String[0]);
    this.pi = new PythonInterpreter();
    JythonDataConverter.INSTANCE.importDatas(pi);
    pi.exec("from sicpythontask.InputPort import InputPort");
  }

  public class PtListener extends PyObject {
    private static final long serialVersionUID = 1L;

    public void externalData(String taskid, int i) {
      idToPythonTaskMap.get(taskid).externalData(i);
    }

    public void sleep(String taskid, long l) {
      idToPythonTaskMap.get(taskid).sleep(l);
    }

    public void generatorPause(String taskid) {
      idToPythonTaskMap.get(taskid).generatorPause();
    }

    public boolean generatorIsPaused(String taskid) {
      return idToPythonTaskMap.get(taskid).generatorIsPaused();
    }

    public void generatorResume(String taskid) {
      idToPythonTaskMap.get(taskid).generatorResume();
    }

    public void generatorEnd(String taskid) {
      idToPythonTaskMap.get(taskid).generatorEnd();
    }

    public void exitRunner(String taskid) {
      idToPythonTaskMap.get(taskid).exitRunner();
    }

    public String getParameter(String taskid, String key) {
      return idToPythonTaskMap.get(taskid).getParameter(key);
    }

    public PyObject runExpression(String taskid, String expression) {
      return new PyString(
          idToPythonTaskMap.get(taskid).runExpression(expression).toString());
    }

    public void log(String taskid, String level, String message) {
      idToPythonTaskMap.get(taskid).log(level, message);
    }

    public PyObject getData(String taskid, String portid, PyObject datatype) {
      DataI dataI = idToPythonTaskMap.get(taskid).getData(portid);
      if (dataI != null)
        dataI = dataI.get(DataManager.INSTANCE
            .getDataFromName(((PyClass) datatype).__name__));
      return JythonDataConverter.INSTANCE
          .toJythonData(JythonInterpreter.this.pi, dataI);
    }

    public void cleanQueue(String taskid, String portid) {
      idToPythonTaskMap.get(taskid).getInputPort(portid).removeAllData();
    }

    public void putData(String taskid, String portid, Object data) {
      DataI dataI = JythonDataConverter.INSTANCE
          .fromJythonData(JythonInterpreter.this.pi, (PyObject) data);
      PythonTaskI t = idToPythonTaskMap.get(taskid);
      if (t != null && dataI != null)
        t.putData(portid, dataI);
    }
  }

  @Override
  protected void createTask(String id, String implFile) {
    int dot = implFile.lastIndexOf(".");
    String module = dot != -1 ? implFile.substring(dot + 1) : implFile;
    String importStr = "from " + implFile + " import " + module;
    logger.trace("import: '{}'", importStr);
    pi.exec(importStr);
    PyObject pyObject = pi.get(module);

    PyObject pt = pyObject.__call__().__call__();

    PtListener ptListener = new PtListener();
    pt.__setattr__("taskid", new PyString(id));
    pt.__setattr__("ptListener", ptListener);
    pi.exec("from sicpythontask.Logger import Logger");
    PyObject pyLoggerModule = pi.get("Logger");
    PyObject pyLogger = pyLoggerModule.__call__(ptListener, new PyString(id));
    pt.__setattr__("logger", pyLogger);

    pt.__getattr__("__init_ports__").__call__();

    Iterator<?> it = ((PyList) pt.__dir__()).iterator();
    while (it.hasNext()) {
      String d = (String) it.next();
      PyObject po = pt.__getattr__(d);
      if ("instance".equals(po.getType().getName())
          || "instance".equals(po.getType().getName())) {
        po.__setattr__("ptListener", ptListener);
        po.__setattr__("taskid", new PyString(id));
      }
    }
    tasks.put(id, pt);
  }

  @Override
  public void runnerStart(String id) {
    tasks.get(id).__getattr__("runner_start").__call__();
  }

  @Override
  public void runnerStop(String id) {
    tasks.get(id).__getattr__("runner_stop").__call__();
  }

  @Override
  public void executeAsync(String id, String portId) {
    for (Object n : ((PyList) tasks.get(id).__dir__()).toArray()) {
      PyObject d = tasks.get(id).__getattr__((String) n);
      if (pi.get("InputPort").equals(d.fastGetClass())
          && d.__getattr__("name").toString().equals(portId)) {
        tasks.get(id).__getattr__("execute_async").__call__((PyObject) d);
        break;
      }
    }
  }

  @Override
  public void execute(String id, int grp) {
    tasks.get(id).__getattr__("execute").__call__(new PyInteger(grp));
  }

  @Override
  public void generate(String id) {
    tasks.get(id).__getattr__("generate").__call__();
  }

  @Override
  public void executeExternalSrc(String id) {
    tasks.get(id).__getattr__("execute_ext_src").__call__();
  }

  @Override
  public boolean atomic(String id) {
    return ((PyBoolean) tasks.get(id).__getattr__("atomic")).getBooleanValue();
  }

  @Override
  public boolean generator(String id) {
    return ((PyBoolean) tasks.get(id).__getattr__("generator"))
        .getBooleanValue();
  }

  @Override
  public boolean externalSource(String id) {
    return ((PyBoolean) tasks.get(id).__getattr__("external_source"))
        .getBooleanValue();
  }

  @Override
  public boolean constant(String id) {
    return ((PyBoolean) tasks.get(id).__getattr__("constant"))
        .getBooleanValue();
  }
}
