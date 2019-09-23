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

import java.util.Arrays;
import java.util.Optional;
import org.ruminaq.model.dt.Text;
import org.ruminaq.model.dt.Bool;
import org.ruminaq.model.dt.Complex32;
import org.ruminaq.model.dt.Complex64;
import org.ruminaq.model.dt.Control;
import org.ruminaq.model.dt.Int32;
import org.ruminaq.model.dt.Int64;
import org.ruminaq.model.dt.Float32;
import org.ruminaq.model.dt.Float64;
import org.ruminaq.model.dt.Decimal;
import org.ruminaq.model.ruminaq.DataType;
import org.ruminaq.tasks.constant.impl.AbstractConstantStrategy;

public enum Strategies {
  TEXT(Text.class, TextStrategy.class), BOOL(Bool.class, BoolStrategy.class),
  COMPLEX32(Complex32.class, Complex32Strategy.class),
  COMPLEX64(Complex64.class, Complex64Strategy.class),
  CONTROL(Control.class, ControlStrategy.class),
  INT32(Int32.class, Int32Strategy.class),
  INT64(Int64.class, Int64Strategy.class),
  FLOAT32(Float32.class, Float32Strategy.class),
  FLOAT64(Float64.class, Float64Strategy.class),
  DECIMAL(Decimal.class, DecimalStrategy.class),;

  private Class<? extends DataType> dataType;
  private Class<? extends AbstractConstantStrategy> strategy;

  Strategies(Class<? extends DataType> dataType,
      Class<? extends AbstractConstantStrategy> strategy) {
    this.dataType = dataType;
    this.strategy = strategy;
  }

  public Class<? extends DataType> getDataType() {
    return dataType;
  }

  public Class<? extends AbstractConstantStrategy> getStrategy() {
    return strategy;
  }

  public static Optional<Strategies> getByDataType(DataType dt) {
    return Arrays.stream(Strategies.values())
        .filter(s -> s.getDataType().isAssignableFrom(dt.getClass()))
        .findFirst();
  }
}
