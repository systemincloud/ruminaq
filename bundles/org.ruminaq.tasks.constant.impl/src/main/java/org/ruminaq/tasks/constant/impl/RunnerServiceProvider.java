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

import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.runner.impl.EmbeddedTaskI;
import org.ruminaq.runner.impl.TaskI;
import org.ruminaq.runner.service.AbstractRunnerService;
import org.ruminaq.tasks.constant.impl.ConstantI;
import org.ruminaq.tasks.constant.model.constant.Constant;
import org.ruminaq.tasks.constant.model.constant.ConstantPackage;

public final class RunnerServiceProvider extends AbstractRunnerService {

  @Override
  public void initModelPackages() {
    ConstantPackage.eINSTANCE.getClass();
  }

  @Override
  public TaskI getImplementation(EmbeddedTaskI parent, Task task) {
    return task instanceof Constant ? new ConstantI(parent, task) : null;
  }
}
