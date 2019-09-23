/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.contextbuttonpad;

import java.util.function.Predicate;

import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.consts.Constants;
import org.ruminaq.gui.api.GenericContextButtonPadDataExtension;
import org.ruminaq.gui.features.contextbuttonpad.ContextButtonPadDataLabelFeature.Filter;
import org.ruminaq.util.ServiceFilter;
import org.ruminaq.util.ServiceFilterArgs;

@Component(property = { "service.ranking:Integer=5" })
@ServiceFilter(Filter.class)
public class ContextButtonPadDataLabelFeature
    implements GenericContextButtonPadDataExtension {

  static class Filter implements Predicate<ServiceFilterArgs> {

    @Override
    public boolean test(ServiceFilterArgs args) {
      IPictogramElementContext context = (IPictogramElementContext) args
          .getArgs().get(1);
      PictogramElement pe = context.getPictogramElement();
      String labelProperty = Graphiti.getPeService().getPropertyValue(pe,
          Constants.LABEL_PROPERTY);
      String portLabelProperty = Graphiti.getPeService().getPropertyValue(pe,
          Constants.PORT_LABEL_PROPERTY);
      return Boolean.parseBoolean(labelProperty)
          || Boolean.parseBoolean(portLabelProperty);
    }
  }

  @Override
  public int getGenericContextButtons() {
    return 0;
  }
}
