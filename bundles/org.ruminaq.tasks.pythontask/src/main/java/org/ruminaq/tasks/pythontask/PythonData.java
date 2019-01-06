package org.ruminaq.tasks.pythontask;

import java.util.LinkedList;
import java.util.List;

import org.javatuples.Pair;
import org.ruminaq.tasks.pythontask.extension.PythonTaskExtensionManager;

public enum PythonData {
    INSTANCE;

    public List<Pair<String, String>> getPythonTaskDatas() {
        List<Pair<String, String>> ret = new LinkedList<>();
        ret.add(new Pair<>("sicpythontask.data.", "Bool"));
        ret.add(new Pair<>("sicpythontask.data.", "Complex32"));
        ret.add(new Pair<>("sicpythontask.data.", "Complex64"));
        ret.add(new Pair<>("sicpythontask.data.", "Control"));
        ret.add(new Pair<>("sicpythontask.data.", "Decimal"));
        ret.add(new Pair<>("sicpythontask.data.", "Int32"));
        ret.add(new Pair<>("sicpythontask.data.", "Int64"));
        ret.add(new Pair<>("sicpythontask.data.", "Float32"));
        ret.add(new Pair<>("sicpythontask.data.", "Float64"));
        ret.add(new Pair<>("sicpythontask.data.", "Raw"));
        ret.add(new Pair<>("sicpythontask.data.", "Text"));
        ret.addAll(PythonTaskExtensionManager.INSTANCE.getPythonTaskDatas());
        return ret;
    }
}
