/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.paste;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IPasteContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.ruminaq.gui.model.diagram.RuminaqShape;

public class PasteDefaultElementFeature<T extends RuminaqShape>
    extends RuminaqShapePasteFeature<T> {

  public PasteDefaultElementFeature(IFeatureProvider fp, T oldPe,
      int xMin, int yMin) {
    super(fp, oldPe, yMin, yMin);
  }

  @Override
  public boolean canPaste(IPasteContext context) {
    PictogramElement[] pes = context.getPictogramElements();
    return pes.length == 1 && pes[0] instanceof Diagram;
  }

  @Override
  public void paste(IPasteContext context) {
  }
}
