/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.move;

import java.util.Optional;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.impl.DefaultMoveShapeFeature;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.move.MoveLabelFeature.Filter;
import org.ruminaq.gui.model.diagram.LabelShape;
import org.ruminaq.gui.model.diagram.RuminaqShape;
import org.ruminaq.gui.model.diagram.impl.label.LabelUtil;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.model.ruminaq.NoElement;

/**
 * IMoveShapeFeature for Label.
 *
 * @author Marek Jagielski
 */
@FeatureFilter(Filter.class)
public class MoveLabelFeature extends DefaultMoveShapeFeature {

  protected static class Filter extends AbstractMoveFeatureFilter {
    @Override
    public Class<? extends RuminaqShape> forShape() {
      return LabelShape.class;
    }

    @Override
    public Class<? extends BaseElement> forBusinessObject() {
      return NoElement.class;
    }
  }

  public MoveLabelFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canMoveShape(IMoveShapeContext context) {
    return context.getSourceContainer().equals(context.getTargetContainer());
  }

  /**
   * Check if Label is still in default position.
   */
  @Override
  protected void postMoveShape(final IMoveShapeContext context) {
    LabelShape shape = Optional.ofNullable(context.getShape())
        .filter(LabelShape.class::isInstance).map(LabelShape.class::cast)
        .orElseThrow();
    shape.setDefaultPosition(LabelUtil.isInDefaultPosition(shape));
  }
}
