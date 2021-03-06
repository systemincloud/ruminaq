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
import org.ruminaq.gui.api.GenericContextButtonPadDataExtension;
import org.ruminaq.gui.diagram.RuminaqBehaviorProvider;
import org.ruminaq.gui.model.diagram.InternalPortShape;
import org.ruminaq.util.ServiceFilter;
import org.ruminaq.util.ServiceFilterArgs;

/**
 * InputPort context buttons.
 *
 * @author Marek Jagielski
 */
@Component(property = { "service.ranking:Integer=5" })
@ServiceFilter(ContextButtonPadInternalPortTool.Filter.class)
public class ContextButtonPadInternalPortTool implements
    GenericContextButtonPadDataExtension, ContextButtonPadLocationExtension {

  private static class Filter implements Predicate<ServiceFilterArgs> {

    @Override
    public boolean test(ServiceFilterArgs args) {
      return Optional.ofNullable(args).map(ServiceFilterArgs::getArgs)
          .map(List::stream).orElseGet(Stream::empty)
          .filter(IPictogramElementContext.class::isInstance)
          .map(IPictogramElementContext.class::cast)
          .map(IPictogramElementContext::getPictogramElement)
          .anyMatch(InternalPortShape.class::isInstance);
    }
  }

  private static final int PAD_LOCATION = 30;

  protected static InternalPortShape shapeFromContext(
      IPictogramElementContext context) {
    return Optional.of(context)
        .map(IPictogramElementContext::getPictogramElement)
        .filter(InternalPortShape.class::isInstance)
        .map(InternalPortShape.class::cast).orElseThrow();
  }

  @Override
  public int getGenericContextButtons() {
    return RuminaqBehaviorProvider.CONTEXT_BUTTON_NONE;
  }

  /**
   * Translate to position absolute.
   */
  @Override
  public IRectangle getPadLocation(IPictogramElementContext context,
      IRectangle rectangle) {
    InternalPortShape shape = shapeFromContext(context);
    RectangleImpl ret = new RectangleImpl(rectangle);
    ret.setHeight(PAD_LOCATION);
    ret.setX(ret.getX() + shape.getTask().getX());
    ret.setY(ret.getY() + shape.getTask().getY());
    return ret;
  }
}
