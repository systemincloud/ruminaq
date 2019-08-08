package org.ruminaq.gui.features.move;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.impl.DefaultMoveShapeFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.ruminaq.consts.Constants;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.FeaturePredicate;
import org.ruminaq.gui.features.move.MoveLabelFeature.Filter;

@FeatureFilter(Filter.class)
public class MoveLabelFeature extends DefaultMoveShapeFeature {

	public static class Filter implements FeaturePredicate<IContext> {
		@Override
		public boolean test(IContext context, IFeatureProvider fp) {
			IMoveShapeContext moveShapeContext = (IMoveShapeContext) context;
			Shape shape = moveShapeContext.getShape();

			return Boolean.parseBoolean(Graphiti.getPeService()
			    .getPropertyValue(shape, Constants.LABEL_PROPERTY));
		}
	}

	public MoveLabelFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canMoveShape(IMoveShapeContext context) {
		if (context.getSourceContainer() == null)
			return false;
		if (context.getSourceContainer().equals(context.getTargetContainer()))
			return true;
		return false;
	}

	@Override
	protected void preMoveShape(IMoveShapeContext context) {
		super.preMoveShape(context);
	}

	@Override
	public void moveShape(IMoveShapeContext context) {
		Shape sh = context.getShape();
		PictogramElement[] selection = getFeatureProvider().getDiagramTypeProvider()
		    .getDiagramBehavior().getDiagramContainer()
		    .getSelectedPictogramElements();
		for (EObject eo : Graphiti.getLinkService()
		    .getAllBusinessObjectsForLinkedPictogramElement(sh))
			for (PictogramElement s : selection)
				if (eo == s)
					return;
		super.moveShape(context);
	}

	@Override
	protected void postMoveShape(final IMoveShapeContext context) {
		super.postMoveShape(context);
	}
}
