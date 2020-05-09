/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.resize;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.resize.ResizeInternalPortFeature.Filter;
import org.ruminaq.gui.model.diagram.InternalPortShape;

/**
 * InternalPort can't be resized.
 * 
 * @author Marek Jagielski
 */
@FeatureFilter(Filter.class)
public class ResizeInternalPortFeature
    extends ResizeShapeForbiddenFeature {

  public static class Filter extends ResizeFilter<InternalPortShape> {
    public Filter() {
      super(InternalPortShape.class);
    }
  }

  public ResizeInternalPortFeature(IFeatureProvider fp) {
    super(fp);
  }
}