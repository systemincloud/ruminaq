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
import org.ruminaq.gui.features.move.MoveElementFeature.Filter;
import org.ruminaq.gui.model.diagram.LabelShape;
import org.ruminaq.gui.model.diagram.LabeledRuminaqShape;
import org.ruminaq.model.ruminaq.BaseElement;

/**
 * IMoveShapeFeature for ruminaq element.
 *
 * @author Marek Jagielski
 */
@FeatureFilter(Filter.class)
public class MoveElementFeature extends DefaultMoveShapeFeature {

  public static class Filter extends AbstractMoveFeatureFilter {
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
    if (Optional.ofNullable(context.getSourceContainer()).isEmpty()) {
      return false;
    }
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

  @Override
  public void moveShape(IMoveShapeContext context) {
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
    super.moveShape(context);
  }

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

    super.postMoveShape(context);
  }
}
