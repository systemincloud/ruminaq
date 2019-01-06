package org.ruminaq.tasks.rtask;

import java.util.LinkedList;
import java.util.List;

import org.javatuples.Pair;
import org.ruminaq.tasks.rtask.extension.RTaskExtensionManager;

public enum RData {
    INSTANCE;

    public List<Pair<String, String>> getRTaskDatas() {
        List<Pair<String, String>> ret = new LinkedList<>();
        ret.add(new Pair<>("sicrtask", "Bool"));
        ret.add(new Pair<>("sicrtask", "Complex32"));
        ret.add(new Pair<>("sicrtask", "Complex64"));
        ret.add(new Pair<>("sicrtask", "Control"));
        ret.add(new Pair<>("sicrtask", "Decimal"));
        ret.add(new Pair<>("sicrtask", "Int32"));
        ret.add(new Pair<>("sicrtask", "Int64"));
        ret.add(new Pair<>("sicrtask", "Float32"));
        ret.add(new Pair<>("sicrtask", "Float64"));
        ret.add(new Pair<>("sicrtask", "Raw"));
        ret.add(new Pair<>("sicrtask", "Text"));
        ret.addAll(RTaskExtensionManager.INSTANCE.getPythonTaskDatas());
        return ret;
    }
}
