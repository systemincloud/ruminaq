package org.ruminaq.tasks.randomgenerator.features;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.services.Graphiti;
import org.osgi.framework.Version;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.tasks.features.CreateTaskFeature;
import org.ruminaq.tasks.randomgenerator.Images;
import org.ruminaq.tasks.randomgenerator.impl.Port;
import org.ruminaq.tasks.randomgenerator.model.randomgenerator.RandomGenerator;
import org.ruminaq.tasks.randomgenerator.model.randomgenerator.RandomgeneratorFactory;

public class CreateFeature extends CreateTaskFeature {

	public CreateFeature(IFeatureProvider fp, String bundleName, Version version) {	super(fp, RandomGenerator.class, bundleName, version); }

	@Override
	public Object[] create(ICreateContext context) {
		Object[] os = super.create(context, RandomgeneratorFactory.eINSTANCE.createRandomGenerator());
		((RandomGenerator) os[0]).setDataType(EcoreUtil.copy(((Task) os[0]).getOutputPort().get(0).getDataType().get(0)));
		((RandomGenerator) os[0]).setDimensions("1");

		UpdateContext updateCtx = new UpdateContext(Graphiti.getLinkService().getPictogramElements(getDiagram(), (RandomGenerator) os[0]).get(0));
		getFeatureProvider().updateIfPossible(updateCtx);
		return os;
	}
	@Override protected Class<? extends PortsDescr> getPortsDescription() { return Port.class; }
	@Override public    String                      getCreateImageId()    { return Images.K.IMG_RANDOMGENERATOR_PALETTE.name(); }
}
