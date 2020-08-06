/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.rtask.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.ruminaq.util.Util;

public enum PrintOutServer {
  INSTACE;

  private static final int MAIN_PORT = 49252;

  private int port = 0;

  public int getPort() {
    return port;
  }

  private List<RTaskI> tasks = new LinkedList<>();

  private ServerSocket serverSocket;

  private Executor acceptor = Executors.newCachedThreadPool();
  private static volatile boolean run = true;

  private class Cmd implements Runnable {
    @Override
    public void run() {
      try {
        Socket client = serverSocket.accept();
        acceptor.execute(new Cmd());
        BufferedReader d = new BufferedReader(
            new InputStreamReader(client.getInputStream()));
        while (run) {
          String line = d.readLine();
          if (line == null)
            break;
          System.out.println(line);
        }
      } catch (IOException e) {
      }
    }
  };

  public synchronized void start(RTaskI rTaskI) {
    if (tasks.isEmpty()) {
      port = Util.findFreeLocalPort(MAIN_PORT);
      try {
        serverSocket = new ServerSocket(port);
        acceptor.execute(new Cmd());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    tasks.add(rTaskI);
  }

  public synchronized void stop(RTaskI rTaskI) {
    tasks.remove(rTaskI);

    if (tasks.isEmpty()) {
      try {
        serverSocket.close();
        PrintOutServer.run = false;
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
