/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.pythontask.gui;

import java.util.Collections;
import java.util.List;

import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.AddFeatureExtension;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.add.AbstractAddFeatureFilter;
import org.ruminaq.gui.features.add.AbstractAddTaskFeature;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.tasks.pythontask.gui.AddFeatureImpl.AddFeature.Filter;
import org.ruminaq.tasks.pythontask.model.pythontask.PythonTask;

/**
 * Service AddFeatureExtension implementation.
 *
 * @author Marek Jagielski
 */
@Component(property = { "service.ranking:Integer=10" })
public class AddFeatureImpl implements AddFeatureExtension {

  @Override
  public List<Class<? extends IAddFeature>> getFeatures() {
    return Collections.singletonList(AddFeature.class);
  }

  /**
   * PythonTask AddFeature.
   */
  @FeatureFilter(Filter.class)
  public static class AddFeature extends AbstractAddTaskFeature {

    public static class Filter extends AbstractAddFeatureFilter {
      @Override
      public Class<? extends BaseElement> forBusinessObject() {
        
        return PythonTask.class;
      }
    }

    public static String NOT_CHOSEN = "???";

    public AddFeature(IFeatureProvider fp) {
      super(fp);
    }

    @Override
    protected String getInsideIconId() {
      return Images.IMG_PYTHONTASK_DIAGRAM;
    }

    @Override
    protected String getInsideIconDesc() {
      return NOT_CHOSEN;
    }
  }
}
