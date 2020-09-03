/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.validation;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.validation.model.IClientSelector;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.model.ruminaq.RuminaqPackage;

public class Selector implements IClientSelector {

  @Override
  public boolean selects(Object object) {
    if (object instanceof EObject) {
      EObject eObj = (EObject) object;
      if (check(eObj))
        return true;
      if (eObj instanceof PictogramElement) {
        eObj = Graphiti.getLinkService()
            .getBusinessObjectForLinkedPictogramElement(
                (PictogramElement) eObj);
        if (eObj != null && check(eObj))
          return true;
      }
    }
    return false;
  }

  private boolean check(EObject eObj) {
    if (eObj instanceof BaseElement)
      return true;
    if (eObj.eClass().getEPackage() == RuminaqPackage.eINSTANCE)
      return true;
    else
      return false;
  }
}
