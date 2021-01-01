/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.cmd;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.Transaction;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.impl.InternalTransactionalEditingDomain;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.IThreadListener;
import org.eclipse.jface.operation.ModalContext;
import org.eclipse.swt.widgets.Display;
import org.ruminaq.eclipse.editor.RuminaqEditor;
import org.ruminaq.gui.model.diagram.RuminaqDiagram;
import org.ruminaq.model.ruminaq.ModelUtil;
import org.ruminaq.util.Try;

/**
 * Update all Tasks.
 *
 * @author Marek Jagielski
 */
public class UpdateDiagram {

  private final class SaveOperation
      implements IRunnableWithProgress, IThreadListener {
    private final TransactionalEditingDomain ed;

    private SaveOperation(TransactionalEditingDomain ed) {
      this.ed = ed;
    }

    public void run(IProgressMonitor monitor) {
      save(ed, saveOptions);
    }

    @Override
    public void threadChange(Thread thread) {
      Optional.ofNullable(Job.getJobManager().currentRule())
          .ifPresent(rule -> Job.getJobManager().transferRule(rule, thread));
    }
  }

  private final Map<Resource, Map<?, ?>> saveOptions = new HashMap<>();
  private final Set<Resource> savedResources = new HashSet<>();

  protected void save(final TransactionalEditingDomain ed,
      Map<Resource, Map<?, ?>> saveOptions) {
    IWorkspaceRunnable wsRunnable = new IWorkspaceRunnable() {
      public void run(IProgressMonitor monitor) {
        Runnable runnable = () -> {
          Transaction parentTx;
          if (ed != null
              && (parentTx = ((InternalTransactionalEditingDomain) ed)
                  .getActiveTransaction()) != null) {
            do {
              if (!parentTx.isReadOnly())
                throw new IllegalStateException(
                    "saveInWorkspaceRunnable() called from within a command (likely to produce deadlock)");
            } while ((parentTx = ((InternalTransactionalEditingDomain) ed)
                .getActiveTransaction().getParent()) != null);
          }

          ed.getResourceSet().getResources().stream()
              .filter(r -> shouldSave(r, ed)).forEach((Resource r) -> {
                if (!Try.check(() -> r.save(saveOptions.get(r))).isFailed()) {
                  savedResources.add(r);
                }
              });
        };
        Try.check(() -> ed.runExclusive(runnable));
      }
    };

    Try.check(() -> ResourcesPlugin.getWorkspace().run(wsRunnable, null));

  }

  private void save(Resource r, IDiagramTypeProvider dtp,
      TransactionalEditingDomain ed) {
    Map<Object, Object> saveOption = new HashMap<>();
    saveOption.put(Resource.OPTION_SAVE_ONLY_IF_CHANGED,
        Resource.OPTION_SAVE_ONLY_IF_CHANGED_MEMORY_BUFFER);
    saveOptions.put(r, saveOption);
    Try.check(() -> ModalContext.run(new SaveOperation(ed), true,
        new NullProgressMonitor(), Display.getDefault()));
    BasicCommandStack commandStack = (BasicCommandStack) ed.getCommandStack();
    commandStack.saveIsDone();
    dtp.resourcesSaved(dtp.getDiagram(),
        savedResources.stream().toArray(Resource[]::new));
  }

  protected boolean shouldSave(Resource resource,
      TransactionalEditingDomain ed) {
    return !ed.isReadOnly(resource)
        && (!resource.isTrackingModification() || resource.isModified())
        && resource.isLoaded();
  }

  /**
   * Iterate over all Tasks and update if needed.
   *
   * @param file eclipse resource of diagram
   */
  public void updateDiagram(IResource file) {
    TransactionalEditingDomain ed = TransactionalEditingDomain.Factory.INSTANCE
        .createEditingDomain();

    Optional<Resource> resource = Optional.of(file).map(IResource::getFullPath)
        .map(IPath::toOSString).map(URI::createURI)
        .map(uri -> ed.getResourceSet().getResource(uri, true));
    Optional<RuminaqDiagram> diagram = resource.map(Resource::getContents)
        .map(EList::stream).orElseGet(Stream::empty).findFirst()
        .filter(RuminaqDiagram.class::isInstance)
        .map(RuminaqDiagram.class::cast);
    Optional<IFeatureProvider> fp = diagram
        .map(GraphitiUi.getExtensionManager()::createFeatureProvider);

    if (diagram.isPresent() && fp.isPresent() && resource.isPresent()) {
      ModelUtil.runModelChange(() -> RuminaqEditor
          .updateShapes(diagram.get().getChildren(), fp.get()), ed,
          "Update shapes");
      Display.getCurrent().asyncExec(
          () -> Try.check(() -> file.refreshLocal(IResource.DEPTH_ZERO, null)));
      save(resource.get(), fp.get().getDiagramTypeProvider(), ed);
    }
  }
}
