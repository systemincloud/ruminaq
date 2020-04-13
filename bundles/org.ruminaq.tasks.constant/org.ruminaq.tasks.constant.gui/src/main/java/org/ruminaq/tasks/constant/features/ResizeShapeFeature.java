/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.constant.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.ruminaq.tasks.features.ResizeShapeTaskFeature;

public class ResizeShapeFeature extends ResizeShapeTaskFeature {

  public ResizeShapeFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canResizeShape(IResizeShapeContext context) {
    return super.canResizeShape(context);
  }

  @Override
  public void resizeShape(IResizeShapeContext context) {
    super.resizeShape(context);
  }
}
