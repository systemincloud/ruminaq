package org.ruminaq.runner.impl.debug;

import java.rmi.RemoteException;

import org.ruminaq.runner.Engine;
import org.ruminaq.runner.RunnerLoggerFactory;
import org.ruminaq.runner.dirmi.DirmiClient;
import org.ruminaq.runner.impl.debug.events.IDebugEvent;
import org.ruminaq.runner.impl.debug.events.model.TerminateRequest;
import org.slf4j.Logger;

public enum DebugI {
  INSTANCE;

  private final Logger logger = RunnerLoggerFactory.getLogger(DebugI.class);

  private boolean debugOn = false;

  private IDebugService ds;
  private Engine engine;

  public void init(boolean debugOn) {
    this.debugOn = debugOn;
    DirmiClient.INSTANCE.register("DEBUG", "main", new IDebugIService() {
      @Override
      public void setDebugService(IDebugService ds) {
        logger.trace("setDebugService");
        DebugI.this.ds = ds;
      }

      @Override
      public void modelEvent(IDebugEvent event) throws RemoteException {
        if (event instanceof TerminateRequest)
          terminate();
        else
          engine.modelEvent(event);
      }
    });
  }

  public void setEngine(Engine engine) {
    this.engine = engine;
  }

  public void initDebugers() {
    if (debugOn)
      engine.initDebugers();
  }

  public void debug(IDebugEvent event) {
    if (!debugOn)
      return;
    event.preevaluate();
    try {
      ds.debuggerEvent(event);
    } catch (RemoteException e) {
    }
  }

  private void terminate() {
    engine.breakRunner();
    engine.getLock().lock();
    engine.getCondition().signal();
    engine.getLock().unlock();
  }
}
