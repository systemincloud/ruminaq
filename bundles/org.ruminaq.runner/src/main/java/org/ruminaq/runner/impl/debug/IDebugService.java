package org.ruminaq.runner.impl.debug;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.ruminaq.runner.impl.debug.events.IDebugEvent;

public interface IDebugService extends Remote {
  void debuggerEvent(IDebugEvent event) throws RemoteException;
}
