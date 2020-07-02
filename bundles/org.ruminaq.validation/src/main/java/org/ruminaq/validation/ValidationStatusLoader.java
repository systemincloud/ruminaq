/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.validation.marker.MarkerUtil;
import org.eclipse.emf.validation.model.ConstraintStatus;
import org.eclipse.emf.validation.model.IModelConstraint;
import org.eclipse.emf.validation.service.ConstraintFactory;
import org.eclipse.emf.validation.service.ConstraintRegistry;
import org.eclipse.emf.validation.service.IConstraintDescriptor;
import org.ruminaq.consts.Constants.SicPlugin;

public class ValidationStatusLoader {

  public Set<EObject> load(TransactionalEditingDomain ed,
      List<IMarker> markers) {
    if (markers == null)
      return Collections.emptySet();
    Set<EObject> touched = new LinkedHashSet<>();
    for (IMarker marker : markers) {
      final EObject markedObject = getTargetObject(ed, marker);
      if (markedObject == null)
        continue;
      ValidationStatusAdapter statusAdapter = (ValidationStatusAdapter) EcoreUtil
          .getRegisteredAdapter(markedObject, ValidationStatusAdapter.class);

      // add the adapter factory for tracking validation errors
      if (statusAdapter == null) {
        ResourceSet resourceSet = ed.getResourceSet();
        resourceSet.getAdapterFactories()
            .add(new ValidationStatusAdapterFactory());
        statusAdapter = (ValidationStatusAdapter) EcoreUtil
            .getRegisteredAdapter(markedObject, ValidationStatusAdapter.class);
      }

      // convert the problem marker to an IStatus suitable for the validation
      // status adapter
      IStatus status = convertMarker(ed, marker, markedObject);
      statusAdapter.addValidationStatus(status);
      touched.add(markedObject);
    }
    return touched;
  }

  private EObject getTargetObject(TransactionalEditingDomain ed,
      IMarker marker) {
    final String uriString = marker.getAttribute(EValidator.URI_ATTRIBUTE,
        null);
    final URI uri = uriString == null ? null : URI.createURI(uriString);
    if (uri == null)
      return null;
    if (ed == null)
      return null;
    else
      return ed.getResourceSet().getEObject(uri, false);
  }

  // Code copied from EditUIMarkerHelper
  private List<?> getTargetObjects(Object object, IMarker marker) {
    ArrayList<Object> result = new ArrayList<>();
    if (object instanceof AdapterFactoryEditingDomain) {
      AdapterFactoryEditingDomain editingDomain = (AdapterFactoryEditingDomain) object;
      String uriAttribute = marker.getAttribute(EValidator.URI_ATTRIBUTE, null);
      if (uriAttribute == null)
        uriAttribute = marker.getAttribute("URI_KEY", null);
      if (uriAttribute != null) {
        URI uri = URI.createURI(uriAttribute);
        try {
          EObject eObject = editingDomain.getResourceSet().getEObject(uri,
              true);
          if (eObject != null)
            result.add(editingDomain.getWrapper(eObject));
        } catch (Throwable throwable) {
        }
      }
    }
    return result;
  }

  @SuppressWarnings("unchecked")
  private IStatus convertMarker(TransactionalEditingDomain ed, IMarker marker,
      EObject target) {
    final String message = marker.getAttribute(IMarker.MESSAGE, "");
    final String constraintId = marker.getAttribute(MarkerUtil.RULE_ATTRIBUTE,
        null);
    final IConstraintDescriptor icd = constraintId == null ? null
        : ConstraintRegistry.getInstance().getDescriptor(constraintId);
    final IModelConstraint imc = icd == null ? null
        : ConstraintFactory.getInstance().newConstraint(icd);
    if (imc == null) {
      final int severity;
      switch (marker.getAttribute(IMarker.SEVERITY, -1)) {
        case IMarker.SEVERITY_INFO:
          severity = IStatus.INFO;
          break;
        case IMarker.SEVERITY_WARNING:
          severity = IStatus.WARNING;
          break;
        case IMarker.SEVERITY_ERROR:
          severity = IStatus.ERROR;
          break;
        default:
          severity = IStatus.OK;
      }
      return new Status(severity, SicPlugin.ECLIPSE_ID.s(), message);
    }
    try {
      marker.setAttribute(EValidator.RELATED_URIS_ATTRIBUTE,
          marker.getAttribute(EValidator.RELATED_URIS_ATTRIBUTE, null));
    } catch (CoreException e) {
    }
    List<?> locus = getTargetObjects(ed, marker);
    for (Iterator<?> it = locus.iterator(); it.hasNext();)
      if (!(it.next() instanceof EObject))
        it.remove();

    return new ConstraintStatus(imc, target, message, locus == null ? null
        : new LinkedHashSet<EObject>((List<? extends EObject>) locus));
  }
}
