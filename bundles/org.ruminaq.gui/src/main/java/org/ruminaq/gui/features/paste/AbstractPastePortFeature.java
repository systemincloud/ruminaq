/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.paste;

import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.ruminaq.gui.model.diagram.PortShape;

/**
 * IPasteFeature for Port.
 *
 * @author Marek Jagielski
 */
public abstract class AbstractPastePortFeature<T extends PortShape>
    extends LabeledRuminaqPasteFeature<T> implements PasteAnchorTracker {

  protected AbstractPastePortFeature(IFeatureProvider fp, T oldPe, int xMin,
      int yMin) {
    super(fp, oldPe, xMin, yMin);
  }

  @Override
  public Map<Anchor, Anchor> getAnchors() {
    Iterator<Anchor> keyIter = oldPe.getAnchors().iterator();
    Iterator<Anchor> valIter = newPe.getAnchors().iterator();
    return IntStream.range(0, oldPe.getAnchors().size()).boxed()
        .collect(Collectors.toMap(i -> keyIter.next(), i -> valIter.next()));
  }
}
