package org.ruminaq.runner.impl.debug;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.ruminaq.runner.impl.debug.events.IDebugEvent;

public interface IDebugIService extends Remote {
  void setDebugService(IDebugService ds) throws RemoteException;

  void modelEvent(IDebugEvent event) throws RemoteException;
}
