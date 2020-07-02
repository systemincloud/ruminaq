/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.cmd;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.transaction.Transaction;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.impl.InternalTransactionalEditingDomain;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.IThreadListener;
import org.eclipse.jface.operation.ModalContext;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PlatformUI;
import org.ruminaq.consts.Constants;
import org.ruminaq.model.ruminaq.MainTask;
import org.ruminaq.model.ruminaq.ModelUtil;
import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.model.ruminaq.UserDefinedTask;

public class UpdateDiagram {

  private boolean updated = false;

  protected final class SaveOperation
      implements IRunnableWithProgress, IThreadListener {
    private final Map<Resource, Map<?, ?>> saveOptions;
    private final Set<Resource> savedResources;
    private final TransactionalEditingDomain ed;

    private SaveOperation(Map<Resource, Map<?, ?>> saveOptions,
        Set<Resource> savedResources, TransactionalEditingDomain ed) {
      this.saveOptions = saveOptions;
      this.savedResources = savedResources;
      this.ed = ed;
    }

    public void run(IProgressMonitor monitor) {
      try {
        savedResources.addAll(save(ed, saveOptions, monitor));
      } catch (final WrappedException e) {
      }
    }

    @Override
    public void threadChange(Thread thread) {
      ISchedulingRule rule = Job.getJobManager().currentRule();
      if (rule != null)
        Job.getJobManager().transferRule(rule, thread);
    }
  }

  protected Set<Resource> save(final TransactionalEditingDomain ed,
      final Map<Resource, Map<?, ?>> saveOptions, IProgressMonitor monitor) {

    final Set<Resource> savedResources = new HashSet<Resource>();
    final IWorkspaceRunnable wsRunnable = new IWorkspaceRunnable() {
      public void run(final IProgressMonitor monitor) throws CoreException {

        final Runnable runnable = new Runnable() {
          public void run() {
            Transaction parentTx;
            if (ed != null
                && (parentTx = ((InternalTransactionalEditingDomain) ed)
                    .getActiveTransaction()) != null) {
              do {
                if (!parentTx.isReadOnly())
                  throw new IllegalStateException(
                      "saveInWorkspaceRunnable() called from within a command (likely to produce deadlock)"); //$NON-NLS-1$
              } while ((parentTx = ((InternalTransactionalEditingDomain) ed)
                  .getActiveTransaction().getParent()) != null);
            }

            final EList<Resource> resources = ed.getResourceSet()
                .getResources();
            Resource[] resourcesArray = new Resource[resources.size()];
            resourcesArray = resources.toArray(resourcesArray);
            for (int i = 0; i < resourcesArray.length; i++) {
              final Resource resource = resourcesArray[i];

              if (shouldSave(resource, ed)) {
                try {
                  resource.save(saveOptions.get(resource));
                  savedResources.add(resource);
                } catch (final Throwable t) {
                }
              }
            }
          }
        };

        try {
          ed.runExclusive(runnable);
        } catch (final InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
    };

    try {
      ResourcesPlugin.getWorkspace().run(wsRunnable, null);
    } catch (final CoreException e) {
    }

    return savedResources;
  }

  protected boolean shouldSave(Resource resource,
      TransactionalEditingDomain ed) {
    return !ed.isReadOnly(resource)
        && (!resource.isTrackingModification() || resource.isModified())
        && resource.isLoaded();
  }

  public void updateDiagram(IResource file) {
    TransactionalEditingDomain ed = TransactionalEditingDomain.Factory.INSTANCE
        .createEditingDomain();
    ResourceSet resSet = ed.getResourceSet();

    Resource resource = null;
    try {
      resource = resSet
          .getResource(URI.createURI(file.getFullPath().toOSString()), true);
    } catch (Exception e) {
    }

    if (resource == null || resource.getContents().size() < 2) {
      return;
    }

    Diagram d = (Diagram) resource.getContents().get(0);
    MainTask mt = (MainTask) resource.getContents().get(1);

    final IFeatureProvider fp = GraphitiUi.getExtensionManager()
        .createFeatureProvider(d);

    for (Task t : mt.getTask()) {
      if (t instanceof UserDefinedTask) {
        final List<PictogramElement> ps = Graphiti.getLinkService()
            .getPictogramElements(d, t);
        if (!ps.isEmpty()) {
          ModelUtil.runModelChange(new Runnable() {
            public void run() {
              UpdateContext context = new UpdateContext(ps.get(0));
              updated = updated | fp.updateIfPossible(context).toBoolean();
            }
          }, ed, "Update diagram");
        }
      }
    }

    if (updated) {
      save(resource, fp.getDiagramTypeProvider(), ed);
      for (final IEditorReference er : PlatformUI.getWorkbench()
          .getActiveWorkbenchWindow().getActivePage().getEditorReferences()) {
        if (Constants.DIAGRAM_EDITOR_ID.equals(er.getId())
            || Constants.TEST_DIAGRAM_EDITOR_ID.equals(er.getId())) {
          Display.getCurrent().asyncExec(new Runnable() {
            public void run() {
              try {
                URL fileUrl = FileLocator.toFileURL(new URL(er.getName()));
                IFile file = ResourcesPlugin.getWorkspace().getRoot()
                    .getFileForLocation(new Path(fileUrl.getPath()));
                file.refreshLocal(IResource.DEPTH_ZERO, null);
              } catch (IOException | CoreException e) {
              }
            }
          });
        }
      }
    }
  }

  private void save(Resource r, IDiagramTypeProvider dtp,
      TransactionalEditingDomain ed) {
    final Map<Object, Object> saveOption = new HashMap<Object, Object>();
    saveOption.put(Resource.OPTION_SAVE_ONLY_IF_CHANGED,
        Resource.OPTION_SAVE_ONLY_IF_CHANGED_MEMORY_BUFFER);
    final Map<Resource, Map<?, ?>> saveOptions = new HashMap<Resource, Map<?, ?>>();
    saveOptions.put(r, saveOption);

    final Set<Resource> savedResources = new HashSet<Resource>();
    final IRunnableWithProgress operation = new SaveOperation(saveOptions,
        savedResources, ed);

    try {
      ModalContext.run(operation, true, new NullProgressMonitor(),
          Display.getDefault());

      BasicCommandStack commandStack = (BasicCommandStack) ed.getCommandStack();
      commandStack.saveIsDone();
    } catch (Exception exception) {
    }

    Resource[] savedResourcesArray = savedResources
        .toArray(new Resource[savedResources.size()]);
    dtp.resourcesSaved(dtp.getDiagram(), savedResourcesArray);
  }
}
