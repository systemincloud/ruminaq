/*
 * (C) Copyright 2018 Marek Jagielski.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
		if(object instanceof EObject) {
			EObject eObj = (EObject) object;
			if(check(eObj)) return true;
			if(eObj instanceof PictogramElement) {
				eObj = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement((PictogramElement)eObj);
				if(eObj != null && check(eObj)) return true;
			}
		}
		return false;
	}

	private boolean check(EObject eObj) {
		if(eObj instanceof BaseElement)                                   return true;
		if(eObj.eClass().getEPackage() == RuminaqPackage.eINSTANCE) return true;
		else return false;
	}
}
