package org.ruminaq.gui.features.move;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.context.impl.MoveShapeContext;
import org.eclipse.graphiti.features.impl.DefaultMoveShapeFeature;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.ruminaq.consts.Constants;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.FeaturePredicate;
import org.ruminaq.gui.features.move.MoveElementFeature.Filter;
import org.ruminaq.model.ruminaq.BaseElement;

@FeatureFilter(Filter.class)
public class MoveElementFeature extends DefaultMoveShapeFeature {

	static class Filter implements FeaturePredicate<IContext> {
		@Override
		public boolean test(IContext context, IFeatureProvider fp) {
			IMoveShapeContext moveShapeContext = (IMoveShapeContext) context;
			Shape shape = moveShapeContext.getShape();

			Object bo = fp
			    .getBusinessObjectForPictogramElement(shape);

			return bo instanceof BaseElement;
		}
	}
	
	public MoveElementFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canMoveShape(IMoveShapeContext context) {
		if(context.getSourceContainer() == null) return false;
		if(context.getSourceContainer().equals(context.getTargetContainer())) return true;

		Shape shape = context.getShape();
		ContainerShape targetShape = null;

		// can move on label place
		for(EObject o : shape.getLink().getBusinessObjects())
			if(o instanceof Shape && Graphiti.getPeService().getPropertyValue((Shape) o, Constants.LABEL_PROPERTY) != null)
				targetShape = (ContainerShape) o;
		if(targetShape != null && targetShape.equals(context.getTargetContainer())) return true;

		return false;
	}

	@Override
	protected void preMoveShape(IMoveShapeContext context) {
		super.preMoveShape(context);
	}

	@Override
	public void moveShape(IMoveShapeContext context) {
		Shape shape = context.getTargetContainer();
		if(Graphiti.getPeService().getPropertyValue(shape, Constants.LABEL_PROPERTY) != null) {
			MoveShapeContext c = (MoveShapeContext) context;
			c.setTargetContainer(shape.getContainer());
			c.setDeltaX(c.getDeltaX() + shape.getGraphicsAlgorithm().getX());
			c.setDeltaY(c.getDeltaY() + shape.getGraphicsAlgorithm().getY());
			c.setX(c.getX() + shape.getGraphicsAlgorithm().getX());
			c.setY(c.getY() + shape.getGraphicsAlgorithm().getY());
		}
		super.moveShape(context);
	}

	@Override
	protected void postMoveShape(final IMoveShapeContext context) {
		Shape shape = context.getShape();

		// move also label
		for (EObject o : shape.getLink().getBusinessObjects()) {
			if (o instanceof Shape && Graphiti.getPeService().getPropertyValue((Shape)o, Constants.LABEL_PROPERTY) != null) {
				ContainerShape textContainerShape = (ContainerShape)o;
				int dx = context.getDeltaX();
				int dy = context.getDeltaY();

				textContainerShape.getGraphicsAlgorithm().setX(textContainerShape.getGraphicsAlgorithm().getX() + dx);
				textContainerShape.getGraphicsAlgorithm().setY(textContainerShape.getGraphicsAlgorithm().getY() + dy);
			}
		}

		super.postMoveShape(context);
	}
}
