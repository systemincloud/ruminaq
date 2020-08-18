/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.contextmenu;

import java.util.function.Predicate;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.ContextMenuEntryExtension;
import org.ruminaq.gui.features.contextmenu.AllInternalPortToggleBreakpointContextMenu.Filter;
import org.ruminaq.gui.features.custom.AllInternalPortToggleBreakpointFeature;
import org.ruminaq.gui.model.diagram.RuminaqDiagram;
import org.ruminaq.util.ServiceFilter;
import org.ruminaq.util.ServiceFilterArgs;

/**
 * Service ContextMenuEntryExtension implementation.
 *
 * @author Marek Jagielski
 */
@Component(property = { "service.ranking:Integer=5" })
@ServiceFilter(Filter.class)
public class AllInternalPortToggleBreakpointContextMenu
    implements ContextMenuEntryExtension {

  /**
   * Has to be RuminaqDiagram.
   */
  public static class Filter implements Predicate<ServiceFilterArgs> {

    @Override
    public boolean test(ServiceFilterArgs args) {
      ICustomContext context = (ICustomContext) args.getArgs().get(1);
      PictogramElement[] pes = context.getPictogramElements();
      return pes.length == 1 && pes[0] instanceof RuminaqDiagram;
    }
  }

  @Override
  public Predicate<ICustomFeature> isAvailable(ICustomContext context) {
    return customFeature -> customFeature.isAvailable(context)
        && AllInternalPortToggleBreakpointFeature.NAME
            .equals(customFeature.getName());
  }

}
