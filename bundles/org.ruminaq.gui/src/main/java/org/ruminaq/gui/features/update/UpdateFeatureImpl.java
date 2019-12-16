/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.update;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.UpdateFeatureExtension;
import org.ruminaq.gui.model.diagram.LabelShape;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.model.ruminaq.InputPort;
import org.ruminaq.model.ruminaq.MainTask;

@Component(property = { "service.ranking:Integer=5" })
public class UpdateFeatureImpl implements UpdateFeatureExtension {

  @Override
  public List<Class<? extends IUpdateFeature>> getFeatures() {
    return Arrays.asList(UpdateMainTaskFeature.class,
        UpdateInputPortFeature.class, UpdateBaseElementFeature.class);
  }

  @Override
  public Predicate<? super Class<? extends IUpdateFeature>> filter(
      IContext context, IFeatureProvider fp) {
    IUpdateContext updateContext = (IUpdateContext) context;
    PictogramElement pe = updateContext.getPictogramElement();
    Object bo = fp.getBusinessObjectForPictogramElement(pe);
    return (Class<?> clazz) -> {
      if (clazz.isAssignableFrom(UpdateLabelFeature.class)) {
        return LabelShape.class.isInstance(pe);
      } else if (clazz.isAssignableFrom(UpdateMainTaskFeature.class)) {
        return bo instanceof MainTask;
      } else if (clazz.isAssignableFrom(UpdateInputPortFeature.class)) {
        return bo instanceof InputPort;
      } else if (clazz.isAssignableFrom(UpdateBaseElementFeature.class)) {
        return bo instanceof BaseElement;
      }

      return true;
    };
  }
}
