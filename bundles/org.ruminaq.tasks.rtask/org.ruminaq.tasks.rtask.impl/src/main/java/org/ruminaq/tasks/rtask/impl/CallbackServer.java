/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.rtask.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.thrift.TException;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.ruminaq.runner.impl.data.RemoteDataConverter;
import org.ruminaq.runner.thrift.RemoteData;
import org.ruminaq.runner.thrift.RunnerSideServer;
import org.ruminaq.util.Util;

public enum CallbackServer implements RunnerSideServer.Iface {
  INSTANCE;

  private static final int MAIN_PORT = 49352;

  private int port = 0;

  public int getPort() {
    return port;
  }

  public Map<String, RTaskListener> tasks = Collections
      .synchronizedMap(new HashMap<String, RTaskListener>());

  private TServer server = null;

  private Executor service = Executors.newSingleThreadExecutor();

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public synchronized void start(String id, RTaskListener rTaskI) {
    if (tasks.isEmpty()) {
      RunnerSideServer.Processor processor = new RunnerSideServer.Processor(
          this);
      init(processor);
    }

    tasks.put(id, rTaskI);
  }

  public synchronized void stop(String id) {
    tasks.remove(id);

    if (tasks.isEmpty())
      shutdown();
  }

  @SuppressWarnings("rawtypes")
  public int init(final RunnerSideServer.Processor processor) {
    final int port = Util.findFreeLocalPort(MAIN_PORT);
    service.execute(new Runnable() {
      @Override
      public void run() {
        try {
          TServerTransport serverTransport = new TServerSocket(port);
          server = new TThreadPoolServer(
              new TThreadPoolServer.Args(serverTransport).processor(processor));
          server.serve();
        } catch (Exception e) {
          server.stop();
        }
      }
    });
    return port;
  }

  public void shutdown() {
    server.stop();
  }

  @Override
  public void runnerIsRunning() throws TException {
  }

  @Override
  public void externalData(String taskid, int i) throws TException {
    tasks.get(taskid).externalData(i);
  }

  @Override
  public void sleep(String taskid, long l) throws TException {
    tasks.get(taskid).sleep(l);
  }

  @Override
  public void generatorPause(String taskid) throws TException {
    tasks.get(taskid).generatorPause();
  }

  @Override
  public boolean generatorIsPaused(String taskid) throws TException {
    return tasks.get(taskid).generatorIsPaused();
  }

  @Override
  public void generatorResume(String taskid) throws TException {
    tasks.get(taskid).generatorResume();
  }

  @Override
  public void generatorEnd(String taskid) throws TException {
    tasks.get(taskid).generatorEnd();
  }

  @Override
  public void exitRunner(String taskid) throws TException {
    tasks.get(taskid).exitRunner();
  }

  @Override
  public String getParameter(String taskid, String key) throws TException {
    return tasks.get(taskid).getParameter(key);
  }

  @Override
  public String runExpression(String taskid, String expression)
      throws TException {
    return tasks.get(taskid).runExpression(expression);
  }

  @Override
  public void log(String taskid, String level, String message)
      throws TException {
    tasks.get(taskid).log(level, message);
  }

  @Override
  public RemoteData getData(String taskid, String portid, String datatype)
      throws TException {
    return RemoteDataConverter.INSTANCE
        .toRemoteData(tasks.get(taskid).getData(portid, datatype));
  }

  @Override
  public void cleanQueue(String taskid, String portid) throws TException {
    tasks.get(taskid).cleanQueue(portid);
  }

  @Override
  public void putData(String taskid, String portid, RemoteData data)
      throws TException {
    tasks.get(taskid).putData(portid,
        RemoteDataConverter.INSTANCE.fromRemoteData(data));
  }
}
