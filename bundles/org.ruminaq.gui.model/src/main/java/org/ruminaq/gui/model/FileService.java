/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.Transaction;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.impl.TransactionalEditingDomainImpl;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.ui.internal.services.GraphitiUiInternal;

/**
 *
 * @author Marek Jagielski
 */
public class FileService {
  
  private FileService() {
    // only static methods.
  }

  public static TransactionalEditingDomain createEmfFileForDiagram(
      URI diagramResourceUri, Diagram diagram, EObject model) {

    // Create a resource set and EditingDomain
    final TransactionalEditingDomain editingDomain = GraphitiUiInternal
        .getEmfService().createResourceSetAndEditingDomain();
    final ResourceSet resourceSet = editingDomain.getResourceSet();

    // Create a resource for this file.
    final Resource resource = resourceSet.createResource(diagramResourceUri);
    final CommandStack commandStack = editingDomain.getCommandStack();
    commandStack.execute(new RecordingCommand(editingDomain) {

      @Override
      protected void doExecute() {
        resource.setTrackingModification(true);
        resource.getContents().add(diagram);
        resource.getContents().add(model);
      }
    });

    save(editingDomain, Collections.<Resource, Map<?, ?>>emptyMap());
    editingDomain.dispose();

    return editingDomain;
  }

  private static void save(TransactionalEditingDomain editingDomain,
      Map<Resource, Map<?, ?>> options) {
    saveInWorkspaceRunnable(editingDomain, options);
  }

  private static void saveInWorkspaceRunnable(
      final TransactionalEditingDomain editingDomain,
      final Map<Resource, Map<?, ?>> options) {

    final Map<URI, Throwable> failedSaves = new HashMap<>();
    final IWorkspaceRunnable wsRunnable = new IWorkspaceRunnable() {
      @Override
      public void run(final IProgressMonitor monitor) throws CoreException {

        final Runnable runnable = () -> {
          Transaction parentTx;
          if (editingDomain != null
              && (parentTx = ((TransactionalEditingDomainImpl) editingDomain)
                  .getActiveTransaction()) != null) {
            do {
              if (!parentTx.isReadOnly()) {
                throw new IllegalStateException(
                    "FileService.save() called from within a command (likely produces a deadlock)"); //$NON-NLS-1$
              }
            } while ((parentTx = ((TransactionalEditingDomainImpl) editingDomain)
                .getActiveTransaction().getParent()) != null);
          }

          final EList<Resource> resources = editingDomain.getResourceSet()
              .getResources();
          // Copy list to an array to prevent
          // ConcurrentModificationExceptions
          // during the saving of the dirty resources
          Resource[] resourcesArray = new Resource[resources.size()];
          resourcesArray = resources.toArray(resourcesArray);
          final Set<Resource> savedResources = new HashSet<>();
          for (int i = 0; i < resourcesArray.length; i++) {
            // In case resource modification tracking is
            // switched on, we can check if a resource
            // has been modified, so that we only need to same
            // really changed resources; otherwise
            // we need to save all resources in the set
            final Resource resource = resourcesArray[i];
            if (resource.isModified()) {
              try {
                resource.save(options.get(resource));
                savedResources.add(resource);
              } catch (final Throwable t) {
                failedSaves.put(resource.getURI(), t);
              }
            }
          }
        };

        try {
          editingDomain.runExclusive(runnable);
        } catch (final InterruptedException e) {
          throw new RuntimeException(e);
        }
        editingDomain.getCommandStack().flush();
      }
    };
    try {
      ResourcesPlugin.getWorkspace().run(wsRunnable, null);
      if (!failedSaves.isEmpty()) {
//				throw new WrappedException(createMessage(failedSaves), new RuntimeException());
      }
    } catch (final CoreException e) {
      final Throwable cause = e.getStatus().getException();
      if (cause instanceof RuntimeException) {
        throw (RuntimeException) cause;
      }
      throw new RuntimeException(e);
    }
  }

//	public static void saveToModelFile(final EObject obj, final Diagram d) throws CoreException, IOException {
//		URI uri = d.eResource().getURI();
//		uri = uri.trimFragment();
//		uri = uri.trimFileExtension();
//		uri = uri.appendFileExtension(RuminaqConstants.MODEL_EXTENSION); //$NON-NLS-1$
//		ResourceSet rSet = d.eResource().getResourceSet();
//		final IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
//		IResource file = workspaceRoot.findMember(uri.toPlatformString(true));
//		if (file == null || !file.exists()) {
//			Resource createResource = rSet.createResource(uri);
//			createResource.save(Collections.emptyMap());
//			createResource.setTrackingModification(true);
//		}
//		final Resource resource = rSet.getResource(uri, true);
//		resource.getContents().add(obj);
//
//	}

}
