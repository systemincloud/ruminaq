/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.debug.api;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.ruminaq.runner.impl.debug.events.IDebugEvent;
import org.ruminaq.runner.impl.debug.events.IDebuggerEvent;
import org.ruminaq.runner.impl.debug.events.IModelRequest;

public class EventDispatchJob extends Job {

  private List<IDebugEvent> events = new ArrayList<IDebugEvent>();
  private boolean terminated = false;
  private List<IEventProcessor> hosts = new LinkedList<>();
  private IEventProcessor debugger;

  public void addHost(IEventProcessor host) {
    this.hosts.add(host);
  }

  public void setDebugger(IEventProcessor debugger) {
    this.debugger = debugger;
  }

  public boolean isTerminated() {
    return terminated;
  }

  public EventDispatchJob() {
    super("System in Cloud Debugger event dispatcher");
    setSystem(true);
  }

  public void addEvent(final IDebugEvent event) {
    synchronized (events) {
      events.add(event);
    }
    synchronized (this) {
      notifyAll();
    }
  }

  @Override
  protected IStatus run(final IProgressMonitor monitor) {
    while (!terminated) {
      if (events.isEmpty()) {
        try {
          synchronized (this) {
            wait();
          }
        } catch (InterruptedException e) {
        }
      }
      if (!monitor.isCanceled()) {
        IDebugEvent event = null;
        synchronized (events) {
          if (!events.isEmpty())
            event = events.remove(0);
        }
        if (event != null)
          handleEvent(event);
      } else
        terminate();
    }
    return Status.OK_STATUS;
  }

  private void handleEvent(final IDebugEvent event) {
    if (event instanceof IDebuggerEvent)
      for (IEventProcessor host : hosts)
        host.handleEvent(event);

    else if (event instanceof IModelRequest)
      debugger.handleEvent(event);

    else
      throw new RuntimeException("Unknown event detected: " + event);
  }

  public void terminate() {
    terminated = true;
    synchronized (this) {
      notifyAll();
    }
  }
}
