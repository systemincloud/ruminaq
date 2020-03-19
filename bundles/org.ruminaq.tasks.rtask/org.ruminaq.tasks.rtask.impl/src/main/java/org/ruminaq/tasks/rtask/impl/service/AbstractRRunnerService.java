package org.ruminaq.tasks.rtask.impl.service;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.ruminaq.runner.impl.data.DataI;

//import de.walware.rj.data.RObject;

public abstract class AbstractRRunnerService implements RRunnerService {

  protected Properties prop = new Properties();

  {
    try {
      prop.load(this.getClass().getResourceAsStream("bundle-info.properties"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

//  @Override
//  public RObject toRData(DataI dataI, RObject rDims) {
//    return null;
//  }

//  @Override
//  public DataI fromRData(RObject data, RObject[] pyValues, List<Integer> dims) {
//    return null;
//  }
}
