package org.ruminaq.tasks.pythontask.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.ruminaq.model.sic.Task;

public class DoubleClickFeatureFilter {

	public ICustomFeature filter(Task t, IFeatureProvider fp) {
		if(t instanceof PythonTask) {
			PythonTask jt = (PythonTask) t;
			String clazzName = jt.getImplementation();
			if(clazzName != null && !"".equals(clazzName)) return new DoubleClickFeature(fp);
		}
		return null;
	}
}
