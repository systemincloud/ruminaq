/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.resize;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.impl.DefaultResizeShapeFeature;

/**
 * Shape should not have possibility to resize.
 *
 * @author Marek Jagielski
 */
public class ResizeShapeForbiddenFeature extends DefaultResizeShapeFeature {

  public ResizeShapeForbiddenFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canResizeShape(IResizeShapeContext context) {
    return false;
  }
}
