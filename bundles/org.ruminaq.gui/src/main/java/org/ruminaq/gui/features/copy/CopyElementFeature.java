/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.copy;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICopyContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.features.AbstractCopyFeature;
import org.ruminaq.consts.Constants;
import org.ruminaq.gui.model.diagram.LabelShape;
import org.ruminaq.model.ruminaq.BaseElement;

public class CopyElementFeature extends AbstractCopyFeature {

  public CopyElementFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canCopy(ICopyContext context) {
    final PictogramElement[] pes = context.getPictogramElements();
    if (pes == null || pes.length == 0) {
      return false;
    }

    boolean onlyLabels = true;
    for (PictogramElement pe : pes) {
      if (pe instanceof Shape && Graphiti.getPeService().getPropertyValue(pe,
          Constants.SIMPLE_CONNECTION_POINT) != null)
        continue;
      final Object bo = getBusinessObjectForPictogramElement(pe);
      if (!(bo instanceof BaseElement)) {
        return false;
      }
      if (!LabelShape.class.isInstance(pe)) {
        onlyLabels = false;
      }
    }
    if (onlyLabels) {
      return false;
    }

    return true;
  }

  @Override
  public void copy(ICopyContext context) {
    Set<PictogramElement> pes = new HashSet<>();
    for (PictogramElement pe : context.getPictogramElements()) {
      if (LabelShape.class.isInstance(pe)) {
        continue;
      } else if (pe instanceof Shape && Graphiti.getPeService()
          .getPropertyValue(pe, Constants.SIMPLE_CONNECTION_POINT) != null) {
        continue;
      } else {
        pes.add(pe);
      }
    }

    putToClipboard(pes.toArray());
  }
}
