/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.tasks.randomgenerator.impl.strategy;

import java.util.List;

import org.eclipse.emf.common.util.EMap;
import org.ruminaq.runner.RunnerLoggerFactory;
import org.ruminaq.runner.impl.data.DataI;
import org.ruminaq.runner.impl.data.Float32I;
import org.ruminaq.tasks.randomgenerator.impl.RandomGeneratorI;
import org.ruminaq.tasks.randomgenerator.model.Port;
import org.ruminaq.util.NumericUtil;
import ch.qos.logback.classic.Logger;

public class Float32Strategy extends RandomGeneratorNumericStrategy {

  private final Logger logger = RunnerLoggerFactory
      .getLogger(Int32Strategy.class);

  public Float32Strategy(RandomGeneratorI task, EMap<String, String> eMap,
      List<Integer> dims) {
    super(task, eMap, dims);
  }

  @Override
  public void generateRandom(List<Integer> dims) {
    logger.trace("generating Float32");

    int n = 1;
    for (Integer i : dims)
      n *= i;
    float[] values = new float[n];

    for (int i = 0; i < n; i++)
      values[i] = (float) distribution.getNext();

    task.putData(Port.OUT, new Float32I(values, dims));
  }

  @Override
  protected boolean isValue(String value) {
    return NumericUtil.isMultiDimsNumeric(value);
  }

  @Override
  protected DataI getDataOfValue(String value, List<Integer> dims) {
    String[] vs = NumericUtil.getMutliDimsValues(value);
    int n = 1;
    for (Integer i : dims)
      n *= i;
    if (vs.length == 1) {
      float[] values = new float[n];
      for (int i = 0; i < n; i++)
        values[i] = Float.parseFloat(vs[0]);
      return new Float32I(values, dims);
    } else {
      List<Integer> dims2 = NumericUtil.getMutliDimsNumericDimensions(value);
      float[] values = new float[vs.length];
      for (int i = 0; i < vs.length; i++)
        values[i] = Float.parseFloat(vs[i]);
      return new Float32I(values, dims2);
    }
  }

}
