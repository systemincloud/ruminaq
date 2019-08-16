/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.paste;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IPasteContext;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.ruminaq.gui.features.FeaturePredicate;
import org.ruminaq.gui.features.PasteFeatureFilter;
import org.ruminaq.gui.features.add.AddElementFeature;
import org.ruminaq.gui.features.paste.PasteOutputPortFeature.Filter;
import org.ruminaq.model.ModelHandler;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.model.ruminaq.MainTask;
import org.ruminaq.model.ruminaq.OutputPort;

@PasteFeatureFilter(Filter.class)
public class PasteOutputPortFeature extends RuminaqPasteFeature
    implements PasteAnchorTracker {

	public static class Filter implements FeaturePredicate<BaseElement> {
		@Override
		public boolean test(BaseElement bo) {
			return bo instanceof OutputPort;
		}
	}

	private PictogramElement oldPe;
	private int xMin;
	private int yMin;

	private Map<Anchor, Anchor> anchors = new HashMap<>();

	@Override
	public List<PictogramElement> getNewPictogramElements() {
		return newPes;
	}

	public PasteOutputPortFeature(IFeatureProvider fp, PictogramElement oldPe,
	    int xMin, int yMin) {
		super(fp);
		this.oldPe = oldPe;
		this.xMin = xMin;
		this.yMin = yMin;
	}

	@Override
	public boolean canPaste(IPasteContext context) {
		PictogramElement[] pes = context.getPictogramElements();
		if (pes.length != 1 || !(pes[0] instanceof Diagram))
			return false;
		return true;
	}

	@Override
	public void paste(IPasteContext context) {
		PictogramElement[] pes = context.getPictogramElements();
		int x = context.getX();
		int y = context.getY();

		Diagram diagram = (Diagram) pes[0];

		OutputPort oldBo = null;
		ContainerShape oldLabel = null;

		for (Object o : getAllBusinessObjectsForPictogramElement(oldPe)) {
			if (o instanceof OutputPort)
				oldBo = (OutputPort) o;
			if (AddElementFeature.isLabel(o))
				oldLabel = (ContainerShape) o;
		}

		PictogramElement newPe = EcoreUtil.copy(oldPe);
		newPes.add(newPe);
		OutputPort newBo = EcoreUtil.copy(oldBo);

		MainTask mt = ModelHandler.getModel(getDiagram(), getFeatureProvider());
		mt.getOutputPort().add(newBo);

		newPe.getGraphicsAlgorithm()
		    .setX(x + newPe.getGraphicsAlgorithm().getX() - xMin);
		newPe.getGraphicsAlgorithm()
		    .setY(y + newPe.getGraphicsAlgorithm().getY() - yMin);

		String newId = PasteDefaultElementFeature.setId(newBo.getId(), newBo,
		    diagram);

		diagram.getChildren().add((Shape) newPe);

		ContainerShape newLabel = PasteDefaultElementFeature.addLabel(oldPe,
		    oldLabel, x, y, newId, diagram, newPe);
		newPes.add(newLabel);

		link(newPe, new Object[] { newBo, newLabel });
		link(newLabel, new Object[] { newBo, newPe });

		updatePictogramElement(newLabel);
		layoutPictogramElement(newLabel);

		Iterator<Anchor> itOld = ((Shape) oldPe).getAnchors().iterator();
		Iterator<Anchor> itNew = ((Shape) newPe).getAnchors().iterator();
		while (itOld.hasNext() && itNew.hasNext())
			anchors.put(itOld.next(), itNew.next());
	}

	@Override
	public Map<Anchor, Anchor> getAnchors() {
		return anchors;
	}
}
