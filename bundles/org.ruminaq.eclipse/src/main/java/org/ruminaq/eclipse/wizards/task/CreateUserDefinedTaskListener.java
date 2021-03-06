/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.wizards.task;

/**
 * Use to notify when creation of task is finished.
 *
 * @author Marek Jagielski
 */
public interface CreateUserDefinedTaskListener {
  void setImplementation(String resourcePath);
}
