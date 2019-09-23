/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.styles;

import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.mm.algorithms.styles.Color;
import org.eclipse.graphiti.mm.algorithms.styles.GradientColoredArea;
import org.eclipse.graphiti.mm.algorithms.styles.LocationType;
import org.eclipse.graphiti.mm.algorithms.styles.StylesFactory;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.util.ColorUtil;

public class Styles {

  protected static void addGradientColoredArea(EList<GradientColoredArea> gcas,
      String colorStart, int locationValueStart, LocationType locationTypeStart,
      String colorEnd, int locationValueEnd, LocationType locationTypeEnd,
      Diagram diagram) {
    final GradientColoredArea gca = StylesFactory.eINSTANCE
        .createGradientColoredArea();
    gcas.add(gca);
    IGaService gaService = Graphiti.getGaService();

    gca.setStart(StylesFactory.eINSTANCE.createGradientColoredLocation());
    final Color startColor = gaService.manageColor(diagram,
        ColorUtil.getRedFromHex(colorStart),
        ColorUtil.getGreenFromHex(colorStart),
        ColorUtil.getBlueFromHex(colorStart));
    gca.getStart().setColor(startColor);
    gca.getStart().setLocationType(locationTypeStart);
    gca.getStart().setLocationValue(locationValueStart);

    gca.setEnd(StylesFactory.eINSTANCE.createGradientColoredLocation());
    final Color endColor = gaService.manageColor(diagram,
        ColorUtil.getRedFromHex(colorEnd), ColorUtil.getGreenFromHex(colorEnd),
        ColorUtil.getBlueFromHex(colorEnd));
    gca.getEnd().setColor(endColor);
    gca.getEnd().setLocationType(locationTypeEnd);
    gca.getEnd().setLocationValue(locationValueEnd);
  }

}
