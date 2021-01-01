/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.contextbuttonpad;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.eclipse.graphiti.datatypes.IRectangle;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.internal.datatypes.impl.RectangleImpl;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.ContextButtonPadLocationExtension;
import org.ruminaq.gui.features.contextbuttonpad.ContextButtonPadPortTool.Filter;
import org.ruminaq.gui.model.diagram.PortShape;
import org.ruminaq.util.ServiceFilter;
import org.ruminaq.util.ServiceFilterArgs;

/**
 * Where to place context pad on Port.
 *
 * @author Marek Jagielski
 */
@Component(property = { "service.ranking:Integer=5" })
@ServiceFilter(Filter.class)
public class ContextButtonPadPortTool
    implements ContextButtonPadLocationExtension {

  protected static class Filter implements Predicate<ServiceFilterArgs> {

    @Override
    public boolean test(ServiceFilterArgs args) {
      return Optional.ofNullable(args).map(ServiceFilterArgs::getArgs)
          .map(List::stream).orElseGet(Stream::empty)
          .filter(IPictogramElementContext.class::isInstance)
          .map(IPictogramElementContext.class::cast)
          .map(IPictogramElementContext::getPictogramElement)
          .anyMatch(PortShape.class::isInstance);
    }
  }

  private static final int PAD_LOCATION = 30;

  @Override
  public IRectangle getPadLocation(IPictogramElementContext context,
      IRectangle rectangle) {
    RectangleImpl ret = new RectangleImpl(rectangle);
    ret.setHeight(PAD_LOCATION);
    return ret;
  }
}
