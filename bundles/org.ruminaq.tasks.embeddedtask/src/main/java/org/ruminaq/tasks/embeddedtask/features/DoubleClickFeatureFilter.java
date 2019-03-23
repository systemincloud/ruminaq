package org.ruminaq.tasks.embeddedtask.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.ruminaq.model.ruminaq.EmbeddedTask;
import org.ruminaq.model.ruminaq.Task;

public class DoubleClickFeatureFilter {

	public ICustomFeature filter(Task t, IFeatureProvider fp) {
		if(t instanceof EmbeddedTask) {
			EmbeddedTask et = (EmbeddedTask) t;
			String clazzName = et.getImplementationTask();
			if(clazzName != null && !"".equals(clazzName)) return new DoubleClickFeature(fp);
		}
		return null;
	}
}
