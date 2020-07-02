/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.resize;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.resize.ResizeLabelFeature.Filter;
import org.ruminaq.gui.model.diagram.LabelShape;

/**
 * Label can't be resize.
 * 
 * @author Marek Jagielski
 */
@FeatureFilter(Filter.class)
public class ResizeLabelFeature extends ResizeShapeForbiddenFeature {

  public static class Filter extends ResizeFilter<LabelShape> {
    public Filter() {
      super(LabelShape.class);
    }
  }

  public ResizeLabelFeature(IFeatureProvider fp) {
    super(fp);
  }
}
