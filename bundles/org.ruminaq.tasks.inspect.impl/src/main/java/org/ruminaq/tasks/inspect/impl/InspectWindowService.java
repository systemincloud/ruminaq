package org.ruminaq.tasks.inspect.impl;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InspectWindowService extends Remote{
	void newValue(String value) throws RemoteException;
}
