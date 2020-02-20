/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.copy;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICopyContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.features.AbstractCopyFeature;
import org.ruminaq.gui.model.diagram.LabelShape;
import org.ruminaq.gui.model.diagram.RuminaqShape;

public class CopyElementFeature extends AbstractCopyFeature {

  public CopyElementFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canCopy(ICopyContext context) {
    PictogramElement[] pes = context.getPictogramElements();
    if (Optional.ofNullable(pes).map(Arrays::asList).stream()
        .flatMap(List::stream).findAny().isEmpty()) {
      return false;
    }

    return !((Stream.of(pes)
        .anyMatch(Predicate.not(RuminaqShape.class::isInstance)))
        || Stream.of(pes).allMatch(LabelShape.class::isInstance));
  }

  @Override
  public void copy(ICopyContext context) {
    putToClipboard(Stream.of(context.getPictogramElements())
        .filter(Predicate.not(LabelShape.class::isInstance))
        .filter(p -> true /* connection point */)
        .toArray(PictogramElement[]::new));
  }
}
