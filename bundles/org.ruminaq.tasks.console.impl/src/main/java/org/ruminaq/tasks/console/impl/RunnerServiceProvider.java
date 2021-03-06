/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.tasks.console.impl;

import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.runner.impl.EmbeddedTaskI;
import org.ruminaq.runner.impl.TaskI;
import org.ruminaq.runner.service.AbstractRunnerService;
import org.ruminaq.tasks.console.model.console.Console;
import org.ruminaq.tasks.console.model.console.ConsolePackage;

public final class RunnerServiceProvider extends AbstractRunnerService {

  @Override
  public void initModelPackages() {
    ConsolePackage.eINSTANCE.getClass();
  }

  @Override
  public TaskI getImplementation(EmbeddedTaskI parent, Task task) {
    return task instanceof Console ? new ConsoleI(parent, task) : null;
  }
}
