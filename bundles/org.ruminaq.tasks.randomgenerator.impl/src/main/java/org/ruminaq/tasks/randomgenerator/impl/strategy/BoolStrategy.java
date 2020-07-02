/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.tasks.randomgenerator.impl.strategy;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.eclipse.emf.common.util.EMap;
import org.ruminaq.runner.RunnerLoggerFactory;
import org.ruminaq.runner.impl.data.BoolI;
import org.ruminaq.tasks.randomgenerator.impl.RandomGeneratorI;
import org.ruminaq.tasks.randomgenerator.model.Port;

import ch.qos.logback.classic.Logger;

/**
 *
 * @author Marek Jagielski
 */
public class BoolStrategy extends RandomGeneratorStrategy {

  private final Logger logger = RunnerLoggerFactory
      .getLogger(BoolStrategy.class);

  public BoolStrategy(RandomGeneratorI task, EMap<String, String> eMap) {
    super(task);
  }

  @Override
  public void generate(List<Integer> dims) {
    logger.trace("generating Bool");

    int n = dims.stream().reduce(1, (a, b) -> a * b);
    boolean[] values = new boolean[n];

    for (int i = 0; i < n; i++) {
      values[i] = ThreadLocalRandom.current().nextBoolean();
    }

    task.putData(Port.OUT, new BoolI(values, dims));
  }
}
