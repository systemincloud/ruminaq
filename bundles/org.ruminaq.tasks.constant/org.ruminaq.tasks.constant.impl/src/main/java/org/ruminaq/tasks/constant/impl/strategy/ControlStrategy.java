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

import org.ruminaq.runner.RunnerLoggerFactory;
import org.ruminaq.runner.impl.data.ControlI;
import org.ruminaq.tasks.constant.impl.AbstractConstantStrategy;
import org.ruminaq.tasks.constant.impl.ConstantI;
import org.ruminaq.tasks.constant.impl.Port;
import org.slf4j.Logger;

public class ControlStrategy extends AbstractConstantStrategy {

  private static final Logger LOGGER = RunnerLoggerFactory
      .getLogger(ControlStrategy.class);

  public ControlStrategy(ConstantI task, String value) {
    super(task, value);
  }

  @Override
  public void execute() {
    LOGGER.trace("create Control");
    task.putData(Port.OUT, new ControlI());
  }
}
