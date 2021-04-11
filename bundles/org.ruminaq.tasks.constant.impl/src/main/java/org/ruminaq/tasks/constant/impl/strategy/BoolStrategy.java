/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.constant.impl.strategy;

import java.util.List;
import org.ruminaq.model.ruminaq.NumericUtil;
import org.ruminaq.runner.RunnerLoggerFactory;
import org.ruminaq.runner.impl.data.BoolI;
import org.ruminaq.tasks.constant.impl.AbstractConstantStrategy;
import org.ruminaq.tasks.constant.impl.ConstantI;
import org.ruminaq.tasks.constant.model.Port;
import ch.qos.logback.classic.Logger;

public class BoolStrategy extends AbstractConstantStrategy {

  private static final Logger LOGGER = RunnerLoggerFactory
      .getLogger(Complex32Strategy.class);

  public BoolStrategy(ConstantI task, String value) {
    super(task, value);
  }

  @Override
  public void execute() {
    LOGGER.trace("create Bool");
    List<Integer> dims = NumericUtil.getMultiDimsBoolDimensions(value);
    String[] vs = NumericUtil.getMutliDimsValues(value);
    int n = dims.stream().reduce(1, (a, b) -> a * b);
    boolean[] values = new boolean[n];
    for (int i = 0; i < n; i++) {
      values[i] = Boolean.parseBoolean(vs[i]);
    }
    task.putData(Port.OUT, new BoolI(values, dims));
  }
}
