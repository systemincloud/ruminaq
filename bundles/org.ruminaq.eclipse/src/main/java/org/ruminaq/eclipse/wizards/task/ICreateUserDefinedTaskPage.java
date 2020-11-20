/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.wizards.task;

import org.eclipse.jface.wizard.IWizardPage;
import org.ruminaq.eclipse.usertask.model.userdefined.Module;

/**
 * Model of a newly created Task.
 *
 * @author Marek Jagielski
 */
public interface ICreateUserDefinedTaskPage extends IWizardPage {
  Module getModel();
}
