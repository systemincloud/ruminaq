package org.ruminaq.tasks.javatask.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.ruminaq.model.model.ruminaq.Task;
import org.ruminaq.tasks.javatask.model.javatask.JavaTask;

public class DoubleClickFeatureFilter {

	public ICustomFeature filter(Task t, IFeatureProvider fp) {
		if(t instanceof JavaTask) {
			JavaTask jt = (JavaTask) t;
			String clazzName = jt.getImplementationClass();
			if(clazzName != null && !"".equals(clazzName)) return new DoubleClickFeature(fp);
		}
		return null;
	}
}
