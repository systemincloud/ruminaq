/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.contextmenu;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.consts.Constants;
import org.ruminaq.gui.api.ContextMenuEntryExtension;
import org.ruminaq.gui.features.contextmenu.CreateSimpleConnectionPointContextMenu.Filter;
import org.ruminaq.gui.features.create.CreateSimpleConnectionPointFeature;
import org.ruminaq.model.ruminaq.SimpleConnection;
import org.ruminaq.util.GraphicsUtil;
import org.ruminaq.util.ServiceFilter;
import org.ruminaq.util.ServiceFilterArgs;

@Component(property = { "service.ranking:Integer=5" })
@ServiceFilter(Filter.class)
public class CreateSimpleConnectionPointContextMenu
    implements ContextMenuEntryExtension {

  static class Filter implements Predicate<ServiceFilterArgs> {

    @Override
    public boolean test(ServiceFilterArgs args) {
      IFeatureProvider fp = (IFeatureProvider) args.getArgs().get(0);
      ICustomContext context = (ICustomContext) args.getArgs().get(1);
      PictogramElement[] pes = context.getPictogramElements();
      List<Object> bos = Stream.of(pes)
          .map(pe -> fp.getBusinessObjectForPictogramElement(pe))
          .collect(Collectors.toList());
      return bos.size() == 1 && bos.get(0) instanceof SimpleConnection
          && GraphicsUtil.distanceToConnection((FreeFormConnection) pes[0],
              context.getX(), context.getY(), Constants.INTERNAL_PORT) < 5;
    }
  }

  @Override
  public Predicate<ICustomFeature> isAvailable(ICustomContext context) {
    return customFeature -> {
      return customFeature.isAvailable(context) && customFeature.getName()
          .equals(CreateSimpleConnectionPointFeature.NAME);
    };
  }

}
