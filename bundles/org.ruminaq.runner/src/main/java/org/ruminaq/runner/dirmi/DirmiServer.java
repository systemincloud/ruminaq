/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.runner.dirmi;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import org.cojen.dirmi.Environment;
import org.cojen.dirmi.Session;
import org.cojen.dirmi.SessionAcceptor;
import org.cojen.dirmi.SessionListener;
import org.ruminaq.logs.ModelerLoggerFactory;
import org.ruminaq.util.Util;

import org.slf4j.Logger;

public enum DirmiServer {
  INSTANCE;

  private final Logger logger = ModelerLoggerFactory
      .getLogger(DirmiServer.class);

  public static final int MAIN_PORT = 49152;

  private Map<String, Integer> sessionAcceptorsPorts = new HashMap<>();

  private Environment env;
  private Map<String, Remote> remotes = new HashMap<>();

  public void createSessionAcceptor(String key, final ClassLoader classLoader) {
    int port = Util.findFreeLocalPort(MAIN_PORT);
    try {
      final LocalService service = new LocalService() {
        @Override
        public void registerRemote(String name, Remote object) {
          remotes.put(name, object);
        }
      };
      final SessionAcceptor sessionAcceptor = env.newSessionAcceptor(port);
      sessionAcceptor.accept(new SessionListener() {
        @Override
        public void established(Session session) throws IOException {
          sessionAcceptor.accept(this);
          session.setClassLoader(classLoader);
          session.send(service);
        }

        @Override
        public void establishFailed(IOException exception) throws IOException {
          sessionAcceptor.accept(this);
        }

        @Override
        public void acceptFailed(IOException exception) throws IOException {
          logger.error("acceptFailed", exception);
        }
      });
      sessionAcceptorsPorts.put(key, port);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public int start(final RegistrationDoneListener registrationDoneListener) {
    int mainPort = Util.findFreeLocalPort(MAIN_PORT);
    try {
      env = new Environment();
      final RootService root = new RootService() {
        @Override
        public int getServicePort(String key) throws RemoteException {
          Integer port = sessionAcceptorsPorts.get(key);
          logger.trace("port for {}: {}", key, port);
          return port;
        }

        @Override
        public void debugInitialized() throws RemoteException {
          registrationDoneListener.debugInitialized();
        }

        @Override
        public void registrationDone() throws RemoteException {
          registrationDoneListener.registrationDone();
        }
      };
      final SessionAcceptor sessionAcceptor = env.newSessionAcceptor(mainPort);
      sessionAcceptor.accept(new SessionListener() {
        @Override
        public void established(Session session) throws IOException {
          sessionAcceptor.accept(this);
          session.setClassLoader(getClass().getClassLoader());
          session.send(root);
        }

        @Override
        public void establishFailed(IOException exception) throws IOException {
          sessionAcceptor.accept(this);
        }

        @Override
        public void acceptFailed(IOException exception) throws IOException {
          logger.error("acceptFailed", exception);
        }
      });
    } catch (IOException e) {
      e.printStackTrace();
    }
    return mainPort;
  }

  @SuppressWarnings("unchecked")
  public <T> T getRemote(String name, Class<T> clazz) {
    return (T) remotes.get(name);
  }

  public void stop() {
    try {
      env.close();
    } catch (IOException e) {
    }
    remotes = new HashMap<>();
    sessionAcceptorsPorts = new HashMap<>();
  }
}
