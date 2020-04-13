/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.contextbuttonpad;

import java.util.Optional;
import java.util.function.Predicate;

import org.eclipse.graphiti.datatypes.IRectangle;
import org.eclipse.graphiti.internal.datatypes.impl.RectangleImpl;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.ContextButtonPadLocationExtension;
import org.ruminaq.gui.api.GenericContextButtonPadDataExtension;
import org.ruminaq.gui.diagram.RuminaqBehaviorProvider;
import org.ruminaq.gui.features.contextbuttonpad.ContextButtonPadInternalPortTool.Filter;
import org.ruminaq.gui.model.diagram.InternalInputPortShape;
import org.ruminaq.util.ServiceFilter;
import org.ruminaq.util.ServiceFilterArgs;

/**
 * InputPort context buttons.
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
      return Optional.of(args).map(ServiceFilterArgs::getArgs)
          .map(l -> l.get(1)).filter(InternalInputPortShape.class::isInstance)
          .isPresent();
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
