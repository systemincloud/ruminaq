package org.ruminaq.tasks.rtask.extension;

import java.util.List;

import org.javatuples.Pair;

public interface RTaskExtension {
    List<Pair<String, String>> getPythonTaskDatas();
}
