package org.ruminaq.tasks.mux;

import java.util.Arrays;
import java.util.List;

import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.CreateFeaturesExtension;
import org.ruminaq.gui.features.create.PaletteCreateFeature;
import org.ruminaq.gui.palette.CommonPaletteCompartmentEntry;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.tasks.mux.impl.Port;
import org.ruminaq.tasks.features.CreateTaskFeature;
import org.ruminaq.tasks.mux.model.mux.Mux;
import org.ruminaq.tasks.mux.model.mux.MuxFactory;

@Component(property = { "service.ranking:Integer=10" })
public class CreateFeaturesImpl implements CreateFeaturesExtension {

	@Override
	public List<Class<? extends ICreateFeature>> getFeatures() {
		return Arrays.asList(CreateFeature.class);
	}
	
	public static class CreateFeature extends CreateTaskFeature implements PaletteCreateFeature {

		public CreateFeature(IFeatureProvider fp) {
			super(fp, Mux.class);
		}

		@Override
		public String getCompartment() {
			return CommonPaletteCompartmentEntry.DEFAULT_COMPARTMENT;
		}
		
		@Override
		public String getStack() {
			return CommonPaletteCompartmentEntry.FLOW_STACK;
		}

		@Override
		public Object[] create(ICreateContext context) {
			return super.create(context, MuxFactory.eINSTANCE.createMux());
		}

		@Override
		protected Class<? extends PortsDescr> getPortsDescription() {
			return Port.class;
		}

		@Override
		public String getCreateImageId() {
			return Images.K.IMG_MUX_PALETTE.name();
		}
	}
}
