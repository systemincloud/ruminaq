package org.ruminaq.tasks.rtask.extension;

import java.util.LinkedList;
import java.util.List;

import org.javatuples.Pair;

public abstract class AbstractRTaskExtension implements RTaskExtension {
    public List<Pair<String, String>> getRTaskDatas() { return new LinkedList<>(); }
}
