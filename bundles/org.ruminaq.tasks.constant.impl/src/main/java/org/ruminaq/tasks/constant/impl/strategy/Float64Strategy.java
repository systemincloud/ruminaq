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

import java.util.List;

import org.ruminaq.runner.RunnerLoggerFactory;
import org.ruminaq.runner.impl.data.Float64I;
import org.ruminaq.tasks.constant.impl.AbstractConstantStrategy;
import org.ruminaq.tasks.constant.impl.ConstantI;
import org.ruminaq.tasks.constant.model.Port;
import org.ruminaq.util.NumericUtil;
import ch.qos.logback.classic.Logger;

public class Float64Strategy extends AbstractConstantStrategy {

  private static final Logger LOGGER = RunnerLoggerFactory
      .getLogger(Int32Strategy.class);

  public static final String DEFAULT_VALUE = "0";

  public Float64Strategy(ConstantI task, String value) {
    super(task, value);
  }

  @Override
  public void execute() {
    LOGGER.trace("create Float64");
    List<Integer> dims = NumericUtil.getMutliDimsNumericDimensions(value);
    String[] vs = NumericUtil.getMutliDimsValues(value);
    int n = dims.stream().reduce(1, (a, b) -> a * b);
    double[] values = new double[n];
    for (int i = 0; i < n; i++) {
      values[i] = Double.parseDouble(vs[i]);
    }
    task.putData(Port.OUT, new Float64I(values, dims));
  }
}
