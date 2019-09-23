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
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;
import org.eclipse.swt.widgets.Display;

public class MarkerChangeListener implements IResourceChangeListener {

  private IFile file;
  private TransactionalEditingDomain ed;
  private DiagramBehavior db;
  private Display display;

  public MarkerChangeListener(IFile file, TransactionalEditingDomain ed,
      DiagramBehavior db, Display dispaly) {
    this.file = file;
    this.ed = ed;
    this.db = db;
    this.display = dispaly;
  }

  @Override
  public void resourceChanged(IResourceChangeEvent event) {
    if (file == null)
      return;
    final IResourceDelta modelFileDelta = event.getDelta()
        .findMember(file.getFullPath());
    if (modelFileDelta == null)
      return;
    final IMarkerDelta[] markerDeltas = modelFileDelta.getMarkerDeltas();
    if (markerDeltas == null || markerDeltas.length == 0)
      return;

    final List<IMarker> newMarkers = new ArrayList<>();
    final Set<String> deletedMarkers = new HashSet<>();
    for (IMarkerDelta markerDelta : markerDeltas) {
      switch (markerDelta.getKind()) {
        case IResourceDelta.ADDED:
          newMarkers.add(markerDelta.getMarker());
          break;
        case IResourceDelta.CHANGED:
          newMarkers.add(markerDelta.getMarker());
          // fall through
        case IResourceDelta.REMOVED:
          final String uri = markerDelta.getAttribute(EValidator.URI_ATTRIBUTE,
              null);
          if (uri != null)
            deletedMarkers.add(uri);
      }
    }

    final Set<EObject> updatedObjects = new LinkedHashSet<>();
    for (String uri : deletedMarkers) {
      if (ed == null || ed.getResourceSet() == null)
        continue;
      final EObject eobject = ed.getResourceSet().getEObject(URI.createURI(uri),
          false);
      if (eobject == null)
        continue;
      final ValidationStatusAdapter adapter = (ValidationStatusAdapter) EcoreUtil
          .getRegisteredAdapter(eobject, ValidationStatusAdapter.class);
      if (adapter == null)
        continue;
      adapter.clearValidationStatus();
      updatedObjects.add(eobject);
    }

    updatedObjects.addAll(new ValidationStatusLoader().load(ed, newMarkers));
    display.asyncExec(new Runnable() {
      @Override
      public void run() {
        db.getRefreshBehavior().refresh();
      }
    });
  }

}
