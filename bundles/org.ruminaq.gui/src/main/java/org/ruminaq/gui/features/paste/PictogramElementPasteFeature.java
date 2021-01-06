/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.paste;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IPasteContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.features.AbstractPasteFeature;
import org.ruminaq.gui.model.diagram.RuminaqDiagram;

/**
 * IPasteFeature for any PictogramElement.
 *
 * @author Marek Jagielski
 */
public class PictogramElementPasteFeature<T extends PictogramElement>
    extends AbstractPasteFeature {

  protected List<PictogramElement> newPes = new LinkedList<>();

  protected T oldPe;
  protected T newPe;

  protected PictogramElementPasteFeature(IFeatureProvider fp, T oldPe) {
    super(fp);
    this.oldPe = oldPe;
  }

  public List<PictogramElement> getNewPictogramElements() {
    return newPes;
  }

  protected RuminaqDiagram getRuminaqDiagram() {
    return (RuminaqDiagram) getDiagram();
  }

  @Override
  public void paste(IPasteContext context) {
    newPe = EcoreUtil.copy(oldPe);
    newPes.add(newPe);
  }

  @Override
  public boolean canPaste(IPasteContext context) {
    return false;
  }
}
