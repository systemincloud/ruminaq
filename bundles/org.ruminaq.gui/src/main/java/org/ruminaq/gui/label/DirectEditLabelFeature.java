/*
 * (C) Copyright 2018 Marek Jagielski.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ruminaq.gui.label;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.impl.AbstractDirectEditingFeature;
import org.eclipse.graphiti.mm.algorithms.MultiText;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.ruminaq.consts.Constants;
import org.ruminaq.model.ruminaq.BaseElement;

public class DirectEditLabelFeature extends AbstractDirectEditingFeature {

	public DirectEditLabelFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public int getEditingType() {
		return TYPE_TEXT;
	}

	@Override
	public boolean stretchFieldToFitText() {
		return true;
	}

	@Override
	public boolean canDirectEdit(IDirectEditingContext context) {
		return true;
	}

	@Override
	public String getInitialValue(IDirectEditingContext context) {
		BaseElement be = (BaseElement) getBusinessObjectForPictogramElement(context.getPictogramElement());
		return be.getId();
	}

	@Override
	public String checkValueValid(String value, IDirectEditingContext context) {
		if (value.length() < 1)              								return "Please enter any text as element id.";
		else if (value.contains("\n"))       								return "Line breakes are not allowed in class names.";
		else if (hasId(getDiagram(), context.getPictogramElement(), value)) return "Model has already id " + value + ".";
		else return null;
	}

	public static boolean hasId(Diagram diagram, PictogramElement pe, String value) {
		for (Shape s : diagram.getChildren()) {
			if(s == pe) continue;
			if (Graphiti.getPeService().getPropertyValue(s, Constants.LABEL_PROPERTY) != null) {
				if(value.equals(((MultiText) s.getGraphicsAlgorithm().getGraphicsAlgorithmChildren().get(0)).getValue())) return true;
			}
		}

		return false;
	}

	@Override
	public void setValue(String value, IDirectEditingContext context) {
		BaseElement be = (BaseElement) getBusinessObjectForPictogramElement(context.getPictogramElement());
		be.setId(value);
		updatePictogramElement(context.getPictogramElement());
	}
}
