package org.ruminaq.tasks.mux.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.ruminaq.tasks.features.UpdateTaskFeature;
import org.ruminaq.tasks.mux.impl.Port;
import org.ruminaq.tasks.mux.model.mux.Mux;

public class UpdateFeature extends UpdateTaskFeature {

	private boolean updateNeededChecked = false;

	private boolean superUpdateNeeded   = false;
	private boolean inputsUpdateNeeded = false;

	public UpdateFeature(IFeatureProvider fp) { super(fp); }

	@Override
	public boolean canUpdate(IUpdateContext context) {
		Object bo = getBusinessObjectForPictogramElement(context.getPictogramElement());
		return (bo instanceof Mux);
	}

	@Override
	public IReason updateNeeded(IUpdateContext context) {
		this.updateNeededChecked = true;
		superUpdateNeeded = super.updateNeeded(context).toBoolean();

		ContainerShape parent = (ContainerShape) context.getPictogramElement();
		Mux mx = (Mux) getBusinessObjectForPictogramElement(parent);

		this.inputsUpdateNeeded = mx.getSize() != mx.getInputPort().size() - 1;

		boolean updateNeeded = superUpdateNeeded
				            || inputsUpdateNeeded;
		return updateNeeded ?  Reason.createTrueReason() : Reason.createFalseReason();
	}

	@Override
	public boolean update(IUpdateContext context) {
		if(!updateNeededChecked)
			if(!this.updateNeeded(context).toBoolean()) return false;

		boolean updated = false;
		if(superUpdateNeeded) updated = updated | super.update(context);

		ContainerShape parent = (ContainerShape) context.getPictogramElement();
		Mux mx = (Mux) getBusinessObjectForPictogramElement(parent);

		if(inputsUpdateNeeded) updated = updated | inputsUpdate(parent, mx);

		return updated;
	}

	private boolean inputsUpdate(ContainerShape parent, Mux mx) {
		int n = mx.getSize() - mx.getInputPort().size() + 1;
		if(n > 0)
			for(int i = 0; i < n; i++) addPort(mx, parent, Port.IN);
		else if(n < 0)
			for(int i = 0; i < -n; i++) removePort(mx, parent, Port.IN);
		return true;
	}
}
