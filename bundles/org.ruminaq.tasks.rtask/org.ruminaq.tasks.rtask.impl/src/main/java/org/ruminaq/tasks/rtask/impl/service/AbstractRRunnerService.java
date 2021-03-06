/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.rtask.impl.service;

import java.io.IOException;
import java.util.Properties;

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
