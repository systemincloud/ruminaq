/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.contextbuttonpad;

import java.util.Optional;
import java.util.function.Predicate;

import org.eclipse.graphiti.datatypes.IRectangle;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.internal.datatypes.impl.RectangleImpl;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.ContextButtonPadLocationExtension;
import org.ruminaq.gui.features.contextbuttonpad.ContextButtonPadPortTool.Filter;
import org.ruminaq.gui.model.diagram.PortShape;
import org.ruminaq.util.ServiceFilter;
import org.ruminaq.util.ServiceFilterArgs;

@Component(property = { "service.ranking:Integer=5" })
@ServiceFilter(Filter.class)
public class ContextButtonPadPortTool
    implements ContextButtonPadLocationExtension {
  
  public static class Filter implements Predicate<ServiceFilterArgs> {

    @Override
    public boolean test(ServiceFilterArgs args) {
      IPictogramElementContext context = (IPictogramElementContext) args
          .getArgs().get(1);
      return Optional.ofNullable(context.getPictogramElement())
          .filter(PortShape.class::isInstance).isPresent();
    }
  }
  
  private static final int PAD_LOCATION =30;

  @Override
  public IRectangle getPadLocation(IRectangle rectangle) {
    RectangleImpl ret = new RectangleImpl(rectangle);
    ret.setHeight(PAD_LOCATION);
    return ret;
  }
}
