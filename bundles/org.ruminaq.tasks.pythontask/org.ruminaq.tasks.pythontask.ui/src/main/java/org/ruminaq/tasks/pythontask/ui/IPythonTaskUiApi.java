/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.pythontask.ui;

import org.ruminaq.tasks.pythontask.ui.wizards.ICreatePythonTaskPage;

public interface IPythonTaskUiApi {
  ICreatePythonTaskPage getCreatePythonTaskPage();
}
