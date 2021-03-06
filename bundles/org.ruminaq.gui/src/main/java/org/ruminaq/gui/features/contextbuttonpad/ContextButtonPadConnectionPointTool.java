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
import org.ruminaq.gui.model.diagram.SimpleConnectionPointShape;
import org.ruminaq.util.ServiceFilter;
import org.ruminaq.util.ServiceFilterArgs;

/**
 * Where to place context pad on SimpleConnectionPointShape.
 *
 * @author Marek Jagielski
 */
@Component(property = { "service.ranking:Integer=5" })
@ServiceFilter(ContextButtonPadConnectionPointTool.Filter.class)
public class ContextButtonPadConnectionPointTool
    implements ContextButtonPadLocationExtension {

  private static final int HEIGHT = 80;

  private static class Filter implements Predicate<ServiceFilterArgs> {

    @Override
    public boolean test(ServiceFilterArgs args) {
      return Optional.ofNullable(args).map(ServiceFilterArgs::getArgs)
          .map(l -> l.get(1)).filter(IPictogramElementContext.class::isInstance)
          .map(IPictogramElementContext.class::cast)
          .map(IPictogramElementContext::getPictogramElement)
          .filter(SimpleConnectionPointShape.class::isInstance).isPresent();
    }
  }

  @Override
  public IRectangle getPadLocation(IPictogramElementContext context,
      IRectangle rectangle) {
    RectangleImpl newRectangle = new RectangleImpl(rectangle);
    newRectangle.setHeight(HEIGHT);
    return newRectangle;
  }
}
