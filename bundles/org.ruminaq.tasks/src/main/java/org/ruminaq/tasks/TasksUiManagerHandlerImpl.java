/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.osgi.framework.Version;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.ruminaq.tasks.api.ITaskUiApi;
import org.ruminaq.tasks.api.TasksUiManagerHandler;

@Component(immediate = true)
public class TasksUiManagerHandlerImpl implements TasksUiManagerHandler {

  private List<ITaskUiApi> tasks = new ArrayList<>();

  @Reference(cardinality = ReferenceCardinality.MULTIPLE,
      policy = ReferencePolicy.DYNAMIC)
  protected void bind(ITaskUiApi extension) {
    if (tasks == null) {
      tasks = new ArrayList<>();
    }
    tasks.add(extension);
  }

  protected void unbind(ITaskUiApi extension) {
    tasks.remove(extension);
  }

  public ITaskUiApi getTask(String prefix, Version version) {
    return tasks.stream().filter(t -> prefix.equals(t.getSymbolicName()))
//				.filter(t -> TaskProvider.compare(t.getVersion(), version))
        .findFirst().get();
  }

  @Override
  public List<ITaskUiApi> getTasks(String prefix) {
    return tasks.stream().filter(t -> prefix.equals(t.getSymbolicName()))
        .collect(Collectors.toList());
  }
}
