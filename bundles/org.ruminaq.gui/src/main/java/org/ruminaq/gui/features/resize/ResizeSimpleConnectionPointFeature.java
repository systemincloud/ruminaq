/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.resize;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.resize.ResizeSimpleConnectionPointFeature.Filter;
import org.ruminaq.gui.model.diagram.RuminaqShape;
import org.ruminaq.gui.model.diagram.SimpleConnectionPointShape;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.model.ruminaq.NoElement;

/**
 * SimpleConnectionPoint can't be resize.
 *
 * @author Marek Jagielski
 */
@FeatureFilter(Filter.class)
public class ResizeSimpleConnectionPointFeature
    extends ResizeShapeForbiddenFeature {

  protected static class Filter extends AbstractResizeFeatureFilter {
    @Override
    public Class<? extends RuminaqShape> forShape() {
      return SimpleConnectionPointShape.class;
    }

    @Override
    public Class<? extends BaseElement> forBusinessObject() {
      return NoElement.class;
    }
  }

  public ResizeSimpleConnectionPointFeature(IFeatureProvider fp) {
    super(fp);
  }
}
