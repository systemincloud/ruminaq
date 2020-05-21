/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.gate.gui.and;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.add.AbstractAddFeatureFilter;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.tasks.gate.gui.AddGateFeature;
import org.ruminaq.tasks.gate.gui.Images;
import org.ruminaq.tasks.gate.gui.and.AddAndFeature.Filter;
import org.ruminaq.tasks.gate.model.gate.And;

@FeatureFilter(Filter.class)
public class AddAndFeature extends AddGateFeature {

  public static class Filter extends AbstractAddFeatureFilter {
    @Override
    public Class<? extends BaseElement> forBusinessObject() {
      return And.class;
    }
  }
  
  public AddAndFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  protected String getInsideIconId() {
    return Images.IMG_AND_DIAGRAM;
  }
}
