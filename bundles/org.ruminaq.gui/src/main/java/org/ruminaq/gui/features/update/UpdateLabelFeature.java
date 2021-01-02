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
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.model.diagram.LabelShape;
import org.ruminaq.gui.model.diagram.RuminaqShape;
import org.ruminaq.gui.model.diagram.impl.label.LabelUtil;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.model.ruminaq.NoElement;

/**
 * IUpdateFeature for Label.
 *
 * @author Marek Jagielski
 */
@FeatureFilter(UpdateLabelFeature.Filter.class)
public class UpdateLabelFeature extends AbstractUpdateFeature {

  private static class Filter extends AbstractUpdateFeatureFilter {
    @Override
    public Class<? extends RuminaqShape> forShape() {
      return LabelShape.class;
    }

    @Override
    public Class<? extends BaseElement> forBusinessObject() {
      return NoElement.class;
    }
  }

  public UpdateLabelFeature(IFeatureProvider fp) {
    super(fp);
  }

  protected static Optional<LabelShape> shapeFromContext(
      IUpdateContext context) {
    return Optional.of(context)
        .map(AbstractUpdateFeatureFilter.getPictogramElement)
        .filter(LabelShape.class::isInstance).map(LabelShape.class::cast);
  }

  @Override
  public boolean canUpdate(IUpdateContext context) {
    return true;
  }

  @Override
  public IReason updateNeeded(IUpdateContext context) {
    if (postionUpdateNeeded(shapeFromContext(context).orElseThrow())) {
      return Reason.createTrueReason();
    } else {
      return Reason.createFalseReason();
    }
  }

  /**
   * If Lable suppose to be in default position and is not
   * the position should be updated.
   *
   * @param labelShape LabelShape
   * @return should update
   */
  private static boolean postionUpdateNeeded(LabelShape labelShape) {
    return labelShape.isDefaultPosition()
        && !LabelUtil.isInDefaultPosition(labelShape);
  }

  @Override
  public boolean update(IUpdateContext context) {
    LabelShape labelShape = shapeFromContext(context).orElseThrow();

    if (postionUpdateNeeded(labelShape)) {
      postionUpdate(labelShape);
    }

    return true;
  }

  private static boolean postionUpdate(LabelShape labelShape) {
    LabelUtil.placeInDefaultPosition(labelShape);
    return true;
  }

}
