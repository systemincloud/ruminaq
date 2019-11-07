/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.resize;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.impl.DefaultResizeShapeFeature;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.ruminaq.consts.Constants;
import org.ruminaq.gui.LabelUtil;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.FeaturePredicate;
import org.ruminaq.gui.features.resize.ResizeShapeForbiddenFeature.Filter;
import org.ruminaq.model.ruminaq.InternalPort;
import org.ruminaq.model.ruminaq.Port;

@FeatureFilter(Filter.class)
public class ResizeShapeForbiddenFeature extends DefaultResizeShapeFeature {

  public static class Filter implements FeaturePredicate<IContext> {
    @Override
    public boolean test(IContext context, IFeatureProvider fp) {
      IResizeShapeContext resizeShapeContext = (IResizeShapeContext) context;
      String labelProperty = Graphiti.getPeService().getPropertyValue(
          resizeShapeContext.getShape(), LabelUtil.LABEL_PROPERTY);
      if (Boolean.parseBoolean(labelProperty)) {
        return true;
      }

      String connectionPointProperty = Graphiti.getPeService().getPropertyValue(
          resizeShapeContext.getShape(), Constants.SIMPLE_CONNECTION_POINT);
      if (Boolean.parseBoolean(connectionPointProperty)) {
        return true;
      }

      Shape shape = resizeShapeContext.getShape();
      Object bo = fp.getBusinessObjectForPictogramElement(shape);

      if (bo instanceof InternalPort) {
        return true;
      } else if (bo instanceof Port) {
        return true;
      }

      return false;
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
