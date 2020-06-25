package org.ruminaq.tasks.javatask.impl.service;

import org.ruminaq.runner.impl.data.DataI;
import org.ruminaq.tasks.javatask.client.data.Data;

public interface JavaTaskService {
  Data toJavaTaskData(DataI dataI, Class<? extends Data> to);

  Data toJavaTaskData(DataI dataI);

  DataI fromJavaTaskData(Data data, boolean copy);
}
