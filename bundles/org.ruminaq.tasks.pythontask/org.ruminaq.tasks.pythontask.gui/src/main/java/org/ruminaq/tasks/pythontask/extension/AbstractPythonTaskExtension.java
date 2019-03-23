package org.ruminaq.tasks.pythontask.extension;

import java.util.LinkedList;
import java.util.List;

import org.javatuples.Pair;

public abstract class AbstractPythonTaskExtension implements PythonTaskExtension {
    public List<Pair<String, String>> getPythonTaskDatas()                 { return new LinkedList<>(); }
    public String                     editPyDevProjectFile(String content) { return content; }
}
