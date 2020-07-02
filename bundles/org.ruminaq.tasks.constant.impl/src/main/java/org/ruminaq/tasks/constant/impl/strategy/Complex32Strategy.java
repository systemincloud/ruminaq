/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
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
import java.util.regex.Pattern;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexFormat;
import org.ruminaq.runner.RunnerLoggerFactory;
import org.ruminaq.runner.impl.data.Complex32I;
import org.ruminaq.tasks.constant.impl.AbstractConstantStrategy;
import org.ruminaq.tasks.constant.impl.ConstantI;
import org.ruminaq.tasks.constant.model.Port;
import org.ruminaq.util.NumericUtil;
import ch.qos.logback.classic.Logger;

public class Complex32Strategy extends AbstractConstantStrategy {

  private static final Logger LOGGER = RunnerLoggerFactory
      .getLogger(Complex32Strategy.class);

  private static final Pattern REAL_PATTERN = Pattern
      .compile("[+-]?[0-9]*\\.?[0-9]+");
  private static final Pattern IMAG_PATTERN = Pattern
      .compile("[+-]?[0-9]*\\.?[0-9]+i");

  public Complex32Strategy(ConstantI task, String value) {
    super(task, value);
  }

  @Override
  public void execute() {
    LOGGER.trace("create Complex32");
    List<Integer> dims = NumericUtil.getMultiDimsComplexDimensions(value);
    String[] vs = NumericUtil.getMutliDimsValues(value);
    int n = dims.stream().reduce(1, (a, b) -> a * b);
    float[] real = new float[n];
    float[] imag = new float[n];
    for (int i = 0; i < n; i++) {
      String s = vs[i];
      if (REAL_PATTERN.matcher(s).matches()) {
        real[i] = Float.parseFloat(s);
      } else if (IMAG_PATTERN.matcher(s).matches()) {
        imag[i] = Float.parseFloat(s.replace("i", ""));
      } else {
        Complex c = new ComplexFormat().parse(s);
        real[i] = (float) c.getReal();
        imag[i] = (float) c.getImaginary();
      }
    }
    task.putData(Port.OUT, new Complex32I(real, imag, dims));
  }
}
