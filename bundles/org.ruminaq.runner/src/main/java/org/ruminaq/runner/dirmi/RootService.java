package org.ruminaq.runner.dirmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RootService extends Remote {
  int getServicePort(String key) throws RemoteException;

  void registrationDone() throws RemoteException;

  void debugInitialized() throws RemoteException;
}
