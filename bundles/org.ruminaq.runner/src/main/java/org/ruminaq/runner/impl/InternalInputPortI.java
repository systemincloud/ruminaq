/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.runner.impl;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;

import org.ruminaq.consts.Constants;
import org.ruminaq.model.ruminaq.InternalInputPort;
import org.ruminaq.runner.RunnerLoggerFactory;
import org.ruminaq.runner.impl.cmd.Command;
import org.ruminaq.runner.impl.data.DataI;
import org.ruminaq.runner.impl.debug.Debug;
import org.ruminaq.runner.impl.debug.DebugI;
import org.ruminaq.runner.impl.debug.DebugListener;
import org.ruminaq.runner.impl.debug.DebugVisited;
import org.ruminaq.runner.impl.debug.DebugVisitor;
import org.ruminaq.runner.impl.debug.VariableDebugVisitor;
import org.ruminaq.runner.impl.debug.events.AbstractPortEvent;
import org.ruminaq.runner.impl.debug.events.AbstractPortEventListener;
import org.ruminaq.runner.impl.debug.events.IDebugEvent;
import org.ruminaq.runner.impl.debug.events.debugger.DataQueueEvent;
import org.ruminaq.runner.impl.debug.events.debugger.InternalInputPortVariablesEvent;
import org.ruminaq.runner.impl.debug.events.debugger.ResumedPortEvent;
import org.ruminaq.runner.impl.debug.events.debugger.SuspendedPortEvent;
import org.ruminaq.runner.impl.debug.events.model.FetchDataQueueRequest;
import org.ruminaq.runner.impl.debug.events.model.FetchPortVariablesRequest;
import org.ruminaq.runner.impl.debug.events.model.PortBreakpointRequest;
import org.ruminaq.runner.impl.debug.events.model.ResumePortRequest;
import org.ruminaq.runner.impl.debug.events.model.SuspendAllPortRequest;
import org.ruminaq.runner.impl.debug.events.model.SuspendPortRequest;
import org.ruminaq.runner.util.Observable;
import org.ruminaq.runner.util.Observer;
import org.slf4j.Logger;

public class InternalInputPortI extends Observable implements Observer,
    DebugListener, DebugVisited, AbstractPortEventListener {

  private final Logger logger = RunnerLoggerFactory
      .getLogger(InternalInputPortI.class);
  private final Debug debug = new Debug(this);

  private InternalInputPort model;
  private TaskI parent;

  private int group = -1;

  public int getGroup() {
    return group;
  }

  private volatile int queueSize = 1;

  public int getQueueSize() {
    return queueSize;
  }

  private boolean holdLast = false;

  public boolean isHoldLast() {
    return holdLast;
  }

  private volatile boolean holdLastRead = false;

  public boolean isHoldLastRead() {
    return holdLastRead;
  }

  private boolean syncPort = false;

  private volatile List<DataI> dataQueue = Collections
      .synchronizedList(new LinkedList<DataI>());

  public List<DataI> getDataQueue() {
    return dataQueue;
  }

  private List<SynchronizationI> syncOuts = new LinkedList<>();

  public void addSyncOut(SynchronizationI s) {
    syncOuts.add(s);
  }

  private List<InternalOutputPortI> resetSyncOuts = new LinkedList<>();

  public void addResetSync(InternalOutputPortI o) {
    resetSyncOuts.add(o);
  }

  protected Condition portSync;

  // TODO:
//	private long lastInterval = 0;

  public boolean isAsynchronous() {
    return model.isAsynchronous();
  }

  public InternalInputPortI(InternalInputPort model, TaskI parent) {
    this.model = model;
    this.parent = parent;
    this.group = model.getGroup();
    this.queueSize = model.getQueueSize().equals(Constants.INF) ? -1
        : Integer.parseInt(
            parent.getParent().replaceVariables(model.getQueueSize()));
    this.holdLast = model.isHoldLast() && !model.isAsynchronous();
    this.syncPort = model.isPreventLostDefault()
        ? parent.getParent().getSyncConns()
        : model.isPreventLost();
    portSync = parent.getInputsLock().newCondition();
    logger.trace("{}:{} queue: size {}", parent.getModel().getId(),
        model.getId(), this.queueSize);
    logger.trace("{}:{} hold: last {}", parent.getModel().getId(),
        model.getId(), this.holdLast);
  }

  public InternalInputPort getModel() {
    return model;
  }

  @Override
  public void update(Observable o, Object obj) {
    parent.getInputsLock().lock();
    logger.trace("{}:{}:{} update", parent.getClass().getSimpleName(),
        parent.getModel().getId(), model.getId());
    logger.trace("{}:{}:{} data queue {}.", parent.getClass().getSimpleName(),
        parent.getModel().getId(), model.getId(), dataQueue.size());

    if (obj instanceof DataI) {
      DataI data = (DataI) obj;
      logger.trace("{}:{}:{} received data {}.",
          parent.getClass().getSimpleName(), parent.getModel().getId(),
          model.getId(), data.getClass().getSimpleName());

      // TODO: check if interval is shortening or queue is growing and in that
      // case inform parent

      boolean notifyPortManager = false;

      if (dataQueue.size() == queueSize && queueSize > 1) {
        if (syncPort) {
          waitWithAdding();
          notifyPortManager = notifyPortManager || addDataToTheEndOfQueue(data);
        } else
          notifyPortManager = notifyPortManager || lastDataOverwritten(data);
      } else if (dataQueue.size() == 1 && dataQueue.get(0).isConstant()
          && dataQueue.get(0).isConstantRead()) {
        notifyPortManager = notifyPortManager || constantDataOverwritten(data);
      } else if (dataQueue.size() == 1 && dataQueue.get(0).isConstant()
          && !dataQueue.get(0).isConstantRead()) {
        if (syncPort) {
          waitWithAdding();
          notifyPortManager = notifyPortManager
              || constantDataOverwritten(data);
        } else
          notifyPortManager = notifyPortManager || lastDataOverwritten(data);
      } else if (dataQueue.size() == 1 && holdLast && holdLastRead) {
        notifyPortManager = notifyPortManager || holdDataOverwritten(data);
        this.holdLastRead = false;
      } else if (dataQueue.size() == 1 && holdLast && !holdLastRead) {
        if (syncPort) {
          waitWithAdding();
          notifyPortManager = notifyPortManager || holdDataOverwritten(data);
        } else
          notifyPortManager = notifyPortManager || lastDataOverwritten(data);
        this.holdLastRead = false;
      } else if (dataQueue.size() == 1 && queueSize == 1
          && parent.isRunning()) {
        if (syncPort) {
          waitWithAdding();
          notifyPortManager = notifyPortManager || addDataToTheEndOfQueue(data);
        } else
          notifyPortManager = notifyPortManager || dataLost();
      } else if (dataQueue.size() == 1 && queueSize == 1
          && !parent.isRunning()) {
        if (syncPort) {
          waitWithAdding();
          notifyPortManager = notifyPortManager || addDataToTheEndOfQueue(data);
        } else
          notifyPortManager = notifyPortManager || lastDataOverwritten(data);
      } else
        notifyPortManager = notifyPortManager || addDataToTheEndOfQueue(data);

      debug.hit();
      debug.trySuspend();

      if (notifyPortManager)
        parent.portManager(this);

    } else if (obj instanceof Command) {
      Command cmd = (Command) obj;
      logger.trace("{}:{}:{}: received command {}.",
          parent.getClass().getSimpleName(), parent.getModel().getId(),
          model.getId(), cmd.getClass().getSimpleName());
      cmd.execute(this);
    }
    parent.getInputsLock().unlock();
    o.done();
  }

  private void waitWithAdding() {
    logger.debug("{}:{}:{} waitWithAdding", parent.getClass().getSimpleName(),
        parent.getModel().getId(), model.getId());
    try {
      portSync.await();
    } catch (InterruptedException e) {
    }
  }

  private boolean addDataToTheEndOfQueue(DataI data) {
    logger.trace("{}:{}:{} add data to the end of queue {}",
        parent.getClass().getSimpleName(), parent.getId(), this.getPortId(),
        dataQueue.size());
    dataQueue.add(data);
    logger.trace("{}:{}:{} queue size {}", parent.getClass().getSimpleName(),
        parent.getId(), this.getPortId(), dataQueue.size());
    return true;
  }

  private boolean dataLost() {
    parent.dataOverwrittenOrLost(this);
    logger.warn("{}:{} data lost on {}", parent.getClass().getSimpleName(),
        parent.getId(), this.getPortId());
    return false;
  }

  private boolean lastDataOverwritten(DataI data) {
    parent.dataOverwrittenOrLost(this);
    logger.warn("{}:{} data overwritten on {}",
        parent.getClass().getSimpleName(), parent.getId(), this.getPortId());
    dataQueue.remove(dataQueue.size() - 1);
    dataQueue.add(data);
    return false;
  }

  private boolean holdDataOverwritten(DataI data) {
    logger.trace("{}:{} hold data overwritten on {}",
        parent.getClass().getSimpleName(), parent.getId(), this.getPortId());
    dataQueue.remove(dataQueue.size() - 1);
    dataQueue.add(data);
    return true;
  }

  private boolean constantDataOverwritten(DataI data) {
    logger.trace("{}:{} constant data overwritten on {}",
        parent.getClass().getSimpleName(), parent.getId(), this.getPortId());
    dataQueue.remove(dataQueue.size() - 1);
    dataQueue.add(data);
    return true;
  }

  private volatile int reserved = 0;

  public boolean hasData() {
    return dataQueue.size() - reserved > 0;
  }

  public void reserveData() {
    if (dataQueue.size() == 1 && holdLast)
      return;
    if (dataQueue.size() == 1 && dataQueue.get(0).isConstant())
      return;
    reserved++;
  }

  public DataI getData() {
    parent.getInputsLock().lock();

    logger.trace("{}:{}:{} taking data {}", parent.getClass().getSimpleName(),
        parent.getId(), this.getPortId(), dataQueue.size());
    DataI dataI = dataQueue.get(0);

    if (dataQueue.size() == 1 && holdLast)
      this.holdLastRead = true;
    if (dataQueue.size() == 1 && dataQueue.get(0).isConstant())
      dataQueue.get(0).setConstantRead(true);
    ;

    boolean canRemove = true;
    if (dataQueue.size() == 1 && holdLast)
      canRemove = false;
    if (dataQueue.size() == 1 && dataQueue.get(0).isConstant())
      canRemove = false;

    if (canRemove) {
      logger.trace("{}:{} remove data", parent.getClass().getSimpleName(),
          parent.getId());
      dataQueue.remove(0);
      reserved--;
    }

    if (syncPort)
      portSync.signal();

    parent.getInputsLock().unlock();

    logger.trace("{}:{}:{} notify waiting output ports",
        parent.getClass().getSimpleName(), parent.getId(), this.getPortId());
    for (SynchronizationI so : syncOuts)
      so.go();
    logger.trace("{}:{}:{} notify skipped ports",
        parent.getClass().getSimpleName(), parent.getId(), this.getPortId());
    for (InternalOutputPortI so : resetSyncOuts)
      so.resetSync();

    return dataI;
  }

  public synchronized void removeAllData() {
    while (dataQueue.size() > 0)
      dataQueue.remove(0);
  }

  public void putData(Object data) {
    logger.trace("{}:{}: put data {}", parent.getId(), model.getId(), data);
    notifyObservers(data);
  }

  public void modelEvent(IDebugEvent event) {
    if (event instanceof FetchPortVariablesRequest
        && ((AbstractPortEvent) event).compare(this)) {
      logger.trace("{}:{} FetchPortVariablesRequest", parent.getModel().getId(),
          model.getId());
      VariableDebugVisitor visitor = new VariableDebugVisitor();
      this.accept(visitor);
      DebugI.INSTANCE.debug(new InternalInputPortVariablesEvent(visitor, this));

    } else if (event instanceof FetchDataQueueRequest
        && ((AbstractPortEvent) event).compare(this)) {
      logger.trace("{}:{} FetchDataQueueRequest", parent.getModel().getId(),
          model.getId());
      DebugI.INSTANCE.debug(new DataQueueEvent(dataQueue, this));

    } else if (event instanceof ResumePortRequest
        && ((AbstractPortEvent) event).compare(this)) {
      logger.trace("{}:{} ResumePortRequest", parent.getModel().getId(),
          model.getId());
      if (debug.resume(((ResumePortRequest) event).getType()))
        DebugI.INSTANCE.debug(new ResumedPortEvent(
            ResumedPortEvent.Type.get(((ResumePortRequest) event).getType()),
            this));

    } else if ((event instanceof SuspendPortRequest
        && ((AbstractPortEvent) event).compare(this))
        || event instanceof SuspendAllPortRequest) {
      logger.trace("{}:{} SuspendPortRequest", parent.getModel().getId(),
          model.getId());
      if (debug.suspend())
        DebugI.INSTANCE.debug(
            new SuspendedPortEvent(SuspendedPortEvent.Type.CLIENT, this));

    } else if (event instanceof PortBreakpointRequest
        && ((AbstractPortEvent) event).compare(this)) {
      logger.trace("{}:{} PortBreakpointRequest", parent.getModel().getId(),
          model.getId());
      PortBreakpointRequest rq = (PortBreakpointRequest) event;
      switch (rq.getType()) {
        case ADDED:
          logger.trace("{}:{}:{} add breakpoint",
              parent.getClass().getSimpleName(), parent.getId(),
              this.getPortId());
          debug.breakpoint(true, rq.getHitCount(), rq.isSupendAll());
          break;
        case REMOVED:
          logger.trace("{}:{}:{} remove breakpoint",
              parent.getClass().getSimpleName(), parent.getId(),
              this.getPortId());
          debug.breakpoint(false);
          break;
      }
    }
  }

  @Override
  public void steppingSuspend() {
    DebugI.INSTANCE
        .debug(new SuspendedPortEvent(SuspendedPortEvent.Type.STEP_OVER, this));
  }

  @Override
  public void breakpointSuspend() {
    DebugI.INSTANCE.debug(
        new SuspendedPortEvent(SuspendedPortEvent.Type.BREAKPOINT, this));
  }

  @Override
  public void suspendAll() {
    parent.spreadDebugEvent(new SuspendAllPortRequest());
  }

  public void initDebugers() {
    debug.suspend();
    DebugI.INSTANCE
        .debug(new SuspendedPortEvent(SuspendedPortEvent.Type.INIT, this));
  }

  @Override
  public void accept(DebugVisitor visitor) {
    visitor.visit(this);
    visitor.visit(debug);
  }

  @Override
  public String getDiagramPath() {
    return parent.getParent().getDiagramPath();
  }

  @Override
  public String getTaskId() {
    return parent.getId();
  }

  @Override
  public String getPortId() {
    return model.getId();
  }
}
