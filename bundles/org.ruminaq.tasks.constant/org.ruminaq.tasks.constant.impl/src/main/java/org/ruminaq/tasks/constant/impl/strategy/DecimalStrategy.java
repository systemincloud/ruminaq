/*
 * (C) Copyright 2018 Marek Jagielski.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ruminaq.tasks.constant.impl.strategy;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import org.ruminaq.runner.RunnerLoggerFactory;
import org.ruminaq.runner.impl.data.DecimalI;
import org.ruminaq.tasks.constant.impl.AbstractConstantStrategy;
import org.ruminaq.tasks.constant.impl.ConstantI;
import org.ruminaq.tasks.constant.impl.Port;
import org.ruminaq.util.NumericUtil;
import org.slf4j.Logger;

public class DecimalStrategy extends AbstractConstantStrategy {

  private static final Logger LOGGER = RunnerLoggerFactory
      .getLogger(Int32Strategy.class);

  public static final String DEFAULT_VALUE = "0";

  public DecimalStrategy(ConstantI task, String value) {
    super(task, value);
  }

  @Override
  public void execute() {
    LOGGER.trace("create Int32");
    if (NumericUtil.isMultiDimsNumeric(value)) {
      List<Integer> dims = NumericUtil.getMutliDimsNumericDimensions(value);
      String[] vs = NumericUtil.getMutliDimsValues(value);
      List<BigDecimal> values = new LinkedList<>();
      for (String v : vs) {
        values.add(new BigDecimal(v));
      }
      task.putData(Port.OUT, new DecimalI(values, dims));
    }
  }
}
