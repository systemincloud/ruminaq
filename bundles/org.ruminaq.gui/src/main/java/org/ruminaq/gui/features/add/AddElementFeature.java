package org.ruminaq.gui.features.add;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.impl.AbstractAddShapeFeature;
import org.eclipse.graphiti.mm.algorithms.MultiText;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeService;
import org.ruminaq.consts.Constants;
import org.ruminaq.util.GraphicsUtil;

public abstract class AddElementFeature extends AbstractAddShapeFeature {

	public AddElementFeature(IFeatureProvider fp) {
		super(fp);
	}

	protected ContainerShape addLabel(ContainerShape targetContainer,
	    String label, int width, int height, int shapeX, int shapeY) {
		return addLabel(targetContainer, getDiagram(), label, width, height, shapeX,
		    shapeY);
	}

	public static ContainerShape addLabel(ContainerShape targetContainer,
	    Diagram diagram, String label, int width, int height, int shapeX,
	    int shapeY) {

		IPeService peService = Graphiti.getPeService();
		IGaService gaService = Graphiti.getGaService();

		ContainerShape textContainerShape = peService
		    .createContainerShape(targetContainer, true);

		Rectangle r = gaService.createInvisibleRectangle(textContainerShape);
		MultiText text = gaService.createDefaultMultiText(diagram, r, label);
		text.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
		text.setVerticalAlignment(Orientation.ALIGNMENT_TOP);

		GraphicsUtil.alignWithShape(text, textContainerShape, width, height, shapeX,
		    shapeY, 0, 0);

		Graphiti.getPeService().setPropertyValue(textContainerShape,
		    Constants.LABEL_PROPERTY, "true");

		return textContainerShape;
	}

	public static boolean isLabel(Object o) {
		return o instanceof ContainerShape && Graphiti.getPeService()
		    .getPropertyValue((ContainerShape) o, Constants.LABEL_PROPERTY) != null;
	}

	public static boolean isLabelInDefaultPosition(ContainerShape label,
	    Shape shape) {
		return GraphicsUtil.isLabelInDefaultPosition(label,
		    shape.getGraphicsAlgorithm().getWidth(),
		    shape.getGraphicsAlgorithm().getHeight(),
		    shape.getGraphicsAlgorithm().getX(),
		    shape.getGraphicsAlgorithm().getY());
	}
}
