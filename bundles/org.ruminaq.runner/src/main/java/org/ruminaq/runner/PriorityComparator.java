/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.runner;

import java.util.Comparator;

import org.ruminaq.runner.impl.TaskI;

public class PriorityComparator implements Comparator<TaskI> {

  @Override
  public int compare(TaskI t1, TaskI t2) {
    return t1.getPriority() < t2.getPriority() ? -1
        : t1.getPriority() > t2.getPriority() ? 1 : 0;
  }
}
