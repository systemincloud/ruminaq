/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.editor;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IOperationHistoryListener;
import org.eclipse.core.commands.operations.OperationHistoryEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.workspace.IWorkspaceCommandStack;
import org.eclipse.gef.editparts.LayerManager;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.ruminaq.consts.Constants;
import org.ruminaq.eclipse.ConstantsUtil;
import org.ruminaq.eclipse.api.EclipseExtension;
import org.ruminaq.model.ModelHandler;
import org.ruminaq.model.ruminaq.MainTask;
import org.ruminaq.model.util.ModelUtil;
import org.ruminaq.prefs.ProjectProps;
import org.ruminaq.util.ServiceUtil;
import org.ruminaq.validation.MarkerChangeListener;
import org.ruminaq.validation.ProjectValidator;
import org.ruminaq.validation.ValidationStatusLoader;

/**
 * Main Ruminaq editor class.
 *
 * @author Marek Jagielski
 */
public class RuminaqEditor extends DiagramEditor {

  private IResourceChangeListener markerChangeListener;

  private ExecutorService validationExecutor;

  public RuminaqEditor() {
    this.validationExecutor = Executors.newSingleThreadExecutor();
  }

  @Override
  protected DiagramBehavior createDiagramBehavior() {
    return new RuminaqDiagramBehavior(this);
  }

  /**
   * Hides grid on diagram, but you can reenable it.
   */
  @Override
  public void createPartControl(Composite parent) {
    super.createPartControl(parent);
    Optional.ofNullable(getGraphicalViewer())
        .map(gv -> gv.getEditPartRegistry())
        .map(epr -> epr.get(LayerManager.ID))
        .filter(ScalableFreeformRootEditPart.class::isInstance)
        .map(epr -> (ScalableFreeformRootEditPart) epr);

//      ScalableFreeformRootEditPart rootEditPart = (ScalableFreeformRootEditPart) getGraphicalViewer()
//          .getEditPartRegistry().get(LayerManager.ID);
//      IFigure gridFigure = ((LayerManager) rootEditPart)
//          .getLayer(LayerConstants.GRID_LAYER);
//      gridFigure.setVisible(false);
//    }
  }

  @Override
  public void init(IEditorSite site, IEditorInput input)
      throws PartInitException {
    ServiceUtil
        .getServicesAtLatestVersion(RuminaqEditor.class, EclipseExtension.class)
        .stream().forEach(EclipseExtension::initEditor);
    super.init(site, input);
    IOperationHistory history = getOperationHistory();
    if (history != null) {
      getOperationHistory()
          .addOperationHistoryListener(new IOperationHistoryListener() {
            @Override
            public void historyNotification(OperationHistoryEvent event) {
              switch (event.getEventType()) {
                case OperationHistoryEvent.DONE:
                case OperationHistoryEvent.REDONE:
                case OperationHistoryEvent.UNDONE:
                  doSave(new NullProgressMonitor());
                  break;
              }
            }
          });
    }
    addMarkerChangeListener();

    final MainTask mt = ModelHandler.getModel(
        getDiagramTypeProvider().getDiagram(),
        getDiagramTypeProvider().getFeatureProvider());
    if (!mt.isInitialized()) {
      TransactionalEditingDomain editingDomain = getDiagramBehavior()
          .getEditingDomain();
      ModelUtil.runModelChange(
          () -> mt
              .setVersion(ProjectProps.getInstance(getModelFile().getProject())
                  .get(ProjectProps.MODELER_VERSION)),
          editingDomain, "Model Update");

      if (ConstantsUtil
          .isTest(getDiagramTypeProvider().getDiagram().eResource().getURI())) {
        ModelUtil.runModelChange(() -> {
          if (((ContainerShape) getDiagramTypeProvider().getDiagram())
              .getChildren().size() != 0) {
            UpdateContext context = new UpdateContext(
                ((ContainerShape) getDiagramTypeProvider().getDiagram())
                    .getChildren().get(0));
            getDiagramTypeProvider().getFeatureProvider()
                .updateIfPossible(context);
          }
        }, editingDomain, "Model Update");
      }
      ModelUtil.runModelChange(() -> mt.setInitialized(true), editingDomain,
          "Model Update");
    }
  }

  private IOperationHistory getOperationHistory() {
    IOperationHistory history = null;
    if (getEditingDomain() != null) {
      final IWorkspaceCommandStack commandStack = (IWorkspaceCommandStack) getEditingDomain()
          .getCommandStack();
      if (commandStack != null) {
        history = commandStack.getOperationHistory();
      }
    }
    return history;
  }

  @Override
  public boolean isDirty() {
    return false;
  }

  private void addMarkerChangeListener() {
    if (getModelFile() != null) {
      if (markerChangeListener == null) {
        markerChangeListener = new MarkerChangeListener(getModelFile(),
            getEditingDomain(), getDiagramBehavior(),
            getEditorSite().getShell().getDisplay());
        getModelFile().getWorkspace().addResourceChangeListener(
            markerChangeListener, IResourceChangeEvent.POST_BUILD);
      }
    }
  }

  @Override
  protected void setInput(IEditorInput input) {
    super.setInput(input);
    loadMarkers();
  }

  private void loadMarkers() {
    if (getModelFile() != null) {
      try {
        (new ValidationStatusLoader()).load(getEditingDomain(),
            Arrays.asList(getModelFile().findMarkers(
                Constants.VALIDATION_MARKER, true, IResource.DEPTH_ZERO)));
      } catch (CoreException e) {
      }
    }
  }

  public IFile getModelFile() {
    return Optional.ofNullable(getDiagramTypeProvider())
        .map(IDiagramTypeProvider::getDiagram).map(Diagram::eResource)
        .map(Resource::getURI).map(URI::trimFragment)
        .map(uri -> uri.toPlatformString(true)).map(uriString -> ResourcesPlugin
            .getWorkspace().getRoot().getFile(new Path(uriString)))
        .get();
  }

  @Override
  public void doSave(final IProgressMonitor monitor) {
    super.doSave(monitor);
    validationExecutor.execute(() -> ProjectValidator.validateOnSave(
        getDiagramTypeProvider().getDiagram().eResource(), monitor));
  }

  @Override
  public void close() {
    super.close();
    validationExecutor.shutdown();
  }
}
