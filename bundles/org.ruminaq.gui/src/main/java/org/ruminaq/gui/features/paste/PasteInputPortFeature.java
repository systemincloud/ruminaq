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
import org.ruminaq.gui.features.ModelFeatureFilter;
import org.ruminaq.gui.model.diagram.InputPortShape;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.model.ruminaq.InputPort;

/**
 * IPasteFeature for InputPort.
 *
 * @author Marek Jagielski
 */
@ModelFeatureFilter(PasteInputPortFeature.Filter.class)
public class PasteInputPortFeature extends
    LabeledRuminaqPasteFeature<InputPortShape> implements PasteAnchorTracker {

  private static class Filter implements FeaturePredicate<BaseElement> {
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
  public void paste(IPasteContext context) {
    super.paste(context);
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
