package org.ruminaq.tasks.console.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.ruminaq.tasks.console.impl.Port;
import org.ruminaq.tasks.console.model.console.Console;
import org.ruminaq.tasks.console.model.console.ConsoleType;
import org.ruminaq.tasks.features.UpdateTaskFeature;

public class UpdateFeature extends UpdateTaskFeature {

	private boolean updateNeededChecked = false;

	private boolean superUpdateNeeded  = false;
	private boolean inputUpdateNeeded  = false;
	private boolean outputUpdateNeeded = false;

	public UpdateFeature(IFeatureProvider fp) { super(fp); }

	@Override
	public boolean canUpdate(IUpdateContext context) {
		Object bo = getBusinessObjectForPictogramElement(context.getPictogramElement());
		return (bo instanceof Console);
	}

	@Override
	public IReason updateNeeded(IUpdateContext context) {
		this.updateNeededChecked = true;
		superUpdateNeeded = super.updateNeeded(context).toBoolean();

		ContainerShape parent = (ContainerShape) context.getPictogramElement();
		Console console = (Console) getBusinessObjectForPictogramElement(parent);
		switch(console.getConsoleType()) {
			case IN:
				if(console.getInputPort() .size() != 1) inputUpdateNeeded  = true;
				if(console.getOutputPort().size() != 0) outputUpdateNeeded = true;
				break;
			case OUT:
				if(console.getInputPort() .size() != 0) inputUpdateNeeded  = true;
				if(console.getOutputPort().size() != 1) outputUpdateNeeded = true;
				break;
			case INOUT:
				if(console.getInputPort() .size() != 1) inputUpdateNeeded  = true;
				if(console.getOutputPort().size() != 1) outputUpdateNeeded = true;
				break;
			default: break;
		}


		boolean updateNeeded = superUpdateNeeded
				            || inputUpdateNeeded
				            || outputUpdateNeeded;
		return updateNeeded ? Reason.createTrueReason() : Reason.createFalseReason();
	}

	@Override
	public boolean update(IUpdateContext context) {
		if(!updateNeededChecked)
			if(!this.updateNeeded(context).toBoolean()) return false;

		Console console = (Console) getBusinessObjectForPictogramElement(context.getPictogramElement());

		boolean updated = false;
		if(superUpdateNeeded)  updated = updated | super.update(context);
		if(inputUpdateNeeded)  updated = updated | updateInput (console, (ContainerShape) context.getPictogramElement());
		if(outputUpdateNeeded) updated = updated | updateOutput(console, (ContainerShape) context.getPictogramElement());
		return updated;
	}

	private boolean updateInput(Console console, ContainerShape parent) {
		if((console.getConsoleType().equals(ConsoleType.IN)
		 || console.getConsoleType().equals(ConsoleType.INOUT))   && console.getInputPort().size() < 1)   {  addPort   (console, parent, Port.IN); }
		else if((console.getConsoleType().equals(ConsoleType.OUT) && console.getInputPort().size() == 1)) {  removePort(console, parent, Port.IN); }
		return true;
	}

	private boolean updateOutput(Console console, ContainerShape parent) {
		if((console.getConsoleType().equals(ConsoleType.OUT)
		 || console.getConsoleType().equals(ConsoleType.INOUT))  && console.getOutputPort().size() < 1)   { addPort   (console, parent, Port.OUT); }
		else if((console.getConsoleType().equals(ConsoleType.IN) && console.getOutputPort().size() == 1)) { removePort(console, parent, Port.OUT); }
		return true;
	}

}
