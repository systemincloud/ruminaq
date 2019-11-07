/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.directediting;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.impl.AbstractDirectEditingFeature;
import org.eclipse.graphiti.mm.algorithms.MultiText;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.ruminaq.gui.LabelUtil;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.FeaturePredicate;
import org.ruminaq.gui.features.directediting.DirectEditLabelFeature.Filter;
import org.ruminaq.model.ruminaq.BaseElement;

@FeatureFilter(Filter.class)
public class DirectEditLabelFeature extends AbstractDirectEditingFeature {

  public static class Filter implements FeaturePredicate<IContext> {
    @Override
    public boolean test(IContext context) {
      IDirectEditingContext directEditingContext = (IDirectEditingContext) context;
      String labelProperty = Graphiti.getPeService().getPropertyValue(
          directEditingContext.getPictogramElement(), LabelUtil.LABEL_PROPERTY);
      return Boolean.parseBoolean(labelProperty);
    }
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
    BaseElement be = (BaseElement) getBusinessObjectForPictogramElement(
        context.getPictogramElement());
    return be.getId();
  }

  @Override
  public String checkValueValid(String value, IDirectEditingContext context) {
    if (value.length() < 1)
      return "Please enter any text as element id.";
    else if (value.contains("\n"))
      return "Line breakes are not allowed in class names.";
    else if (hasId(getDiagram(), context.getPictogramElement(), value))
      return "Model has already id " + value + ".";
    else
      return null;
  }

  public static boolean hasId(Diagram diagram, PictogramElement pe,
      String value) {
    for (Shape s : diagram.getChildren()) {
      if (s == pe)
        continue;
      if (Graphiti.getPeService().getPropertyValue(s,
          LabelUtil.LABEL_PROPERTY) != null) {
        if (value.equals(((MultiText) s.getGraphicsAlgorithm()
            .getGraphicsAlgorithmChildren().get(0)).getValue()))
          return true;
      }
    }

    return false;
  }

  @Override
  public void setValue(String value, IDirectEditingContext context) {
    BaseElement be = (BaseElement) getBusinessObjectForPictogramElement(
        context.getPictogramElement());
    be.setId(value);
    updatePictogramElement(context.getPictogramElement());
  }
}
