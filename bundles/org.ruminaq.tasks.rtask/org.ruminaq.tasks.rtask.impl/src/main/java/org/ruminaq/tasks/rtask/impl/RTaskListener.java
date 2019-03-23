package org.ruminaq.tasks.rtask.impl;

import org.ruminaq.runner.impl.data.DataI;

public interface RTaskListener {

    // PythonTask
    void    externalData(int i);
    void    sleep(long l);
    void    generatorPause();
    boolean generatorIsPaused();
    void    generatorResume();
    void    generatorEnd();
    void    exitRunner();
    String  getParameter(String key);
    String  runExpression(String expression);
    void    log(String level, String msg);

    // InputPort
    DataI getData(String ipName, String datatype);
    void       cleanQueue(String ipName);

    // OutputPort
    void putData(String opName, DataI data);
}
