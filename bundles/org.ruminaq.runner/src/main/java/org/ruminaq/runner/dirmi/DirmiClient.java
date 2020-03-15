package org.ruminaq.runner.dirmi;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import org.cojen.dirmi.Environment;
import org.cojen.dirmi.Session;
import org.ruminaq.runner.RunnerLoggerFactory;

import org.slf4j.Logger;

public enum DirmiClient {
  INSTANCE;

  private final Logger logger = RunnerLoggerFactory
      .getLogger(DirmiClient.class);

  private Map<String, LocalService> localServices = new HashMap<>();

  private Environment env = new Environment();
  private RootService root;

  public void init(int mainPort) {
    logger.trace("Init with port {}", mainPort);
    try {
      Session mainSession = env.newSessionConnector("localhost", mainPort)
          .connect();
      root = (RootService) mainSession.receive();
      logger.trace("Init with port {}", mainPort);
    } catch (Exception e) {
      StringWriter sw = new StringWriter();
      e.printStackTrace(new PrintWriter(sw));
      logger.error("\n{}", sw.toString());
      System.err.println(RunnerLoggerFactory.ERROR_MSG);
      System.exit(1);
    }
  }

  public void registrationDone() {
    try {
      root.registrationDone();
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }

  public void debugInitialized() {
    try {
      root.debugInitialized();
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }

  public void register(String key, String name, Remote object) {
    logger.trace("key: {}, name: {}, object: {}", key, name,
        object.getClass().getSimpleName());
    LocalService ls = localServices.get(key);
    if (ls == null) {
      try {
        int port = root.getServicePort(key);
        Session session = env.newSessionConnector("localhost", port).connect();
        ls = (LocalService) session.receive();
        localServices.put(key, ls);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    if (ls != null) {
      try {
        ls.registerRemote(name, object);
      } catch (RemoteException e) {
        e.printStackTrace();
      }
    }
  }
}
