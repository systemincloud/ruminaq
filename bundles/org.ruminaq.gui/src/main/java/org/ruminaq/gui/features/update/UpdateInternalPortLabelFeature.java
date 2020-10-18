/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.update;

import java.util.Optional;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.AbstractUpdateFeature;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.update.UpdateInternalPortLabelFeature.Filter;
import org.ruminaq.gui.model.diagram.InternalPortLabelShape;
import org.ruminaq.gui.model.diagram.RuminaqShape;
import org.ruminaq.gui.model.diagram.impl.task.InternalPortLabelShapeGA;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.model.ruminaq.NoElement;

/**
 * IUpdateFeature for InternalPortLabel.
 *
 * @author Marek Jagielski
 */
@FeatureFilter(Filter.class)
public class UpdateInternalPortLabelFeature extends AbstractUpdateFeature {

  public static class Filter extends AbstractUpdateFeatureFilter {
    @Override
    public Class<? extends RuminaqShape> forShape() {
      return InternalPortLabelShape.class;
    }

    @Override
    public Class<? extends BaseElement> forBusinessObject() {
      return NoElement.class;
    }
  }
  
  public UpdateInternalPortLabelFeature(IFeatureProvider fp) {
    super(fp);
  }

  private static Optional<InternalPortLabelShape> shapeFromContext(
      IUpdateContext context) {
    return Optional.of(context)
        .map(AbstractUpdateFeatureFilter.getPictogramElement)
        .filter(InternalPortLabelShape.class::isInstance)
        .map(InternalPortLabelShape.class::cast);
  }
  
  private static boolean postionUpdateNeeded(InternalPortLabelShape labelShape) {
    return Optional.of(labelShape).map(PictogramElement::getGraphicsAlgorithm)
        .filter(InternalPortLabelShapeGA.class::isInstance)
        .map(InternalPortLabelShapeGA.class::cast)
        .filter(InternalPortLabelShapeGA::isInPosition).isEmpty();
  }
  
  private static boolean postionUpdate(InternalPortLabelShape labelShape) {
    Optional.of(labelShape).map(PictogramElement::getGraphicsAlgorithm)
        .filter(InternalPortLabelShapeGA.class::isInstance)
        .map(InternalPortLabelShapeGA.class::cast)
        .ifPresent(InternalPortLabelShapeGA::updatePosition);
    return true;
  }

  @Override
  public boolean canUpdate(IUpdateContext context) {
    return true;
  }

  @Override
  public IReason updateNeeded(IUpdateContext context) {
    InternalPortLabelShape labelShape = shapeFromContext(context).orElseThrow();

    if (postionUpdateNeeded(labelShape)) {
      return Reason.createTrueReason();
    } else {
      return Reason.createFalseReason();
    }
  }

  @Override
  public boolean update(IUpdateContext context) {
    InternalPortLabelShape labelShape = shapeFromContext(context).orElseThrow();

    if (postionUpdateNeeded(labelShape)) {
      postionUpdate(labelShape);
    }

    return true;
  }

}
