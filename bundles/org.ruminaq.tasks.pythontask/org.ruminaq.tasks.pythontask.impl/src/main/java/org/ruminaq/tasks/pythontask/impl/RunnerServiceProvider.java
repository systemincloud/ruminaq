/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.pythontask.impl;

import org.apache.commons.cli.Options;
import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.runner.impl.EmbeddedTaskI;
import org.ruminaq.runner.impl.TaskI;
import org.ruminaq.runner.service.AbstractRunnerService;
import org.ruminaq.tasks.pythontask.impl.jython.JythonProgramArguments;
import org.ruminaq.tasks.pythontask.model.pythontask.PythonTask;
import org.ruminaq.tasks.pythontask.model.pythontask.PythontaskPackage;

public final class RunnerServiceProvider extends AbstractRunnerService {
  @Override
  public void initModelPackages() {
    PythontaskPackage.eINSTANCE.getClass();
  }

  @Override
  public TaskI getImplementation(EmbeddedTaskI parent, Task task) {
    return task instanceof PythonTask ? new PythonTaskI(parent, task) : null;
  }

  @Override
  public void addOptions(Options options) {
    options.addOption(PythonTaskI.ATTR_PY_BIN, true, "");
    options.addOption(PythonTaskI.ATTR_PY_ENV, true, "");
    options.addOption(PythonTaskI.ATTR_PY_PATH, true, "");
    options.addOption(PythonTaskI.ATTR_PY_EXT_LIBS, true, "");
    options.addOption(PythonTaskI.ATTR_PY_TYPE, true, "");
    JythonProgramArguments.INSTANCE.addOptions(options);
  }
}
