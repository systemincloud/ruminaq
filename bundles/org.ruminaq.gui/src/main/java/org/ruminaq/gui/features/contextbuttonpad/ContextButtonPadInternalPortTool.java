/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.contextbuttonpad;

import java.util.function.Predicate;

import org.eclipse.graphiti.datatypes.IRectangle;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.internal.datatypes.impl.RectangleImpl;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.ContextButtonPadLocationExtension;
import org.ruminaq.gui.api.GenericContextButtonPadDataExtension;
import org.ruminaq.gui.diagram.RuminaqBehaviorProvider;
import org.ruminaq.gui.features.contextbuttonpad.ContextButtonPadInternalPortTool.Filter;
import org.ruminaq.model.ruminaq.InternalPort;
import org.ruminaq.util.ServiceFilter;
import org.ruminaq.util.ServiceFilterArgs;

/**
 * 
 * @author Marek Jagielski
 */
@Component(property = { "service.ranking:Integer=5" })
@ServiceFilter(Filter.class)
public class ContextButtonPadInternalPortTool implements
    GenericContextButtonPadDataExtension, ContextButtonPadLocationExtension {

  public static class Filter implements Predicate<ServiceFilterArgs> {

    @Override
    public boolean test(ServiceFilterArgs args) {
      IFeatureProvider fp = (IFeatureProvider) args.getArgs().get(0);
      IPictogramElementContext context = (IPictogramElementContext) args
          .getArgs().get(1);
      PictogramElement pe = context.getPictogramElement();
      return fp
          .getBusinessObjectForPictogramElement(pe) instanceof InternalPort;
    }
  }

  private static final int PAD_LOCATION = 80;

  @Override
  public int getGenericContextButtons() {
    return RuminaqBehaviorProvider.CONTEXT_BUTTON_NONE;
  }

  @Override
  public IRectangle getPadLocation(IRectangle rectangle) {
    RectangleImpl ret = new RectangleImpl(rectangle);
    ret.setHeight(PAD_LOCATION);
    return ret;
  }
}
