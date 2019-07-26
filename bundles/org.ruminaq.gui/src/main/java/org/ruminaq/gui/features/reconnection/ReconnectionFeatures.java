package org.ruminaq.gui.features.reconnection;

import java.util.Arrays;
import java.util.List;

import org.eclipse.graphiti.features.IReconnectionFeature;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.ReconnectionFeatureExtension;

@Component(property = { "service.ranking:Integer=5" })
public class ReconnectionFeatures implements ReconnectionFeatureExtension {

	@Override
	public List<Class<? extends IReconnectionFeature>> getFeatures() {
		return Arrays.asList(ReconnectionSimpleConnectionFeature.class);
	}
}
