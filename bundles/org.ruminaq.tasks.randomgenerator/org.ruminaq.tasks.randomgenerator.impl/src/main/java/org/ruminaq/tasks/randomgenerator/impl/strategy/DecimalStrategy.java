/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.tasks.randomgenerator.impl.strategy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.common.util.EMap;
import org.ruminaq.runner.RunnerLoggerFactory;
import org.ruminaq.runner.impl.data.DataI;
import org.ruminaq.runner.impl.data.DecimalI;
import org.ruminaq.tasks.randomgenerator.impl.Port;
import org.ruminaq.tasks.randomgenerator.impl.RandomGeneratorI;
import org.ruminaq.util.NumericUtil;
import org.ruminaq.util.RandomUtil;
import org.slf4j.Logger;

public class DecimalStrategy extends RandomGeneratorNumericStrategy {

  private final Logger logger = RunnerLoggerFactory
      .getLogger(Int32Strategy.class);

  public static final String DECIMAL_NUMBER = "DECIMAL_NUMBER";
  public static final int DEFAULT_DECIMAL_NUMBER = 2;

  private String textScale;
  private int scale = -1;

  public DecimalStrategy(RandomGeneratorI task, EMap<String, String> eMap,
      List<Integer> dims) {
    super(task, eMap, dims);
    this.textScale = eMap.get(DECIMAL_NUMBER);
    if (this.textScale == null)
      this.textScale = Integer.toString(DEFAULT_DECIMAL_NUMBER);
    if (!RandomUtil
        .containsRandom(task.getParent().replaceVariables(textScale)))
      this.scale = Integer
          .parseInt(task.getParent().replaceVariables(textScale));
  }

  @Override
  public void generateRandom(List<Integer> dims) {
    logger.trace("generating Int32");

    int n = 1;
    for (Integer i : dims)
      n *= i;
    List<BigDecimal> values = new ArrayList<>(n);

    int scale = this.scale != -1 ? this.scale
        : Integer.parseInt(RandomUtil.replaceRandoms(
            task.getParent().replaceVariables(textScale), true, true));

    for (int i = 0; i < n; i++)
      values.add(new BigDecimal(distribution.getNext()).setScale(scale,
          BigDecimal.ROUND_HALF_UP));

    task.putData(Port.OUT, new DecimalI(values, dims));
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
    List<BigDecimal> values = new LinkedList<>();

    if (vs.length == 1) {
      for (int i = 0; i < n; i++)
        values.add(new BigDecimal(vs[0]));
      return new DecimalI(values, dims);
    } else {
      List<Integer> dims2 = NumericUtil.getMutliDimsNumericDimensions(value);
      for (int i = 0; i < vs.length; i++)
        values.add(new BigDecimal(vs[i]));
      return new DecimalI(values, dims2);
    }
  }
}
