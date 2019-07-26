package org.ruminaq.gui.features.delete;

import java.util.Arrays;
import java.util.List;

import org.eclipse.graphiti.features.IDeleteFeature;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.DeleteFeature;
import org.ruminaq.gui.api.DeleteFeatureExtension;

@Component(property = { "service.ranking:Integer=5" })
public class DeleteFeatures implements DeleteFeatureExtension {

	@Override
	public List<Class<? extends IDeleteFeature>> getFeatures() {
		return Arrays.asList(DeleteLabelFeature.class, DeleteForbiddenFeature.class,
		    DeleteFeature.class);
	}
}
