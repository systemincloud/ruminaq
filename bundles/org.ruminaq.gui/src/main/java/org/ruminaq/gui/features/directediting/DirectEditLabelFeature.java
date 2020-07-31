/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.directediting;

import java.util.Optional;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.impl.AbstractDirectEditingFeature;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.directediting.DirectEditLabelFeature.Filter;
import org.ruminaq.gui.features.resize.AbstractResizeFeatureFilter;
import org.ruminaq.gui.model.diagram.LabelShape;
import org.ruminaq.gui.model.diagram.LabeledRuminaqShape;
import org.ruminaq.gui.model.diagram.RuminaqShape;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.model.ruminaq.NoElement;

@FeatureFilter(Filter.class)
public class DirectEditLabelFeature extends AbstractDirectEditingFeature {

  public static class Filter extends AbstractDirectEditingFeatureFilter {
    @Override
    public Class<? extends RuminaqShape> forShape() {
      return LabelShape.class;
    }

    @Override
    public Class<? extends BaseElement> forBusinessObject() {
      return NoElement.class;
    }
  }

  protected static LabelShape shapeFromContext(IDirectEditingContext context) {
    return Optional.of(context)
        .map(AbstractDirectEditingFeatureFilter.getPictogramElement)
        .filter(LabelShape.class::isInstance).map(LabelShape.class::cast)
        .orElseThrow();
  }

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
    return shapeFromContext(context).getLabeledShape().getModelObject().getId();
  }

  @Override
  public String checkValueValid(String value, IDirectEditingContext context) {
    if (value.length() < 1) {
      return "Please enter any text as element id.";
    } else if (value.contains("\n")) {
      return "Line breakes are not allowed in class names.";
    } else if (hasId(getDiagram(), context.getPictogramElement(), value)) {
      return "Model has already id " + value + ".";
    } else {
      return null;
    }
  }

  public static boolean hasId(Diagram diagram, PictogramElement pe,
      String value) {
    return diagram.getChildren().stream().filter(s -> pe != s)
        .filter(LabelShape.class::isInstance).map(LabelShape.class::cast)
        .map(LabelShape::getLabeledShape)
        .map(LabeledRuminaqShape::getModelObject).map(BaseElement::getId)
        .anyMatch(value::equals);
  }

  @Override
  public void setValue(String value, IDirectEditingContext context) {
    shapeFromContext(context).getLabeledShape().getModelObject().setId(value);
    updatePictogramElement(context.getPictogramElement());
  }
}
