/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.properties;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.platform.AbstractPropertySectionFilter;
import org.ruminaq.gui.LabelUtil;
import org.ruminaq.model.ruminaq.InputPort;

public class PropertyInputPortFilter extends AbstractPropertySectionFilter {

  @Override
  protected boolean accept(PictogramElement pe) {
    EObject eObject = Graphiti.getLinkService()
        .getBusinessObjectForLinkedPictogramElement(pe);
    if (eObject instanceof InputPort && Graphiti.getPeService()
        .getPropertyValue(pe, LabelUtil.LABEL_PROPERTY) == null)
      return true;
    else
      return false;
  }

}
