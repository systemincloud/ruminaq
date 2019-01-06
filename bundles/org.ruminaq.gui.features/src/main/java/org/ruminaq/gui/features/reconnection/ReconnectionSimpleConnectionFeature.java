package org.ruminaq.gui.features.reconnection;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.features.context.IReconnectionContext;
import org.eclipse.graphiti.features.impl.DefaultReconnectionFeature;
import org.eclipse.graphiti.services.Graphiti;
import org.ruminaq.consts.Constants;

public class ReconnectionSimpleConnectionFeature extends DefaultReconnectionFeature {

	public ReconnectionSimpleConnectionFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canReconnect(IReconnectionContext context) {
//        FlowSource source = getFlowSource(context.getSourceAnchor());
//        FlowTarget target = getFlowTarget(context.getTargetAnchor());
//
//        if(target != null && context.getTargetAnchor().getIncomingConnections().size() > 0) return false;
//
//        if (source != null && target != null) return true;
//        else return false;
		return false;
	}

	@Override
	public boolean canStartReconnect(IReconnectionContext context) {
		if(Graphiti.getPeService().getPropertyValue(context.getOldAnchor().getParent(), Constants.SIMPLE_CONNECTION_POINT) != null) return false;
		return true;
	}

}
