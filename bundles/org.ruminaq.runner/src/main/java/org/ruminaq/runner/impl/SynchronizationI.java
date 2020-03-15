package org.ruminaq.runner.impl;

import java.util.LinkedList;
import java.util.List;

import org.ruminaq.model.ruminaq.InternalPort;
import org.ruminaq.model.ruminaq.Synchronization;
import org.ruminaq.runner.RunnerLoggerFactory;
import org.ruminaq.util.GroovyExpressionUtil;
import ch.qos.logback.classic.Logger;

public class SynchronizationI {

  private final Logger logger = RunnerLoggerFactory
      .getLogger(SynchronizationI.class);

  private int syncGroup;
  private InternalPort syncPort;
  private int ticksNb;
  private int maxSkipFirst;
  private boolean loop;
  private boolean skipLoop;

  private InternalOutputPortI iop;
  private SynchronizationI next;

  public int getSyncGroup() {
    return syncGroup;
  }

  public InternalPort getSyncPort() {
    return syncPort;
  }

  public SynchronizationI getNextState() {
    if (skipLoop)
      skipped = 1;
    return loop ? this : next;
  }

  private volatile int skipped = 0;
  private volatile int ticks = 0;

  public SynchronizationI(Synchronization model, InternalOutputPortI iop,
      SynchronizationI next) {
    this.syncGroup = model.getGroup();
    this.syncPort = model.getWaitForPort();
    this.loop = model.isLoop();
    this.skipLoop = model.isSkipLoop();
    this.iop = iop;
    this.next = next;

    this.ticksNb = (int) GroovyExpressionUtil.evaluate(
        iop.getParent().getParent().replaceVariables(model.getWaitForTicks()));
    this.maxSkipFirst = (int) GroovyExpressionUtil.evaluate(
        iop.getParent().getParent().replaceVariables(model.getSkipFirst()));

    logger.trace("{}:{}:{} group {}",
        iop.getParent().getClass().getSimpleName(), iop.getParent().getId(),
        iop.getPortId(), syncGroup);
    logger.trace("{}:{}:{} looped {}",
        iop.getParent().getClass().getSimpleName(), iop.getParent().getId(),
        iop.getPortId(), loop);
    logger.trace("{}:{}:{} skipLoop {}",
        iop.getParent().getClass().getSimpleName(), iop.getParent().getId(),
        iop.getPortId(), skipLoop);
    logger.trace("{}:{}:{} sync ticksNb {}",
        iop.getParent().getClass().getSimpleName(), iop.getParent().getId(),
        iop.getPortId(), ticksNb);
    logger.trace("{}:{}:{} sync maxSkipFirst {}",
        iop.getParent().getClass().getSimpleName(), iop.getParent().getId(),
        iop.getPortId(), maxSkipFirst);
  }

  public List<SynchronizationI> getSyncs() {
    List<SynchronizationI> ret = next == null
        ? new LinkedList<SynchronizationI>()
        : next.getSyncs();
    ret.add(this);
    return ret;
  }

  public boolean isSkipping() {
    return maxSkipFirst > 0 && skipped < maxSkipFirst;
  }

  public void skip() {
    this.skipped++;
  }

  public boolean isLoop() {
    return loop;
  }

  public boolean isSkipLoop() {
    return skipLoop;
  }

  public void go() {
    if (this == iop.getCurrentSync()) {
      ticks++;
      if (ticks == ticksNb) {
        iop.go();
        ticks = 0;
      }
    }
  }

  public void reset() {
    this.skipped = 0;
    this.ticks = 0;
    if (next != null)
      next.reset();
  }
}
