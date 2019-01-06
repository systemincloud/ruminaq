package org.ruminaq.tasks.javatask.client;

import org.ruminaq.tasks.javatask.client.data.Data;

public class OutputPort {

    private JavaTaskListener jtListener;

    private String name;

    public OutputPort(String name) { this.name = name; }

    public  String getName()                        { return name; }
    public  void   putData(Data data)               { jtListener.putData(this, data, false); }
    public  void   putData(Data data, boolean copy) { jtListener.putData(this, data, copy); }
}
