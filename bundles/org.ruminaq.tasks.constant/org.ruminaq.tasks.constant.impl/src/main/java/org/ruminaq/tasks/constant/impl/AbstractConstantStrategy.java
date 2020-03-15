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

import org.ruminaq.runner.RunnerLoggerFactory;
import ch.qos.logback.classic.Logger;

public abstract class AbstractConstantStrategy {

  private static final Logger LOGGER = RunnerLoggerFactory
      .getLogger(AbstractConstantStrategy.class);

  protected ConstantI task;
  protected String value;

  public AbstractConstantStrategy(ConstantI task, String value) {
    this.task = task;
    this.value = value;
    LOGGER.trace("Value of constant is: {}", value);
  }

  public abstract void execute();
}
