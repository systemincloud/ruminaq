package org.ruminaq.tasks.constant;

import java.util.Arrays;
import java.util.List;

import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.AddFeatureExtension;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.add.AddFeatureFilter;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.tasks.constant.AddFeatureImpl.AddFeature.Filter;
import org.ruminaq.tasks.constant.impl.Port;
import org.ruminaq.tasks.constant.model.constant.Constant;
import org.ruminaq.tasks.features.AddTaskFeature;

@Component(property = { "service.ranking:Integer=10" })
public class AddFeatureImpl implements AddFeatureExtension {

	@Override
	public List<Class<? extends IAddFeature>> getFeatures() {
		return Arrays.asList(AddFeature.class);
	}

	@FeatureFilter(Filter.class)
	public static class AddFeature extends AddTaskFeature {

		public class Filter extends AddFeatureFilter {
			@Override
			public Class<? extends BaseElement> forBusinessObject() {
				return Constant.class;
			}
		}

		public AddFeature(IFeatureProvider fp) {
			super(fp);
		}

		@Override
		protected int getHeight() {
			return 50;
		}

		@Override
		protected int getWidth() {
			return 50;
		}

		@Override
		protected Class<? extends PortsDescr> getPortsDescription() {
			return Port.class;
		}
	}
}
