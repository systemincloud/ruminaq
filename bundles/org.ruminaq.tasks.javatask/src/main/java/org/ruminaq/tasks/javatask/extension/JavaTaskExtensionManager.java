package org.ruminaq.tasks.javatask.extension;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.ruminaq.consts.Constants;
import org.ruminaq.tasks.javatask.client.data.Data;
import org.ruminaq.util.ExtensionUtil;

public enum JavaTaskExtensionManager {

	INSTANCE;
	
	private List<JavaTaskExtension> extensions = new ArrayList<>();
	
	public void init(BundleContext ctx) {
        extensions.addAll(ExtensionUtil.getExtensions(JavaTaskExtension.class, Constants.EXTENSION_PREFIX, ctx));
	}

	public List<Class<? extends Data>> getJavaTaskDatas() {
		List<Class<? extends Data>> ret = new LinkedList<>();
		for(JavaTaskExtension srv : extensions) {
			List<Class<? extends Data>> datas = srv.getJavaTaskDatas();
			if(datas != null) ret.addAll(datas);
		}
		return ret;
	}
}
