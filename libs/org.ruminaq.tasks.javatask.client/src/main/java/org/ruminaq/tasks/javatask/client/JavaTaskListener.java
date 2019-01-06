package org.ruminaq.tasks.javatask.client;

import org.ruminaq.tasks.javatask.client.data.Data;
import org.slf4j.Logger;

public interface JavaTaskListener {

    // JavaTask
    void    externalData(int i);
    void    sleep(long l);
    void    generatorPause();
    boolean generatorIsPaused();
    void    generatorResume();
    void    generatorEnd();
    void    exitRunner();
    String  getParameter(String key);
    Object  runExpression(String expression);
    Logger  log();

    // InputPort
    <T extends Data> T    getData(InputPort ip, Class<T> type);
                     void cleanQueue(InputPort ip);
    // OutputPort
                     void putData(OutputPort outputPort, Data data, boolean copy);
}
