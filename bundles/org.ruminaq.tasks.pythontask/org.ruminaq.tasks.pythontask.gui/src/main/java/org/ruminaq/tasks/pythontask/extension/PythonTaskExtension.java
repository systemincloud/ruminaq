package org.ruminaq.tasks.pythontask.extension;

import java.util.List;

import org.javatuples.Pair;

public interface PythonTaskExtension {
    List<Pair<String, String>> getPythonTaskDatas();
    String                     editPyDevProjectFile(String ret);
}
