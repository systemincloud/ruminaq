/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.api;

import java.util.Collection;

import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.swt.widgets.Composite;
import org.ruminaq.model.ruminaq.Task;

public interface TaskManagerHandler {

  Collection<ITaskApi> getProjectVersionTasks();

  Collection<ITaskApi> getTasks();

  void addToGeneralTab(Composite composite, final Task task,
      final TransactionalEditingDomain ed);

}
