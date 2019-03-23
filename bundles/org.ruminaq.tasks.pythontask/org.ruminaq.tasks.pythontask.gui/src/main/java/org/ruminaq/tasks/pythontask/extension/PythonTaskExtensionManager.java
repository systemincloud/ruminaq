package org.ruminaq.tasks.pythontask.extension;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.javatuples.Pair;
import org.osgi.framework.BundleContext;
import org.ruminaq.consts.Constants;
import org.ruminaq.util.ExtensionUtil;

public enum PythonTaskExtensionManager {

    INSTANCE;

    private List<PythonTaskExtension> extensions = new ArrayList<>();

    public void init(BundleContext ctx) {
        extensions.addAll(ExtensionUtil.getExtensions(PythonTaskExtension.class, Constants.EXTENSION_PREFIX, ctx));
    }

    public List<Pair<String, String>> getPythonTaskDatas() {
        List<Pair<String, String>> ret = new LinkedList<>();
        for(PythonTaskExtension srv : extensions) {
            List<Pair<String, String>> datas = srv.getPythonTaskDatas();
            if(datas != null) ret.addAll(datas);
        }
        return ret;
    }

    public String editPyDevProjectFile(String content) {
        String ret = content;
        for(PythonTaskExtension srv : extensions)
            ret = srv.editPyDevProjectFile(ret);
        return ret;
    }
}
