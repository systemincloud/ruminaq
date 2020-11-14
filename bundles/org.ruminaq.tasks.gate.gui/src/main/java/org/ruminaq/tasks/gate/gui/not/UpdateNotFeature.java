/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.gate.gui.not;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.update.AbstractUpdateFeatureFilter;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.model.ruminaq.PortsDescr;
import org.ruminaq.tasks.gate.gui.UpdateFeatureImpl.UpdateFeature;
import org.ruminaq.tasks.gate.gui.not.UpdateNotFeature.Filter;
import org.ruminaq.tasks.gate.model.gate.Not;
import org.ruminaq.tasks.gate.not.impl.Port;

/**
 * IUpdateFeature for Not.
 *
 * @author Marek Jagielski
 */
@FeatureFilter(Filter.class)
public class UpdateNotFeature extends UpdateFeature {

  public static class Filter extends AbstractUpdateFeatureFilter {
    @Override
    public Class<? extends BaseElement> forBusinessObject() {
      return Not.class;
    }
  }

  public UpdateNotFeature(IFeatureProvider fp) {
    super(fp);
  }
  
  @Override
  protected Class<? extends PortsDescr> getPortsDescription() {
    return Port.class;
  }

}
