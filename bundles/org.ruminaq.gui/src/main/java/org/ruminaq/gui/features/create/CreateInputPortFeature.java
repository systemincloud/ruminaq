package org.ruminaq.gui.features.create;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.ruminaq.gui.Images;
import org.ruminaq.gui.palette.CommonPaletteCompartmentEntry;
import org.ruminaq.model.ModelHandler;
import org.ruminaq.model.ruminaq.InputPort;
import org.ruminaq.model.ruminaq.MainTask;
import org.ruminaq.model.ruminaq.RuminaqFactory;

public class CreateInputPortFeature extends CreateElementFeature implements PaletteCreateFeature {

	public CreateInputPortFeature(IFeatureProvider fp) {
		super(fp, InputPort.class);
	}
	
	@Override
	public String getCompartment() {
		return CommonPaletteCompartmentEntry.DEFAULT_COMPARTMENT;
	}
	
	@Override
	public String getStack() {
		return CommonPaletteCompartmentEntry.PORTS_STACK;
	}

	@Override
	public Object[] create(ICreateContext context) {
		InputPort inputPort = RuminaqFactory.eINSTANCE.createInputPort();
		setDefaultId(inputPort, context);

		MainTask mt = ModelHandler.getModel(getDiagram(), getFeatureProvider());
		mt.getInputPort().add(inputPort);

		addGraphicalRepresentation(context, inputPort);
		return new Object[] { inputPort };
	}

	@Override
	public String getCreateImageId() {
		return Images.Image.IMG_PALETTE_INPUTPORT.name();
	}
}
