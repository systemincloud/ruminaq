package org.ruminaq.gui.features.doubleclick;

import java.util.Arrays;
import java.util.List;

import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.DoubleClickFeatureExtension;

@Component(property = { "service.ranking:Integer=5" })
public class DoubleClickFeatureImpl implements DoubleClickFeatureExtension {

	@Override
	public List<Class<? extends ICustomFeature>> getFeatures() {
		return Arrays.asList(DoubleClickBaseElementFeature.class);
	}
}
