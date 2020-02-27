/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.contextmenu;

import java.util.function.Predicate;
import java.util.stream.Stream;

import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.consts.Constants;
import org.ruminaq.gui.api.ContextMenuEntryExtension;
import org.ruminaq.gui.features.contextmenu.CreateSimpleConnectionPointContextMenu.Filter;
import org.ruminaq.gui.features.create.CreateSimpleConnectionPointFeature;
import org.ruminaq.gui.model.diagram.SimpleConnectionShape;
import org.ruminaq.gui.model.diagram.impl.GuiUtil;
import org.ruminaq.util.ServiceFilter;
import org.ruminaq.util.ServiceFilterArgs;

/**
 * Service ContextMenuEntryExtension implementation.
 *
 * @author Marek Jagielski
 */
@Component(property = { "service.ranking:Integer=5" })
@ServiceFilter(Filter.class)
public class CreateSimpleConnectionPointContextMenu
    implements ContextMenuEntryExtension {

  public static class Filter implements Predicate<ServiceFilterArgs> {

    @Override
    public boolean test(ServiceFilterArgs args) {
      ICustomContext context = (ICustomContext) args.getArgs().get(1);
      PictogramElement[] pes = context.getPictogramElements();
      return pes.length == 1
          && Stream.of(pes).filter(SimpleConnectionShape.class::isInstance)
              .map(SimpleConnectionShape.class::cast)
              .filter(scs -> GuiUtil.distanceToConnection(scs, context.getX(),
                  context.getY(), Constants.INTERNAL_PORT) < 5)
              .findFirst().isPresent();
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
