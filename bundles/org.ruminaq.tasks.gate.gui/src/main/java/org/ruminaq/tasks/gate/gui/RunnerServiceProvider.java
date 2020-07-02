/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.gate.gui;

import org.ruminaq.runner.service.AbstractRunnerService;
import org.ruminaq.tasks.gate.model.gate.GatePackage;

public final class RunnerServiceProvider extends AbstractRunnerService {
  @Override
  public void initModelPackages() {
    GatePackage.eINSTANCE.getClass();
  }
}
