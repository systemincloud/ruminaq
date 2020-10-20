/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.rtask;

import org.osgi.framework.Version;
import org.ruminaq.tasks.rtask.ui.IRTaskUiApi;
import org.ruminaq.tasks.rtask.ui.wizards.ICreateRTaskPage;
import org.ruminaq.tasks.rtask.wizards.CreateRTaskPage;

public class TaskUiApi implements IRTaskUiApi {

  public TaskUiApi(String symbolicName, Version version) {
  }

  @Override
  public ICreateRTaskPage getCreatePythonTaskPage() {
    return new CreateRTaskPage("System in Cloud - R Task");
  }
}
