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
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.GenericContextButtonPadDataExtension;
import org.ruminaq.gui.diagram.RuminaqBehaviorProvider;
import org.ruminaq.gui.features.contextbuttonpad.ContextButtonPadUserDefinedTaskTool.Filter;
import org.ruminaq.gui.model.diagram.RuminaqShape;
import org.ruminaq.model.ruminaq.UserDefinedTask;
import org.ruminaq.util.ServiceFilter;
import org.ruminaq.util.ServiceFilterArgs;

/**
 * User Defined Task can be updated.
 *
 * @author Marek Jagielski
 */
@Component(property = { "service.ranking:Integer=10" })
@ServiceFilter(Filter.class)
public class ContextButtonPadUserDefinedTaskTool
    implements GenericContextButtonPadDataExtension {

  public static class Filter implements Predicate<ServiceFilterArgs> {

    @Override
    public boolean test(ServiceFilterArgs args) {
      return Optional.of(args).map(ServiceFilterArgs::getArgs).map(List::stream)
          .orElseGet(Stream::empty)
          .filter(IPictogramElementContext.class::isInstance)
          .map(IPictogramElementContext.class::cast).findFirst()
          .map(IPictogramElementContext::getPictogramElement)
          .filter(RuminaqShape.class::isInstance).map(RuminaqShape.class::cast)
          .map(RuminaqShape::getModelObject)
          .filter(UserDefinedTask.class::isInstance).isPresent();
    }
  }

  @Override
  public int getGenericContextButtons() {
    return RuminaqBehaviorProvider.CONTEXT_BUTTON_DELETE
        | RuminaqBehaviorProvider.CONTEXT_BUTTON_UPDATE;
  }
}
