package org.ruminaq.runner.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.locks.Condition;

import org.ruminaq.model.ruminaq.InternalOutputPort;
import org.ruminaq.model.ruminaq.InternalPort;
import org.ruminaq.model.ruminaq.Synchronization;
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
import org.ruminaq.runner.impl.debug.events.debugger.DataEvent;
import org.ruminaq.runner.impl.debug.events.debugger.InternalOutputPortVariablesEvent;
import org.ruminaq.runner.impl.debug.events.debugger.ResumedPortEvent;
import org.ruminaq.runner.impl.debug.events.debugger.SuspendedPortEvent;
import org.ruminaq.runner.impl.debug.events.debugger.WaitingForEvent;
import org.ruminaq.runner.impl.debug.events.model.FetchDataRequest;
import org.ruminaq.runner.impl.debug.events.model.FetchPortVariablesRequest;
import org.ruminaq.runner.impl.debug.events.model.FetchWaitingForRequest;
import org.ruminaq.runner.impl.debug.events.model.PortBreakpointRequest;
import org.ruminaq.runner.impl.debug.events.model.ResumePortRequest;
import org.ruminaq.runner.impl.debug.events.model.SuspendAllPortRequest;
import org.ruminaq.runner.impl.debug.events.model.SuspendPortRequest;
import org.ruminaq.runner.util.Observable;
import org.ruminaq.runner.util.Observer;
import org.slf4j.Logger;;

public class InternalOutputPortI extends Observable implements Observer,
    DebugListener, DebugVisited, AbstractPortEventListener {

  private final Logger logger = RunnerLoggerFactory
      .getLogger(InternalOutputPortI.class);
  private final Debug debug = new Debug(this);

  private InternalOutputPort model;
  private TaskI parent;

  public TaskI getParent() {
    return parent;
  }

  private volatile DataI syncToSend = null;
  private volatile boolean syncToSendNull = true;
  private volatile DataI data;
  private volatile boolean syncReceivedBefore = false;

  private InternalPort resetSyncPort;
  private boolean loop;

  private List<SynchronizationI> syncOuts = new LinkedList<>();

  public void addSyncOut(SynchronizationI s) {
    syncOuts.add(s);
  }

  private List<InternalOutputPortI> resetSyncOuts = new LinkedList<>();

  public void addResetSync(InternalOutputPortI o) {
    resetSyncOuts.add(o);
  }

  private Condition waitCond;

  private volatile SynchronizationI firstSync = null;
  private volatile SynchronizationI currentSync = null;

  public SynchronizationI getFirstSync() {
    return firstSync;
  }

  public SynchronizationI getCurrentSync() {
    return currentSync;
  }

  public InternalOutputPortI(InternalOutputPort model, TaskI parent) {
    this.model = model;
    this.parent = parent;

    initSync();

    this.resetSyncPort = model.getResetPort();
    this.loop = model.isLoop();

    logger.trace("{}:{} loop {}", parent.getModel().getId(), model.getId(),
        loop);

    this.waitCond = parent.getOutputsLock().newCondition();
  }

  private void initSync() {
    ListIterator<Synchronization> li = model.getSynchronization()
        .listIterator(model.getSynchronization().size());
    while (li.hasPrevious())
      this.currentSync = new SynchronizationI(li.previous(), this, currentSync);
    this.firstSync = currentSync;
  }

  public List<SynchronizationI> getSyncs() {
    return currentSync.getSyncs();
  }

  public InternalOutputPort getModel() {
    return model;
  }

  public InternalPort getResetSyncPort() {
    return resetSyncPort;
  }

  public boolean isSynchronized() {
    return currentSync != null;
  }

  @Override
  public void update(Observable o, Object obj) {
    logger.trace("{}:{} received data.", parent.getModel().getId(),
        model.getId());
    if (obj instanceof DataI)
      putData((DataI) obj);
    else if (obj instanceof Command)
      putCommand((Command) obj);
    o.done();
  }

  @Override
  protected void notifyObservers(Object obj) {
    this.data = null;
    super.notifyObservers(obj);
  }

  public void putData(DataI data) {
    this.data = data;
    parent.getOutputsLock().lock();
    debug.hit();
    debug.trySuspend();

    logger.trace("{}:{}:{} notify waiting output ports",
        parent.getClass().getSimpleName(), parent.getId(), this.getPortId());
    for (SynchronizationI so : syncOuts)
      so.go();
    logger.trace("{}:{}:{} notify skipped ports",
        parent.getClass().getSimpleName(), parent.getId(), this.getPortId());
    for (InternalOutputPortI so : resetSyncOuts)
      so.resetSync();

    if (!isSynchronized()) {
      logger.trace("{}:{}:{} is not synchronized",
          parent.getClass().getSimpleName(), parent.getId(), this.getPortId());
      notifyObservers(data);

    } else if (currentSync.isSkipping()) {
      logger.trace("{}:{}:{} is synchronized however is skipped",
          parent.getClass().getSimpleName(), parent.getId(), this.getPortId());
      notifyObservers(data);
      currentSync.skip();
      this.syncReceivedBefore = false;

    } else if (syncReceivedBefore) {
      logger.trace("{}:{}:{} is synchronized but go() arrived before",
          parent.getClass().getSimpleName(), parent.getId(), this.getPortId());
      notifyObservers(data);

      if (currentSync != null)
        currentSync = currentSync.getNextState();
      if (currentSync == null && loop)
        newLoop();

      this.syncReceivedBefore = false;
    } else {
      logger.trace("{}:{}:{} is normally synchronized",
          parent.getClass().getSimpleName(), parent.getId(), this.getPortId());

      if (!syncToSendNull) {
        logger.trace("{}:{}:{} last data hasn't been sent so wait",
            parent.getClass().getSimpleName(), parent.getId(),
            this.getPortId());
        try {
          waitCond.await();
        } catch (InterruptedException e) {
        }
        logger.trace("{}:{}:{} wake up", parent.getClass().getSimpleName(),
            parent.getId(), this.getPortId());

        if (!isSynchronized()) {
          logger.trace("{}:{}:{} now is not synchronized",
              parent.getClass().getSimpleName(), parent.getId(),
              this.getPortId());
          notifyObservers(data);
          parent.getOutputsLock().unlock();
          return;
        } else if (currentSync.isSkipping()) {
          logger.trace("{}:{}:{} is synchronized however now is skipped",
              parent.getClass().getSimpleName(), parent.getId(),
              this.getPortId());
          notifyObservers(data);
          currentSync.skip();
          this.syncReceivedBefore = false;
          parent.getOutputsLock().unlock();
          return;
        } else if (syncReceivedBefore) {
          logger.trace("{}:{}:{} now syncReceivedBefore",
              parent.getClass().getSimpleName(), parent.getId(),
              this.getPortId());
          notifyObservers(data);

          if (currentSync != null)
            currentSync = currentSync.getNextState();
          if (currentSync == null && loop)
            newLoop();

          this.syncReceivedBefore = false;
          parent.getOutputsLock().unlock();
          return;
        }
      }

      syncToSend = data;
      syncToSendNull = false;

      parent.putDataSync(this);
      parent.getOutputsSync().signal();
    }
    parent.getOutputsLock().unlock();
  }

  public void putDataSync() {
    if (!syncToSendNull) {
      logger.trace("{}:{}:{} notify observers",
          parent.getClass().getSimpleName(), parent.getId(), this.getPortId());
      DataI tmpToSend = syncToSend;
      syncToSend = null;
      syncToSendNull = true;
      notifyObservers(tmpToSend);

      if (currentSync != null)
        currentSync = currentSync.getNextState();
      if (currentSync == null && loop)
        newLoop();

      waitCond.signal();
    } else
      logger.trace("{}:{}:{} No data to send",
          parent.getClass().getSimpleName(), parent.getId(), this.getPortId());
  }

  public boolean isWaiting() {
    return !syncToSendNull;
  }

  public void go() {
    logger.trace("{}:{} go, current Sync is {}", parent.getModel().getId(),
        model.getId(), currentSync);

    parent.getOutputsLock().lock();

    logger.trace("{}:{} notified for synchronization",
        parent.getModel().getId(), model.getId());
    if (syncToSendNull) {
      logger.trace("{}:{} synchronization received before data put in the port",
          parent.getModel().getId(), model.getId());
      syncReceivedBefore = true;
    }
    parent.goSync(this);
    parent.getOutputsLock().unlock();
  }

  public void newLoop() {
    parent.getOutputsLock().lock();
    this.currentSync = firstSync;
    parent.getOutputsLock().unlock();
  }

  public void resetSync() {
    parent.getOutputsLock().lock();
    logger.trace("{}:{} resetSync", parent.getModel().getId(), model.getId(),
        currentSync);
    this.currentSync = firstSync;
    this.currentSync.reset();
    parent.getOutputsLock().unlock();
  }

  public void putCommand(Command cmd) {
    notifyObservers(cmd);
  }

  public void modelEvent(IDebugEvent event) {
    if (event instanceof FetchPortVariablesRequest
        && ((AbstractPortEvent) event).compare(this)) {
      logger.trace("{}:{} FetchPortVariablesRequest", parent.getModel().getId(),
          model.getId());
      VariableDebugVisitor visitor = new VariableDebugVisitor();
      this.accept(visitor);
      DebugI.INSTANCE
          .debug(new InternalOutputPortVariablesEvent(visitor, this));

    }
    if (event instanceof FetchDataRequest
        && ((AbstractPortEvent) event).compare(this)) {
      logger.trace("{}:{} FetchDataRequest", parent.getModel().getId(),
          model.getId());
      DebugI.INSTANCE.debug(new DataEvent(data, this));

    }
    if (event instanceof FetchWaitingForRequest
        && ((AbstractPortEvent) event).compare(this)) {
      logger.trace("{}:{} FetchWaitingForRequest", parent.getModel().getId(),
          model.getId());
      DebugI.INSTANCE
          .debug(new WaitingForEvent(isWaiting(), currentSync, this));

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
