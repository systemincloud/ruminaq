/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.contextbuttonpad;

import java.util.Optional;
import java.util.function.Predicate;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.GenericContextButtonPadDataExtension;
import org.ruminaq.gui.diagram.RuminaqBehaviorProvider;
import org.ruminaq.gui.model.diagram.LabelShape;
import org.ruminaq.util.ServiceFilter;
import org.ruminaq.util.ServiceFilterArgs;

/**
 * Label doesn't have context pad buttons.
 *
 * @author Marek Jagielski
 */
@Component(property = { "service.ranking:Integer=5" })
@ServiceFilter(ContextButtonPadLabelFeature.Filter.class)
public class ContextButtonPadLabelFeature
    implements GenericContextButtonPadDataExtension {

  private static class Filter implements Predicate<ServiceFilterArgs> {

    @Override
    public boolean test(ServiceFilterArgs args) {
      return Optional.ofNullable(args).map(ServiceFilterArgs::getArgs)
          .map(l -> l.get(1)).filter(IPictogramElementContext.class::isInstance)
          .map(IPictogramElementContext.class::cast)
          .map(IPictogramElementContext::getPictogramElement)
          .filter(LabelShape.class::isInstance).isPresent();
    }
  }

  @Override
  public int getGenericContextButtons() {
    return RuminaqBehaviorProvider.CONTEXT_BUTTON_NONE;
  }
}
