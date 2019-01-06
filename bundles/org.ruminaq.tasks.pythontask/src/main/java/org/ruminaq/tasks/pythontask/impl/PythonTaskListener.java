package org.ruminaq.tasks.pythontask.impl;

import org.ruminaq.runner.impl.data.DataI;

public interface PythonTaskListener {

    // PythonTask
    void    externalData(int i);
    void    sleep(long l);
    void    generatorPause();
    boolean generatorIsPaused();
    void    generatorResume();
    void    generatorEnd();
    void    exitRunner();
    String  getParameter(String key);
    Object  runExpression(String expression);
    void    log(String level, String msg);

    // InputPort
    DataI getData(String ipName);
    void  cleanQueue(String ipName);

    // OutputPort
    void putData(String opName, DataI dataI);
}
