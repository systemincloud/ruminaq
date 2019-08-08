package org.ruminaq.gui.features.reconnection;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.IReconnectionContext;
import org.eclipse.graphiti.features.impl.DefaultReconnectionFeature;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.services.Graphiti;
import org.ruminaq.consts.Constants;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.FeaturePredicate;
import org.ruminaq.gui.features.reconnection.ReconnectionSimpleConnectionFeature.Filter;
import org.ruminaq.model.ruminaq.SimpleConnection;

@FeatureFilter(Filter.class)
public class ReconnectionSimpleConnectionFeature
    extends DefaultReconnectionFeature {

	public static class Filter implements FeaturePredicate<IContext> {
		@Override
		public boolean test(IContext context, IFeatureProvider fp) {
			IReconnectionContext reconnectionContext = (IReconnectionContext) context;
			Connection c = reconnectionContext.getConnection();
			Object bo = fp.getBusinessObjectForPictogramElement(c);
			return bo instanceof SimpleConnection;
		}
	}

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
		if (Graphiti.getPeService().getPropertyValue(
		    context.getOldAnchor().getParent(),
		    Constants.SIMPLE_CONNECTION_POINT) != null)
			return false;
		return true;
	}

}
