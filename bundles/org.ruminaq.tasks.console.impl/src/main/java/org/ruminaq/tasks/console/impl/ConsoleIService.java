package org.ruminaq.tasks.console.impl;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ConsoleIService extends Remote {
	void newCommand(String cmd) throws RemoteException;

	void addListener(ConsoleViewService viewApi) throws RemoteException;

	void removeListener(ConsoleViewService viewApi) throws RemoteException;

	String getHistory() throws RemoteException;

	void clearHistory() throws RemoteException;
}
