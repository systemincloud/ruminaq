/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.contextmenu;

import java.util.Optional;
import java.util.function.Predicate;

import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.ContextMenuEntryExtension;
import org.ruminaq.gui.features.contextmenu.CreateSimpleConnectionPointContextMenu.Filter;
import org.ruminaq.gui.features.create.CreateSimpleConnectionPointFeature;
import org.ruminaq.gui.model.diagram.RuminaqDiagram;
import org.ruminaq.gui.model.diagram.SimpleConnectionShape;
import org.ruminaq.gui.model.diagram.impl.simpleconnection.SimpleConnectionUtil;
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

  private static final int DISTANCE_TOLERANCE_ON_SELECTED_CONNECTION = 15;

  private static final int DISTANCE_TOLERANCE_ON_UNSELECTED_CONNECTION = 8;

  public static class Filter implements Predicate<ServiceFilterArgs> {

    @Override
    public boolean test(ServiceFilterArgs args) {
      ICustomContext context = (ICustomContext) args.getArgs().get(1);
      PictogramElement[] pes = context.getPictogramElements();
      if (pes.length == 1) {
        if (pes[0] instanceof SimpleConnectionShape) {
          return SimpleConnectionUtil.distanceToConnection(
              (SimpleConnectionShape) pes[0], context.getX(),
              context.getY()) < DISTANCE_TOLERANCE_ON_SELECTED_CONNECTION;
        } else if (pes[0] instanceof RuminaqDiagram) {
          return Optional.of(pes[0]).filter(RuminaqDiagram.class::isInstance)
              .map(RuminaqDiagram.class::cast)
              .map(RuminaqDiagram::getConnections).stream()
              .flatMap(EList::stream)
              .filter(SimpleConnectionShape.class::isInstance)
              .map(SimpleConnectionShape.class::cast)
              .anyMatch(scs -> SimpleConnectionUtil.distanceToConnection(scs,
                  context.getX(),
                  context.getY()) < DISTANCE_TOLERANCE_ON_UNSELECTED_CONNECTION);
        } else {
          return false;
        }
      }

      return false;
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
