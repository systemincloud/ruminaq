/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.gate.gui.or;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.add.AbstractAddFeatureFilter;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.tasks.gate.gui.AddGateFeature;
import org.ruminaq.tasks.gate.gui.Images;
import org.ruminaq.tasks.gate.gui.or.AddOrFeature.Filter;
import org.ruminaq.tasks.gate.model.gate.Or;

@FeatureFilter(Filter.class)
public class AddOrFeature extends AddGateFeature {

  public static class Filter extends AbstractAddFeatureFilter {
    @Override
    public Class<? extends BaseElement> forBusinessObject() {
      return Or.class;
    }
  }
  
  public AddOrFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  protected String getInsideIconId() {
    return Images.IMG_OR_DIAGRAM;
  }
}
