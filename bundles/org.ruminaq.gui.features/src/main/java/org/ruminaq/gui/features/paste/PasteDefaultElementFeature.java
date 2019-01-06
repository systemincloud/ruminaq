package org.ruminaq.gui.features.paste;

import java.util.List;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IPasteContext;
import org.eclipse.graphiti.mm.algorithms.MultiText;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.ruminaq.gui.features.CreateElementFeature;
import org.ruminaq.gui.features.add.AddElementFeature;
import org.ruminaq.model.model.ruminaq.BaseElement;
import org.ruminaq.util.GraphicsUtil;

public class PasteDefaultElementFeature extends SicPasteFeature {

	public PasteDefaultElementFeature(IFeatureProvider fp, PictogramElement oldPe, int xMin, int yMin) {
		super(fp);
	}

	@Override public List<PictogramElement> getNewPictogramElements() { return newPes; }

	@Override
	public boolean canPaste(IPasteContext context) {
        PictogramElement[] pes = context.getPictogramElements();
        if(pes.length != 1 || !(pes[0] instanceof Diagram)) return false;
		return false;
	}

	@Override
	public void paste(IPasteContext context) { }

	public static String setId(String baseId, BaseElement element, Diagram diagram) {
		String name = baseId;
		if(CreateElementFeature.isPresent(name, diagram.getChildren())) {
			name = "(Copy) " + baseId;
			if(CreateElementFeature.isPresent(name, diagram.getChildren())) {
				int i = 1;
				while(CreateElementFeature.isPresent(name + " " + i, diagram.getChildren())) i++;
				name = name + " " + i;
			}
		}

		element.setId(name);
		return name;
	}

	public static ContainerShape addLabel(PictogramElement oldPe, ContainerShape oldLabel, int x, int y, String newId, Diagram diagram, PictogramElement newPe) {
	  	boolean labelInDefaultPosition = AddElementFeature.isLabelInDefaultPosition(oldLabel, (Shape) oldPe);
	    ContainerShape newLabel = EcoreUtil.copy(oldLabel);
		newLabel.getGraphicsAlgorithm().setX(newLabel.getGraphicsAlgorithm().getX() + x - oldPe.getGraphicsAlgorithm().getX());
		newLabel.getGraphicsAlgorithm().setY(newLabel.getGraphicsAlgorithm().getY() + y - oldPe.getGraphicsAlgorithm().getY());
		((MultiText) newLabel.getGraphicsAlgorithm().getGraphicsAlgorithmChildren().get(0)).setValue(newId);
		diagram.getChildren().add(newLabel);
		if(labelInDefaultPosition)
		GraphicsUtil.alignWithShape((MultiText) newLabel.getGraphicsAlgorithm().getGraphicsAlgorithmChildren().get(0),
									            newLabel,
									            newPe.getGraphicsAlgorithm().getWidth(),
									            newPe.getGraphicsAlgorithm().getHeight(),
									            newPe.getGraphicsAlgorithm().getX(),
									            newPe.getGraphicsAlgorithm().getY(),
									            0, 0);
		return newLabel;
	}
}
