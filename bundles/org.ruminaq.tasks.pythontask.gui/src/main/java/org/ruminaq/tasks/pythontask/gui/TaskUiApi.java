/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.pythontask.gui;

import org.osgi.framework.Version;
import org.ruminaq.tasks.pythontask.gui.wizards.CreatePythonTaskPage;
import org.ruminaq.tasks.pythontask.ui.IPythonTaskUiApi;
import org.ruminaq.tasks.pythontask.ui.wizards.ICreatePythonTaskPage;

public class TaskUiApi implements IPythonTaskUiApi {

  public TaskUiApi(String symbolicName, Version version) {
  }

  @Override
  public ICreatePythonTaskPage getCreatePythonTaskPage() {
    return new CreatePythonTaskPage("System in Cloud - Python Task");
  }
}
