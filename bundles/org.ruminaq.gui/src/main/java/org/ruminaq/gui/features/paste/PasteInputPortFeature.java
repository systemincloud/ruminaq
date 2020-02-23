/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.paste;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IPasteContext;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.ruminaq.gui.features.FeaturePredicate;
import org.ruminaq.gui.features.PasteFeatureFilter;
import org.ruminaq.gui.features.paste.PasteInputPortFeature.Filter;
import org.ruminaq.gui.model.diagram.InputPortShape;
import org.ruminaq.gui.model.diagram.RuminaqDiagram;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.model.ruminaq.InputPort;

/**
 * IPasteFeature for InputPort.
 *
 * @author Marek Jagielski
 */
@PasteFeatureFilter(Filter.class)
public class PasteInputPortFeature extends
    LabeledRuminaqPasteFeature<InputPortShape> implements PasteAnchorTracker {

  public static class Filter implements FeaturePredicate<BaseElement> {
    @Override
    public boolean test(BaseElement bo) {
      return bo instanceof InputPort;
    }
  }

  private Map<Anchor, Anchor> anchors = new HashMap<>();

  public PasteInputPortFeature(IFeatureProvider fp, PictogramElement oldPe,
      int xMin, int yMin) {
    super(fp, (InputPortShape) oldPe, xMin, yMin);
  }

  @Override
  public boolean canPaste(IPasteContext context) {
    PictogramElement[] pes = context.getPictogramElements();
    return pes.length == 1 && pes[0] instanceof RuminaqDiagram;
  }

  @Override
  public void paste(IPasteContext context) {
    int x = context.getX();
    int y = context.getY();
    InputPortShape newPe = super.paste(x, y);
    getRuminaqDiagram().getMainTask().getInputPort()
        .add((InputPort) newPe.getModelObject());

    Iterator<Anchor> itOld = oldPe.getAnchors().iterator();
    Iterator<Anchor> itNew = newPe.getAnchors().iterator();
    while (itOld.hasNext() && itNew.hasNext()) {
      anchors.put(itOld.next(), itNew.next());
    }
  }

  @Override
  public Map<Anchor, Anchor> getAnchors() {
    return anchors;
  }
}
