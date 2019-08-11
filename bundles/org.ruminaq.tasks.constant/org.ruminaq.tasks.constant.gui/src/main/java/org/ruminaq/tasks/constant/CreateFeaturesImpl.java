package org.ruminaq.tasks.constant;

import java.util.Arrays;
import java.util.List;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.services.Graphiti;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.CreateFeaturesExtension;
import org.ruminaq.gui.features.create.PaletteCreateFeature;
import org.ruminaq.gui.palette.CommonPaletteCompartmentEntry;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.tasks.constant.impl.Port;
import org.ruminaq.tasks.constant.impl.strategy.Int32Strategy;
import org.ruminaq.tasks.constant.model.constant.Constant;
import org.ruminaq.tasks.constant.model.constant.ConstantFactory;
import org.ruminaq.tasks.features.CreateTaskFeature;

@Component(property = { "service.ranking:Integer=10" })
public class CreateFeaturesImpl implements CreateFeaturesExtension {

	@Override
	public List<Class<? extends ICreateFeature>> getFeatures() {
		return Arrays.asList(CreateFeature.class);
	}
	
	public static class CreateFeature extends CreateTaskFeature implements PaletteCreateFeature {

		public CreateFeature(IFeatureProvider fp) {
			super(fp, Constant.class);
		}

		@Override
		public String getCompartment() {
			return CommonPaletteCompartmentEntry.DEFAULT_COMPARTMENT;
		}
		
		@Override
		public String getStack() {
			return CommonPaletteCompartmentEntry.SOURCES_STACK;
		}

		@Override
		public Object[] create(ICreateContext context) {
			Object[] os = super.create(context, ConstantFactory.eINSTANCE.createConstant());
			((Constant) os[0]).setDataType(EcoreUtil.copy(((Task) os[0]).getOutputPort().get(0).getDataType().get(0)));
			((Constant) os[0]).setValue(Int32Strategy.DEFAULT_VALUE);
			UpdateContext updateCtx = new UpdateContext(
					Graphiti.getLinkService().getPictogramElements(getDiagram(), (Constant) os[0]).get(0));
			getFeatureProvider().updateIfPossible(updateCtx);
			return os;
		}

		@Override
		protected Class<? extends PortsDescr> getPortsDescription() {
			return Port.class;
		}

		@Override
		public String getCreateImageId() {
			return Images.K.IMG_CONSTANT_PALETTE.name();
		}
	}
}
