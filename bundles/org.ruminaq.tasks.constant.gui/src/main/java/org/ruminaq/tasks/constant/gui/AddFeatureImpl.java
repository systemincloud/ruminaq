/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.constant.gui;

import java.util.Collections;
import java.util.List;

import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.AddFeatureExtension;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.add.AbstractAddFeatureFilter;
import org.ruminaq.gui.features.add.AbstractAddTaskFeature;
import org.ruminaq.gui.model.diagram.TaskShape;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.tasks.constant.gui.AddFeatureImpl.AddFeature.Filter;
import org.ruminaq.tasks.constant.gui.constantshape.ConstantshapeFactory;
import org.ruminaq.tasks.constant.impl.Port;
import org.ruminaq.tasks.constant.model.constant.Constant;

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
   * Constant AddFeature.
   */
  @FeatureFilter(Filter.class)
  public static class AddFeature extends AbstractAddTaskFeature {

    public static class Filter extends AbstractAddFeatureFilter {
      @Override
      public Class<? extends BaseElement> forBusinessObject() {
        return Constant.class;
      }
    }

    private static final int HEIGHT = 50;

    private static final int WIDTH = 50;

    public AddFeature(IFeatureProvider fp) {
      super(fp);
    }
    
    @Override
    public TaskShape createTaskShape() {
      return ConstantshapeFactory.eINSTANCE.createConstantShape();
    }

    @Override
    protected int getHeight() {
      return HEIGHT;
    }

    @Override
    protected int getWidth() {
      return WIDTH;
    }

    @Override
    protected Class<? extends PortsDescr> getPortsDescription() {
      return Port.class;
    }
  }
}
