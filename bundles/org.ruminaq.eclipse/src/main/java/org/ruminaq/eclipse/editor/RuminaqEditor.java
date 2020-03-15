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
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.workspace.IWorkspaceCommandStack;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.LayerManager;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.ruminaq.consts.Constants;
import org.ruminaq.eclipse.Messages;
import org.ruminaq.eclipse.api.EclipseExtension;
import org.ruminaq.gui.model.diagram.DiagramPackage;
import org.ruminaq.gui.model.diagram.RuminaqDiagram;
import org.ruminaq.logs.ModelerLoggerFactory;
import org.ruminaq.model.util.ModelUtil;
import org.ruminaq.util.ServiceUtil;
import org.ruminaq.validation.MarkerChangeListener;
import org.ruminaq.validation.ProjectValidator;
import org.ruminaq.validation.ValidationStatusLoader;
import org.slf4j.Logger;;

/**
 * Main Ruminaq editor class.
 *
 * @author Marek Jagielski
 */
public class RuminaqEditor extends DiagramEditor {

  private static final Logger LOGGER = ModelerLoggerFactory
      .getLogger(RuminaqEditor.class);

  private ExecutorService validationExecutor;

  private IResourceChangeListener markerChangeListener;

  public RuminaqEditor() {
    this.validationExecutor = Executors.newSingleThreadExecutor();
  }

  /**
   * You can change default behavior of all editors.
   */
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
        .map(GraphicalViewer::getEditPartRegistry)
        .map(epr -> epr.get(LayerManager.ID))
        .filter(ScalableFreeformRootEditPart.class::isInstance)
        .map(epr -> (ScalableFreeformRootEditPart) epr)
        .map(re -> re.getLayer(LayerConstants.GRID_LAYER))
        .ifPresent(gf -> gf.setVisible(true));
  }

  /**
   * Does the initialization of the editor.
   *
   * @see org.eclipse.graphiti.ui.editor.DiagramEditor#init(IEditorSite,
   *      IEditorInput)
   */
  @Override
  public void init(IEditorSite site, IEditorInput input)
      throws PartInitException {
    DiagramPackage.eINSTANCE.eClass();
    ServiceUtil
        .getServicesAtLatestVersion(RuminaqEditor.class, EclipseExtension.class)
        .stream().forEach(EclipseExtension::initEditor);
    super.init(site, input);

    Optional<IFile> mf = getModelFile();

    if (mf.isPresent()) {
      this.markerChangeListener = new MarkerChangeListener(mf.get(),
          getEditingDomain(), getDiagramBehavior(),
          getEditorSite().getShell().getDisplay());
    }

    getOperationHistory().ifPresent((IOperationHistory oh) -> oh
        .addOperationHistoryListener((OperationHistoryEvent event) -> {
          switch (event.getEventType()) {
            case OperationHistoryEvent.DONE:
            case OperationHistoryEvent.REDONE:
            case OperationHistoryEvent.UNDONE:
              doSave(new NullProgressMonitor());
              break;
            default:
              break;
          }
        }));

    addMarkerChangeListener();

    ModelUtil.runModelChange(
        () -> getRuminaqDiagram().getChildren().stream().map(UpdateContext::new)
            .filter(ctx -> getDiagramTypeProvider().getFeatureProvider()
                .canUpdate(ctx).toBoolean())
            .forEach(ctx -> getDiagramTypeProvider().getFeatureProvider()
                .updateIfPossible(ctx)),
        getDiagramBehavior().getEditingDomain(),
        Messages.modelChangeInitialization);
  }

  private Optional<IOperationHistory> getOperationHistory() {
    return Optional.ofNullable(getEditingDomain())
        .map(EditingDomain::getCommandStack)
        .filter(IWorkspaceCommandStack.class::isInstance)
        .map(ed -> (IWorkspaceCommandStack) ed)
        .map(IWorkspaceCommandStack::getOperationHistory);
  }

  /**
   * Editor will never be dirty. Editor will save on any change.
   */
  @Override
  public boolean isDirty() {
    return false;
  }

  private void addMarkerChangeListener() {
    getModelFile().map(IFile::getWorkspace)
        .ifPresent(w -> w.addResourceChangeListener(markerChangeListener,
            IResourceChangeEvent.POST_BUILD));
  }

  public RuminaqDiagram getRuminaqDiagram() {
    return (RuminaqDiagram) getDiagramTypeProvider().getDiagram();
  }

  @Override
  protected void setInput(IEditorInput input) {
    super.setInput(input);
    loadMarkers();
  }

  private void loadMarkers() {
    getModelFile().ifPresent((IFile mf) -> {
      try {
        new ValidationStatusLoader().load(getEditingDomain(),
            Arrays.asList(mf.findMarkers(Constants.VALIDATION_MARKER, true,
                IResource.DEPTH_ZERO)));
      } catch (CoreException e) {
        LOGGER.error(Messages.ruminaqEditorLoadMarkersFailed, e);
      }
    });
  }

  private Optional<IFile> getModelFile() {
    return Optional.ofNullable(getDiagramTypeProvider())
        .map(IDiagramTypeProvider::getDiagram).map(Diagram::eResource)
        .map(Resource::getURI).map(URI::trimFragment)
        .map(uri -> uri.toPlatformString(true)).map(uriString -> ResourcesPlugin
            .getWorkspace().getRoot().getFile(new Path(uriString)));
  }

  /**
   * Validate diagram after each change.
   */
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
