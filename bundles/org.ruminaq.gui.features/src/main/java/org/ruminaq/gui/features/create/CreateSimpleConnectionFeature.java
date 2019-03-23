package org.ruminaq.gui.features.create;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.impl.AbstractCreateConnectionFeature;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.services.Graphiti;
import org.ruminaq.consts.Constants;
import org.ruminaq.model.ModelHandler;
import org.ruminaq.model.ruminaq.FlowSource;
import org.ruminaq.model.ruminaq.FlowTarget;
import org.ruminaq.model.ruminaq.MainTask;
import org.ruminaq.model.ruminaq.RuminaqFactory;
import org.ruminaq.model.ruminaq.SimpleConnection;

public class CreateSimpleConnectionFeature extends AbstractCreateConnectionFeature {

	public CreateSimpleConnectionFeature(IFeatureProvider fp) {
		super(fp, "Simple Connection", null);
	}

	@Override
	public boolean canStartConnection(ICreateConnectionContext context) {
		if (getFlowSource(context.getSourceAnchor()) != null) return true;
		return false;
	}

	@Override
	public boolean canCreate(ICreateConnectionContext context) {
        FlowSource source = getFlowSource(context.getSourceAnchor());
        FlowTarget target = getFlowTarget(context.getTargetAnchor());

        if(target != null && context.getTargetAnchor().getIncomingConnections().size() > 0) return false;

        if (source != null && target != null) return true;
        else return false;
	}

	@Override
	public Connection create(ICreateConnectionContext context) {
		Connection newConnection = null;

		FlowSource source = getFlowSource(context.getSourceAnchor());
		FlowTarget target = getFlowTarget(context.getTargetAnchor());

        if (source != null && target != null) {
        	SimpleConnection simpleConnection = createSimpleConnection(source, target);
            AddConnectionContext addContext = new AddConnectionContext(context.getSourceAnchor(), context.getTargetAnchor());
            addContext.setNewObject(simpleConnection);
            newConnection = (Connection) getFeatureProvider().addIfPossible(addContext);
        }

        return newConnection;
	}

	private FlowSource getFlowSource(Anchor anchor) {
		if (anchor != null) {
	        String isConnectionPoint = Graphiti.getPeService().getPropertyValue(anchor.getParent(), Constants.SIMPLE_CONNECTION_POINT);
	        if(Boolean.parseBoolean(isConnectionPoint)) {
	        	return getFlowSource(anchor.getIncomingConnections().get(0).getStart());
	        } else {
	        	Object obj = getBusinessObjectForPictogramElement(anchor.getParent());
	        	if (obj instanceof FlowSource) return (FlowSource) obj;
	        }
		}
		return null;
	}

	private FlowTarget getFlowTarget(Anchor anchor) {
		if (anchor != null) {
			Object obj = getBusinessObjectForPictogramElement(anchor.getParent());
			if (obj instanceof FlowTarget) return (FlowTarget) obj;
		}
		return null;
	}

    private SimpleConnection createSimpleConnection(FlowSource source, FlowTarget target) {
    	SimpleConnection simpleConnection = RuminaqFactory.eINSTANCE.createSimpleConnection();
    	simpleConnection.setSourceRef(source);
    	simpleConnection.setTargetRef(target);

		MainTask mt = ModelHandler.getModel(getDiagram(), getFeatureProvider());
		mt.getConnection().add(simpleConnection);

        return simpleConnection;
   }
}
