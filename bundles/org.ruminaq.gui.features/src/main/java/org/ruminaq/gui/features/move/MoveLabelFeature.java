package org.ruminaq.gui.features.move;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.impl.DefaultMoveShapeFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;

public class MoveLabelFeature extends DefaultMoveShapeFeature {

	public MoveLabelFeature(IFeatureProvider fp) {
		super(fp);
	}
	
	@Override
	public boolean canMoveShape(IMoveShapeContext context) {
		if(context.getSourceContainer() == null) return false;
		if(context.getSourceContainer().equals(context.getTargetContainer())) return true;
		return false;
	}	
	
	@Override
	protected void preMoveShape(IMoveShapeContext context) {
		super.preMoveShape(context);
	}
	
	@Override
	public void moveShape(IMoveShapeContext context) {
		Shape sh = context.getShape();
		PictogramElement[] selection = getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getDiagramContainer().getSelectedPictogramElements();
		for(EObject eo : Graphiti.getLinkService().getAllBusinessObjectsForLinkedPictogramElement(sh))
			for(PictogramElement s : selection)
				if(eo == s) return;
		super.moveShape(context);
	}
	
	@Override
	protected void postMoveShape(final IMoveShapeContext context) {
		super.postMoveShape(context);
	}
}
