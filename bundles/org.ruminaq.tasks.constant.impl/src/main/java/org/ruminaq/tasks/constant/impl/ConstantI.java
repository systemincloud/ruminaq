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
package org.ruminaq.tasks.constant.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

import org.ruminaq.model.ruminaq.DataType;
import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.runner.RunnerLoggerFactory;
import org.ruminaq.runner.impl.BasicTaskI;
import org.ruminaq.runner.impl.EmbeddedTaskI;
import org.ruminaq.tasks.constant.impl.service.ConstantServiceManager;
import org.ruminaq.tasks.constant.impl.strategy.Strategies;
import org.ruminaq.tasks.constant.model.constant.Constant;
import ch.qos.logback.classic.Logger;

public class ConstantI extends BasicTaskI {

  private static final Logger LOGGER = RunnerLoggerFactory
      .getLogger(ConstantI.class);

  private Constant model;

  private AbstractConstantStrategy strategy;

  public ConstantI(EmbeddedTaskI parent, Task task) {
    super(parent, task);
    setConstant(true);
    this.model = (Constant) task;
    DataType dataType = model.getOutputPort().get(0).getDataType().get(0);

    Optional<AbstractConstantStrategy> str = getStrategy(dataType,
        parent.replaceVariables(model.getValue()));
    if (str.isPresent()) {
      this.strategy = str.get();
      LOGGER.trace("chosen strategy is {}",
          this.strategy.getClass().getSimpleName());
    } else {
      LOGGER.error("Constant implementation not found for data type {}",
          dataType.getClass().getName());
    }
  }

  private Optional<AbstractConstantStrategy> getStrategy(DataType dt,
      String value) {
    LOGGER.trace("look for strategy for {}", dt);
    Optional<AbstractConstantStrategy> cs = ConstantServiceManager.INSTANCE
        .getStrategy(dt, value);
    if (cs.isPresent()) {
      return cs;
    }

    Optional<Strategies> strType = Strategies.getByDataType(dt);
    if (strType.isPresent()) {
      try {
        return Optional.of(strType.get().getStrategy()
            .getConstructor(ConstantI.class, String.class)
            .newInstance(this, value));
      } catch (InstantiationException | IllegalAccessException
          | IllegalArgumentException | InvocationTargetException
          | NoSuchMethodException | SecurityException e) {
        LOGGER.error("Couldn't create strategy", e);
      }
    }

    return Optional.empty();
  }

  @Override
  protected void executeConstant() {
    strategy.execute();
  }
}
