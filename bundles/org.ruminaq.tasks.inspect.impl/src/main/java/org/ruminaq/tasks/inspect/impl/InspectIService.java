package org.ruminaq.tasks.inspect.impl;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InspectIService extends Remote {
	void   addListener(InspectWindowService tvWindowService)    throws RemoteException;
	void   removeListener(InspectWindowService tvWindowService) throws RemoteException;
	String getLastValue()                                       throws RemoteException;
	
}
