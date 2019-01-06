package org.ruminaq.tasks.javatask.client;

import org.ruminaq.tasks.javatask.client.data.Data;

public class InputPort {

	private JavaTaskListener jtListener;
	
	private String name;
	
    public InputPort(String name) { this.name = name; }
	
	public                   String getName()               { return name; }
    public  <T extends Data> T      getData(Class<T> clazz) { return jtListener.getData(this, clazz); }
    public                   void   cleanQueue()            {        jtListener.cleanQueue(this); }
}
