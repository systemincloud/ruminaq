package com.test;

import org.ruminaq.tasks.javatask.api.InputPort;
import org.ruminaq.tasks.javatask.api.JavaTask;
import org.ruminaq.tasks.javatask.api.OutputPort;
import org.ruminaq.tasks.javatask.api.annotations.InputPortInfo;
import org.ruminaq.tasks.javatask.api.annotations.JavaTaskInfo;
import org.ruminaq.tasks.javatask.api.annotations.OutputPortInfo;
import org.ruminaq.tasks.javatask.api.data.Int64;
import org.ruminaq.tasks.javatask.api.data.Bool;

@JavaTaskInfo(atomic=true, generator=true, externalSource=true)
public class MyJavaTaskClass extends JavaTask {

    @InputPortInfo(name = "RST", dataType = Int64.class, asynchronous=true)
    public InputPort rst;

    @InputPortInfo(name = "In1", dataType = Int64.class)
    public InputPort in1;
    @InputPortInfo(name = "In2", dataType = Int64.class)
    public InputPort in2;
    @OutputPortInfo(name = "Out", dataType = Bool.class)
    public OutputPort out;

    @Override
    public void execute(int grp) {
        /*
         * Here you define what's
         * happen when all synchronous ports from group
         * received data or your generator code
         * is ready to action.
         */
    }

    @Override
    public void executeAsync(InputPort asyncIn) {
        /*
         * Here you define what's
         * happen when a asynchronous port
         * received data. Port is in argument
         */
    }

    @Override
    public void executeExtSrc() {
        /*
         * Here is made an action after you
         * notify Modeler that this Task is
         * ready to run
         */
    }

    @Override
    public void generate() {
        /*
         * Here is made a periodical action
         * as part of generator
         */
    }
}
