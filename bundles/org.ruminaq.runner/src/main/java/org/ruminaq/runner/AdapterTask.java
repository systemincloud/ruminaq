package org.ruminaq.runner;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;

import org.apache.commons.cli.CommandLine;
import org.ruminaq.runner.impl.EmbeddedTaskI;
import org.ruminaq.runner.impl.GeneratorI;
import org.ruminaq.runner.impl.TaskI;
import org.ruminaq.runner.impl.debug.events.IDebugEvent;
import ch.qos.logback.classic.Logger;

public class AdapterTask extends EmbeddedTaskI {

  private final Logger logger = RunnerLoggerFactory
      .getLogger(AdapterTask.class);

  private Engine engine;

  public void setEngine(Engine engine) {
    this.engine = engine;
  }

  private CommandLine cmdlie;

  private EmbeddedTaskI mainTask;

  private String basePath;

  @Override
  public String getBasePath() {
    return basePath;
  }

  public AdapterTask(String basePath, String testFile, boolean runOnlyLocal,
      CommandLine cmdlie) {
    super();
    this.basePath = basePath;
    this.cmdlie = cmdlie;
    this.mainTask = new EmbeddedTaskI(this, null, testFile, null, false,
        runOnlyLocal);
  }

  @Override
  public Lock getReadyLock() {
    return engine.getLock();
  }
//	public void hangEngine()   { try { engine.getCondition().await(); } catch (InterruptedException e) { } }
//	public void signalEngine() { engine.getCondition().signal(); }

  @Override
  public void setReadyWithParents(boolean ready) {
    logger.trace("setReadyWithParents");
    if (ready) {
      engine.getLock().lock();
      logger.trace("Notify engine");
      engine.getCondition().signal();
      engine.getLock().unlock();
    }
  }

  @Override
  public void breakRunner() {
    engine.breakRunner();
  }

  @Override
  public LinkedList<TaskI> getReadyTasks() {
    LinkedList<TaskI> ret = mainTask.getReadyTasks();
    return ret;
  }

  @Override
  public boolean isReady() {
    return mainTask.isReady();
  }

  @Override
  public void runnerStart() {
    mainTask.runnerStart();
  }

  @Override
  public void executeConstants() {
    mainTask.executeConstants();
  }

  @Override
  public List<? extends GeneratorI> getAllGenerators() {
    return mainTask.getAllGenerators();
  }

  @Override
  public boolean hasExternalSource() {
    return mainTask.hasExternalSource();
  }

  @Override
  public void runnerStop() {
    mainTask.runnerStop();
  }

  @Override
  public String getParameter(String key) {
    return null;
  }

  @Override
  public String getInputArgument(String name) {
    return cmdlie.getOptionValue(name);
  }

  @Override
  public void modelEvent(IDebugEvent event) {
    mainTask.modelEvent(event);
  }

  @Override
  public void spreadDebugEvent(IDebugEvent event) {
    modelEvent(event);
  }

  @Override
  public void initDebugers() {
    mainTask.initDebugers();
  }
}
