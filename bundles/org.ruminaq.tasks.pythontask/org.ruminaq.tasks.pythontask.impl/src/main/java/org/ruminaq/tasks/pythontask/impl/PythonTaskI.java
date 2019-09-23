package org.ruminaq.tasks.pythontask.impl;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map.Entry;

import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.runner.Runner;
import org.ruminaq.runner.RunnerLoggerFactory;
import org.ruminaq.runner.impl.EmbeddedTaskI;
import org.ruminaq.runner.impl.GeneratorI;
import org.ruminaq.runner.impl.PortMap;
import org.ruminaq.runner.impl.data.DataI;
import org.ruminaq.runner.util.Util;
import org.ruminaq.tasks.pythontask.model.pythontask.PythonTask;
import org.ruminaq.util.GroovyExpressionUtil;
import org.slf4j.Logger;

public class PythonTaskI extends GeneratorI implements PythonTaskListener {

  private final Logger logger = RunnerLoggerFactory
      .getLogger(PythonTaskI.class);

  public static final String ATTR_PY_TYPE = "py_type";
  public static final String ATTR_PY_BIN = "py_bin";
  public static final String ATTR_PY_ENV = "py_env";
  public static final String ATTR_PY_PATH = "py_path";
  public static final String ATTR_PY_EXT_LIBS = "py_ext_libs";

  public static final String PROCESS_LIB_GROUPID = "org.ruminaq.tasks.pythontask";
  public static final String PROCESS_LIB_ARTIFACT = "org.ruminaq.tasks.pythontask.process";
  public static final String PROCESS_LIB_VERSION = "0.2.0";

  private PythonTaskProxy pyProxy = null;
  private Logger pyLogger = null;

  private PyInterpreter interpreter;

  private HashMap<String, String> parameters = new HashMap<>();

  private PortMap portIdData;

  public PythonTaskI(EmbeddedTaskI parent, Task task) {
    super(parent, task);

    int type = Integer.parseInt(parent.getInputArgument(ATTR_PY_TYPE));

    logger.trace("type: {}", type);

    String mvnRepo = parent.getInputArgument(Runner.ATTR_MVN_REPO);
    String processJar = mvnRepo + File.separator
        + PROCESS_LIB_GROUPID.replace(".", File.separator) + File.separator
        + PROCESS_LIB_ARTIFACT + File.separator + PROCESS_LIB_VERSION
        + File.separator + PROCESS_LIB_ARTIFACT + "-" + PROCESS_LIB_VERSION
        + ".jar";

    interpreter = PyInterpreter.getInstance(type, parent, processJar);

    String impl = ((PythonTask) task).getImplementation();

    logger.trace("impl: {}", impl);

    this.pyLogger = RunnerLoggerFactory.getLogger(impl);

    //
    // Parameters
    //
    for (Entry<String, String> p : ((org.ruminaq.tasks.pythontask.model.pythontask.PythonTask) task)
        .getParameters().entrySet())
      parameters.put(p.getKey(), parent.replaceVariables(p.getValue()));

    this.pyProxy = interpreter.createTask(impl,
        Util.getUniqueId((PythonTask) task, parent.getBasePath()), this);

    //
    // PythonTaskInfo
    //
    this.atomic = pyProxy.atomic();
    this.generator = pyProxy.generator();
    this.externalSource = pyProxy.externalSource();
    this.constant = pyProxy.constant();
  }

  @Override
  public void runnerStart() {
    pyProxy.runnerStart();
  }

  @Override
  public void runnerStop() {
    pyProxy.runnerStop();
  }

  @Override
  protected void executeConstant() {
    logger.trace("executeConstant");
    this.portIdData = null;
    pyProxy.execute(-1);
  }

  @Override
  protected void execute(PortMap portIdData, int grp) {
    logger.trace("execute");
    this.portIdData = portIdData;
    pyProxy.execute(grp);
  }

  @Override
  protected void executeAsync(String portId, DataI data) {
    logger.trace("executeAsync: {}:{}", portId, data);
    PortMap portIdData = new PortMap();
    portIdData.put(portId, data);
    this.portIdData = portIdData;
    pyProxy.executeAsync(portId);
  }

  @Override
  protected void executeExternalSrc() {
    logger.trace("executeExternalSrc");
    this.portIdData = null;
    pyProxy.executeExternalSrc();
  }

  @Override
  protected void generate() {
    logger.trace("generate");
    this.portIdData = null;
    pyProxy.generate();
  }

  @Override
  public void sleep(long l) {
    super.sleep(l);
  }

  @Override
  public void externalData(int i) {
    addExternalSrcExecNb(i);
    setReadyWithParents(true);
  }

  @Override
  public void generatorPause() {
    super.pause();
  }

  @Override
  public boolean generatorIsPaused() {
    return super.isPaused();
  }

  @Override
  public void generatorResume() {
    super.resume();
  }

  @Override
  public void generatorEnd() {
    super.end();
  }

  @Override
  public void exitRunner() {
    super.breakRunner();
  }

  @Override
  public String getParameter(String key) {
    return parameters.get(key);
  }

  @Override
  public Object runExpression(String expression) {
    return GroovyExpressionUtil.evaluate(expression);
  }

  @Override
  public void log(String level, String msg) {
    try {
      Method method = pyLogger.getClass().getMethod(level, String.class);
      method.invoke(pyLogger, msg);
    } catch (NoSuchMethodException | SecurityException | IllegalAccessException
        | IllegalArgumentException | InvocationTargetException e) {
    }
  }

  @Override
  public DataI getData(String ipName) {
    if (portIdData == null)
      return null;
    return portIdData.get(ipName);
  }

  @Override
  public void cleanQueue(String ipName) {
    internalInputPorts.get(ipName).removeAllData();
  }

  @Override
  public void putData(String opName, DataI dataI) {
    if (dataI == null)
      return;
    logger.trace("Data is {}", dataI.getClass().getSimpleName());
    super.putData(opName, dataI);
  }
}
