/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.javatask.gui;

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
import org.ruminaq.tasks.javatask.model.javatask.JavaTask;

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
   * JavaTask AddFeature.
   */
  @FeatureFilter(AddFeature.Filter.class)
  public static class AddFeature extends AbstractAddTaskFeature {

    private static class Filter extends AbstractAddFeatureFilter {
      @Override
      public Class<? extends BaseElement> forBusinessObject() {
        return JavaTask.class;
      }
    }
    
    public static final String NOT_CHOSEN = "???";

    public AddFeature(IFeatureProvider fp) {
      super(fp);
    }

    @Override
    protected String getInsideIconId() {
      return Images.IMG_JAVATASK_DIAGRAM;
    }

    @Override
    protected String getInsideIconDesc() {
      return NOT_CHOSEN;
    }
  }
}
