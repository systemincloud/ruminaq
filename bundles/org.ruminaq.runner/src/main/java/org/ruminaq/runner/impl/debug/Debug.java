/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.runner.impl.debug;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.ruminaq.runner.RunnerLoggerFactory;
import org.ruminaq.runner.impl.debug.events.model.ResumePortRequest.Type;

import org.slf4j.Logger;

public class Debug implements DebugVisited {

  private final Logger logger = RunnerLoggerFactory.getLogger(Debug.class);

  private Lock lock = new ReentrantLock();
  private Condition condition = lock.newCondition();

  private boolean suspended = false;
  private boolean stepping = false;
  private boolean breakpoint = false;
  private int hitCount = 0;
  private boolean suspendAll = false;

  private int hits = 0;

  private DebugListener listener;

  public Debug(DebugListener listener) {
    this.listener = listener;
  }

  public void hit() {
    if (breakpoint && hits < hitCount)
      hits++;
  }

  public boolean resume(Type type) {
    lock.lock();
    logger.trace("resume");
    if (type == Type.STEP_OVER)
      this.stepping = true;
    else
      this.stepping = false;
    this.suspended = false;
    condition.signal();
    lock.unlock();
    return true;
  }

  public boolean suspend() {
    lock.lock();
    logger.trace("suspend");
    boolean ret = !suspended;
    this.suspended = true;
    lock.unlock();
    return ret;
  }

  public void trySuspend() {
    lock.lock();
    if (suspended || stepping || ((hits >= hitCount) && breakpoint)) {
      logger.trace("suspended");
      if (stepping)
        listener.steppingSuspend();
      if (breakpoint)
        listener.breakpointSuspend();
      if (suspendAll)
        listener.suspendAll();
      try {
        condition.await();
      } catch (InterruptedException e) {
      }
    }
    lock.unlock();
  }

  public void breakpoint(boolean b) {
    this.breakpoint = b;
    breakpoint(b, 0, false);
  }

  public void breakpoint(boolean b, int hitCount, boolean suspendAll) {
    logger.trace("breakpoint: {}, hit count: {}, suspendAll: {}", b, hitCount,
        suspendAll);
    this.breakpoint = b;
    this.hitCount = hitCount < 1 ? 1 : hitCount;
    this.suspendAll = suspendAll;
  }

  @Override
  public void accept(DebugVisitor visitor) {
    visitor.visit(this);
  }
}
