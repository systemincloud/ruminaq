package org.ruminaq.runner.dirmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface LocalService extends Remote {
	void registerRemote(String name, Remote object) throws RemoteException;
}
