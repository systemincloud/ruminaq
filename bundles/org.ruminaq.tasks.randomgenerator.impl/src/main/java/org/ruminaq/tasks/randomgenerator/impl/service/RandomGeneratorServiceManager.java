/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.tasks.randomgenerator.impl.service;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.eclipse.emf.common.util.EMap;
import org.ruminaq.model.ruminaq.DataType;
import org.ruminaq.tasks.randomgenerator.impl.strategy.RandomGeneratorStrategy;

/**
 *
 * @author Marek Jagielski
 */
public enum RandomGeneratorServiceManager {

  INSTANCE;

  private List<RandomGeneratorService> services = new ArrayList<>();

  private RandomGeneratorServiceManager() {
    ServiceLoader<RandomGeneratorService> sl = ServiceLoader
        .load(RandomGeneratorService.class);
    for (RandomGeneratorService srv : sl) {
      services.add(srv);
    }
  }

  public RandomGeneratorStrategy getStrategy(DataType dt,
      EMap<String, String> eMap) {
    for (RandomGeneratorService srv : services) {
      RandomGeneratorStrategy strategy = srv.getStrategy(dt, eMap);
      if (strategy != null)
        return strategy;
    }
    return null;
  }
}
