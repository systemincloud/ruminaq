/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.update;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.AbstractUpdateFeature;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.MultiText;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.util.GraphicsUtil;

public class UpdateLabelFeature extends AbstractUpdateFeature {

	public UpdateLabelFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canUpdate(IUpdateContext context) {
		return true;
	}

	@Override
	public IReason updateNeeded(IUpdateContext context) {
		String pictogramName = null;
		PictogramElement pictogramElement = context.getPictogramElement();
		if (pictogramElement instanceof ContainerShape) {
			ContainerShape cs = (ContainerShape) pictogramElement;
			for (GraphicsAlgorithm ga : cs.getGraphicsAlgorithm().getGraphicsAlgorithmChildren()) {
				if (ga instanceof MultiText) {
					pictogramName = ((MultiText) ga).getValue();
				}
			}
		}

		// retrieve name from business model
		String businessName = null;
		Object bo = getBusinessObjectForPictogramElement(pictogramElement);
		if (bo instanceof BaseElement) {
			BaseElement be = (BaseElement) bo;
			businessName = be.getId();
		}

		// update needed, if names are different
		boolean updateNameNeeded = ((pictogramName == null && businessName != null) || (pictogramName != null && !pictogramName.equals(businessName)));
		if (updateNameNeeded) return Reason.createTrueReason("Name is out of date");
		else return Reason.createFalseReason();
	}

	@Override
	public boolean update(IUpdateContext context) {
        // retrieve name from business model
        String businessName = null;
        PictogramElement pictogramElement = context.getPictogramElement();
        Object bo = getBusinessObjectForPictogramElement(pictogramElement);
        if (bo instanceof BaseElement) {
        	BaseElement be = (BaseElement) bo;
            businessName = be.getId();
        }

        // Set name in pictogram model
        for (GraphicsAlgorithm ga : pictogramElement.getGraphicsAlgorithm().getGraphicsAlgorithmChildren()) {
            if (ga instanceof MultiText) {
            	MultiText text = (MultiText) ga;
            	int widthBefore = GraphicsUtil.getLabelWidth(text);
            	text.setValue(businessName);
            	int widthAfter = GraphicsUtil.getLabelWidth(text);
                text.setWidth(GraphicsUtil.getLabelWidth(text) + 7);
                pictogramElement.getGraphicsAlgorithm().setWidth(text.getWidth());
                pictogramElement.getGraphicsAlgorithm().setX(pictogramElement.getGraphicsAlgorithm().getX()
                		- ((widthAfter - widthBefore) >> 1) );


                return true;
            }
        }

        return false;
	}


}
