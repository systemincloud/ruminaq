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
