package org.ruminaq.tasks.javatask.impl;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map.Entry;

import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.runner.RunnerLoggerFactory;
import org.ruminaq.runner.impl.EmbeddedTaskI;
import org.ruminaq.runner.impl.GeneratorI;
import org.ruminaq.runner.impl.PortMap;
import org.ruminaq.runner.impl.data.DataI;
import org.ruminaq.tasks.javatask.client.InputPort;
import org.ruminaq.tasks.javatask.client.JavaTask;
import org.ruminaq.tasks.javatask.client.JavaTaskListener;
import org.ruminaq.tasks.javatask.client.OutputPort;
import org.ruminaq.tasks.javatask.client.annotations.InputPortInfo;
import org.ruminaq.tasks.javatask.client.annotations.JavaTaskInfo;
import org.ruminaq.tasks.javatask.client.annotations.OutputPortInfo;
import org.ruminaq.tasks.javatask.client.data.Data;
import org.ruminaq.util.GroovyExpressionUtil;
import org.slf4j.Logger;

public class JavaTaskI extends GeneratorI implements JavaTaskListener {

  private final Logger logger = RunnerLoggerFactory.getLogger(JavaTaskI.class);

  private Class<? extends JavaTask> clazz;

  private JavaTask jt = null;
  private Logger jtLogger = null;

  private HashMap<String, String> parameters = new HashMap<>();

  private PortMap portIdData;

  @SuppressWarnings("unchecked")
  public JavaTaskI(EmbeddedTaskI parent, Task task) {
    super(parent, task);
    String implementationClass = ((org.ruminaq.tasks.javatask.model.javatask.JavaTask) task)
        .getImplementationClass();
    try {
      this.clazz = (Class<? extends JavaTask>) JavaTaskI.class.getClassLoader()
          .loadClass(implementationClass);
      logger.trace("Found user class {}", clazz.getCanonicalName());
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }

    //
    // Parameters
    //
    for (Entry<String, String> p : ((org.ruminaq.tasks.javatask.model.javatask.JavaTask) task)
        .getParameters().entrySet())
      parameters.put(p.getKey(), parent.replaceVariables(p.getValue()));

    //
    // JavaTaskInfo
    //
    JavaTaskInfo jti = clazz.getAnnotation(JavaTaskInfo.class);
    setAtomic(jti.atomic());
    setGenerator(jti.generator());
    setExternalSource(jti.externalSource());
    setConstant(jti.constant());

    this.jtLogger = RunnerLoggerFactory.getLogger(clazz);
    this.jt = initJT();
  }

  private JavaTask initJT() {
    JavaTask jt;
    try {
      jt = clazz.newInstance();
      for (Field f : clazz.getSuperclass().asSubclass(JavaTask.class)
          .getDeclaredFields()) {
        if (f.getType().equals(JavaTaskListener.class)) {
          logger.trace("Set ReadyListener");
          f.setAccessible(true);
          f.set(jt, this);
        }
      }
    } catch (InstantiationException | IllegalAccessException e) {
      return null;
    }
    initPorts(jt);
    return jt;
  }

  private void initPorts(JavaTask jt) {
    logger.trace("Init Ports");
    try {
      for (Field f : clazz.getDeclaredFields()) {
        InputPortInfo ipi = f.getAnnotation(InputPortInfo.class);
        OutputPortInfo opi = f.getAnnotation(OutputPortInfo.class);
        if (ipi != null) {
          logger.trace("Found Input Port {}", ipi.name());
          InputPort ip = new InputPort(ipi.name());
          try {
            for (Field ipf : InputPort.class.getDeclaredFields())
              if (ipf.getType().equals(JavaTaskListener.class)) {
                ipf.setAccessible(true);
                ipf.set(ip, this);
              }
          } catch (IllegalAccessException e) {
          }
          f.setAccessible(true);
          f.set(jt, ip);
        } else if (opi != null) {
          logger.trace("Found Output Port {}", opi.name());
          OutputPort op = new OutputPort(opi.name());
          try {
            for (Field opf : OutputPort.class.getDeclaredFields())
              if (opf.getType().equals(JavaTaskListener.class)) {
                opf.setAccessible(true);
                opf.set(op, this);
              }
          } catch (IllegalAccessException e) {
          }
          f.setAccessible(true);
          f.set(jt, op);
        }
      }
    } catch (IllegalAccessException | IllegalArgumentException e) {
      e.printStackTrace();
    }
  }

  private InputPort getInputPortById(JavaTask jt, String portId)
      throws IllegalArgumentException, IllegalAccessException {
    for (Field f : clazz.getFields()) {
      InputPortInfo ipi = f.getAnnotation(InputPortInfo.class);
      if (ipi != null) {
        if (portId.equals(ipi.name()))
          return (InputPort) f.get(jt);
      }
    }
    return null;
  }

  @Override
  public void executeConstant() {
    logger.trace("executeConstant");
    this.portIdData = null;
    jt.execute(-1);
  }

  @Override
  protected void execute(PortMap portIdData, int grp) {
    logger.trace("execute");
    this.portIdData = portIdData;
    jt.execute(grp);
  }

  @Override
  protected void executeAsync(String portId, DataI data) {
    logger.trace("executeAsync: {}:{}", portId, data);
    PortMap portIdData = new PortMap();
    portIdData.put(portId, data);
    this.portIdData = portIdData;
    try {
      jt.executeAsync(getInputPortById(jt, portId));
    } catch (IllegalArgumentException | IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void executeExternalSrc() {
    logger.trace("executeExternalSrc");
    this.portIdData = null;
    jt.executeExtSrc();
  }

  @Override
  protected void generate() {
    logger.trace("generate");
    this.portIdData = null;
    jt.generate();
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
  public Logger log() {
    return jtLogger;
  }

  @Override
  public void runnerStart() {
    jt.runnerStart();
  }

  @Override
  public void runnerStop() {
    jt.runnerStop();
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends Data> T getData(InputPort ip, Class<T> type) {
    if (portIdData == null)
      return null;

    DataI dataI = portIdData.get(ip.getName());

    if (dataI != null) {
      logger.trace("Found data implementation {}", dataI);
      if (Data.class.equals(type))
        return (T) JavaTaskDataConverter.INSTANCE.toJavaTaskData(dataI);
      else if (Data.class.isAssignableFrom(type))
        return (T) JavaTaskDataConverter.INSTANCE.toJavaTaskData(dataI, type);
    }
    return null;
  }

  @Override
  public void cleanQueue(InputPort ip) {
    internalInputPorts.get(ip.getName()).removeAllData();
  }

  @Override
  public void putData(OutputPort op, Data data, boolean copy) {
    if (data == null)
      return;
    DataI dataI = JavaTaskDataConverter.INSTANCE.fromJavaTaskData(data, copy);
    if (dataI == null)
      return;
    logger.trace("Data is {}", dataI.getClass().getSimpleName());
    super.putData(op.getName(), dataI);
  }
}
