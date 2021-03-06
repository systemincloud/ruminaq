/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.rtask.ui.wizards;

import org.ruminaq.eclipse.usertask.model.userdefined.Module;
import org.ruminaq.eclipse.wizards.task.CreateUserDefinedTaskPage;

public interface ICreateRTaskPage extends CreateUserDefinedTaskPage {
  String generate(Module module);
}
