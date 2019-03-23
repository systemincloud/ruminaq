package org.ruminaq.tasks.rtask.extension;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.javatuples.Pair;
import org.osgi.framework.BundleContext;
import org.ruminaq.consts.Constants;
import org.ruminaq.util.ExtensionUtil;

public enum RTaskExtensionManager {

    INSTANCE;

    private List<RTaskExtension> extensions = new ArrayList<>();

    public void init(BundleContext ctx) {
        extensions.addAll(ExtensionUtil.getExtensions(RTaskExtension.class, Constants.EXTENSION_PREFIX, ctx));
    }

    public List<Pair<String, String>> getPythonTaskDatas() {
        List<Pair<String, String>> ret = new LinkedList<>();
        for(RTaskExtension srv : extensions) {
            List<Pair<String, String>> datas = srv.getPythonTaskDatas();
            if(datas != null) ret.addAll(datas);
        }
        return ret;
    }
}
