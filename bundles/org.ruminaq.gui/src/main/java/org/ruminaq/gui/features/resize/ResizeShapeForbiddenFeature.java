/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.resize;

import java.util.Optional;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.impl.DefaultResizeShapeFeature;
import org.eclipse.graphiti.services.Graphiti;
import org.ruminaq.consts.Constants;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.FeaturePredicate;
import org.ruminaq.gui.features.resize.ResizeShapeForbiddenFeature.Filter;
import org.ruminaq.gui.model.diagram.LabelShape;
import org.ruminaq.gui.model.diagram.PortShape;
import org.ruminaq.model.ruminaq.InternalPort;

@FeatureFilter(Filter.class)
public class ResizeShapeForbiddenFeature extends DefaultResizeShapeFeature {

  public static class Filter implements FeaturePredicate<IContext> {
    @Override
    public boolean test(IContext context, IFeatureProvider fp) {
      IResizeShapeContext resizeShapeContext = (IResizeShapeContext) context;

      String connectionPointProperty = Graphiti.getPeService().getPropertyValue(
          resizeShapeContext.getShape(), Constants.SIMPLE_CONNECTION_POINT);
      if (Boolean.parseBoolean(connectionPointProperty)) {
        return true;
      }

      return Optional.of(resizeShapeContext.getShape())
          .filter(FeaturePredicate.predicate(InternalPort.class::isInstance)
              .or(PortShape.class::isInstance).or(LabelShape.class::isInstance))
          .isPresent();
    }
  }

  public ResizeShapeForbiddenFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canResizeShape(IResizeShapeContext context) {
    return false;
  }
}
