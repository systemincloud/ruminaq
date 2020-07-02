/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.pythontask.impl.rmi;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.ruminaq.runner.thrift.RunnerSideServer;
import org.ruminaq.util.Util;

public class Server {

  public static final int MAIN_PORT = 59152;

  private TServer server = null;

  private Executor service = Executors.newSingleThreadExecutor();

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
}
