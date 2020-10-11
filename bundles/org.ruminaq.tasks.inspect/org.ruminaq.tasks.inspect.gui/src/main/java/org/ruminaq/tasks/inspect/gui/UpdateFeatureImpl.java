/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.inspect.gui;

import java.util.Collections;
import java.util.List;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.UpdateFeatureExtension;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.update.AbstractUpdateFeatureFilter;
import org.ruminaq.gui.features.update.UpdateTaskFeature;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.model.ruminaq.PortsDescr;
import org.ruminaq.tasks.inspect.gui.UpdateFeatureImpl.UpdateFeature.Filter;
import org.ruminaq.tasks.inspect.model.Port;
import org.ruminaq.tasks.inspect.model.inspect.Inspect;

/**
 * Service UpdateFeatureExtension implementation.
 *
 * @author Marek Jagielski
 */
@Component(property = { "service.ranking:Integer=10" })
public class UpdateFeatureImpl implements UpdateFeatureExtension {

  @Override
  public List<Class<? extends IUpdateFeature>> getFeatures() {
    return Collections.singletonList(UpdateFeature.class);
  }

  /**
   * Inspect AddFeature.
   */
  @FeatureFilter(Filter.class)
  public static class UpdateFeature extends UpdateTaskFeature {

    public static class Filter extends AbstractUpdateFeatureFilter {
      @Override
      public Class<? extends BaseElement> forBusinessObject() {
        return Inspect.class;
      }
    }

    public UpdateFeature(IFeatureProvider fp) {
      super(fp);
    }
    
    @Override
    protected Class<? extends PortsDescr> getPortsDescription() {
      return Port.class;
    }
  }
}
