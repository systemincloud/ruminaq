/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.resize;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.resize.ResizeSimpleConnectionPointFeature.Filter;
import org.ruminaq.gui.model.diagram.SimpleConnectionPointShape;

@FeatureFilter(Filter.class)
public class ResizeSimpleConnectionPointFeature
    extends ResizeShapeForbiddenFeature {

  public static class Filter extends ResizeFilter<SimpleConnectionPointShape> {
    public Filter() {
      super(SimpleConnectionPointShape.class);
    }
  }

  public ResizeSimpleConnectionPointFeature(IFeatureProvider fp) {
    super(fp);
  }
}
