/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.move;

import java.util.Optional;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.context.impl.MoveShapeContext;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.features.impl.DefaultMoveShapeFeature;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.model.diagram.LabelShape;
import org.ruminaq.gui.model.diagram.LabeledRuminaqShape;
import org.ruminaq.model.ruminaq.BaseElement;

/**
 * IMoveShapeFeature for ruminaq element.
 *
 * @author Marek Jagielski
 */
@FeatureFilter(MoveElementFeature.Filter.class)
public class MoveElementFeature extends DefaultMoveShapeFeature {

  private static class Filter extends AbstractMoveFeatureFilter {
    @Override
    public Class<? extends BaseElement> forBusinessObject() {
      return BaseElement.class;
    }
  }

  public MoveElementFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canMoveShape(IMoveShapeContext context) {
    if (context.getSourceContainer().equals(context.getTargetContainer())) {
      return true;
    }
    return Optional.ofNullable(context.getTargetContainer())
        .filter(LabelShape.class::isInstance).map(LabelShape.class::cast)
        .filter(l -> Optional.ofNullable(context.getShape())
            .filter(LabeledRuminaqShape.class::isInstance)
            .map(LabeledRuminaqShape.class::cast)
            .map(LabeledRuminaqShape::getLabel).filter(l::equals).isPresent())
        .isPresent();
  }

  /**
   * Check if shape is hovering label.
   */
  @Override
  public void preMoveShape(IMoveShapeContext context) {
    Optional.ofNullable(context.getTargetContainer())
        .filter(LabelShape.class::isInstance).map(LabelShape.class::cast)
        .ifPresent((LabelShape l) -> {
          MoveShapeContext c = (MoveShapeContext) context;
          c.setTargetContainer(l.getContainer());
          c.setDeltaX(c.getDeltaX() + l.getX());
          c.setDeltaY(c.getDeltaY() + l.getY());
          c.setX(c.getX() + l.getX());
          c.setY(c.getY() + l.getY());
        });
  }

  /**
   * Move respectively label.
   */
  @Override
  protected void postMoveShape(IMoveShapeContext context) {
    Optional.of(context.getShape())
        .filter(LabeledRuminaqShape.class::isInstance)
        .map(LabeledRuminaqShape.class::cast).map(LabeledRuminaqShape::getLabel)
        .ifPresent((LabelShape l) -> {
          l.setX(l.getX() + context.getDeltaX());
          l.setY(l.getY() + context.getDeltaY());
          getFeatureProvider().updateIfPossibleAndNeeded(new UpdateContext(l));
        });
  }
}
