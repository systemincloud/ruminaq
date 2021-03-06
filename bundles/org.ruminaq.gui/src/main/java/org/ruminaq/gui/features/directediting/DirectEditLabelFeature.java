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
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.model.diagram.LabelShape;
import org.ruminaq.gui.model.diagram.LabeledRuminaqShape;
import org.ruminaq.gui.model.diagram.RuminaqShape;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.model.ruminaq.NoElement;

/**
 * IDirectEditingFeature for Label.
 *
 * @author Marek Jagielski
 */
@FeatureFilter(DirectEditLabelFeature.Filter.class)
public class DirectEditLabelFeature extends AbstractDirectEditingFeature {

  private static class Filter extends AbstractDirectEditingFeatureFilter {
    @Override
    public Class<? extends RuminaqShape> forShape() {
      return LabelShape.class;
    }

    @Override
    public Class<? extends BaseElement> forBusinessObject() {
      return NoElement.class;
    }
  }

  public DirectEditLabelFeature(IFeatureProvider fp) {
    super(fp);
  }

  protected static LabelShape shapeFromContext(IDirectEditingContext context) {
    return Optional.of(context)
        .map(AbstractDirectEditingFeatureFilter.getPictogramElement)
        .filter(LabelShape.class::isInstance).map(LabelShape.class::cast)
        .orElseThrow();
  }

  /**
   * Check if ContainerShape contains already given label id.
   *
   * @param containerShape shape to check
   * @param pe PictogramElement
   * @param value id value
   * @return contains id
   */
  public static boolean hasId(ContainerShape containerShape, PictogramElement pe,
      String value) {
    return containerShape.getChildren().stream().filter(s -> pe != s)
        .filter(LabelShape.class::isInstance).map(LabelShape.class::cast)
        .map(LabelShape::getLabeledShape)
        .map(LabeledRuminaqShape::getModelObject).map(BaseElement::getId)
        .anyMatch(value::equals);
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
  public String getInitialValue(IDirectEditingContext context) {
    return shapeFromContext(context).getLabeledShape().getModelObject().getId();
  }

  @Override
  public String checkValueValid(String value, IDirectEditingContext context) {
    String message;
    if (value.length() < 1) {
      message = "Please enter any text as element id.";
    } else if (value.contains("\n")) {
      message = "Line breakes are not allowed in class names.";
    } else if (hasId(getDiagram(), context.getPictogramElement(), value)) {
      message = "Model has already id " + value + ".";
    } else {
      message = null;
    }
    return message;
  }

  @Override
  public void setValue(String value, IDirectEditingContext context) {
    shapeFromContext(context).getLabeledShape().getModelObject().setId(value);
    updatePictogramElement(context.getPictogramElement());
  }
}
