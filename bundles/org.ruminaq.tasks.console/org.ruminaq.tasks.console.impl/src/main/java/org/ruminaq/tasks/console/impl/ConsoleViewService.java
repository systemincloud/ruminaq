package org.ruminaq.tasks.console.impl;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ConsoleViewService extends Remote {
  void newOutput(String out) throws RemoteException;

  void clearScreen() throws RemoteException;

  void deleteFirstLine() throws RemoteException;
}
