package org.ruminaq.gui.features.create;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.ruminaq.gui.features.CreateElementFeature;
import org.ruminaq.gui.features.Images;
import org.ruminaq.model.ModelHandler;
import org.ruminaq.model.model.ruminaq.MainTask;
import org.ruminaq.model.model.ruminaq.OutputPort;
import org.ruminaq.model.model.ruminaq.RuminaqFactory;

public class CreateOutputPortFeature extends CreateElementFeature {

	public CreateOutputPortFeature(IFeatureProvider fp) { super(fp, OutputPort.class); }

	@Override
	public Object[] create(ICreateContext context) {
		OutputPort outputPort = RuminaqFactory.eINSTANCE.createOutputPort();
		setDefaultId(outputPort, context);

		MainTask mt = ModelHandler.getModel(getDiagram(), getFeatureProvider());
		mt.getOutputPort().add(outputPort);

		addGraphicalRepresentation(context, outputPort);
		return new Object[] { outputPort };
	}

	@Override public String getCreateImageId() { return Images.K.IMG_PALETTE_OUTPUTPORT.name(); }
}
