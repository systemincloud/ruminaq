/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.wizards.diagram;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.graphiti.internal.IDiagramVersion;
import org.eclipse.graphiti.internal.util.LookManager;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.pictograms.PictogramLink;
import org.eclipse.graphiti.mm.pictograms.PictogramsFactory;
import org.eclipse.graphiti.mm.pictograms.PictogramsPackage;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.util.ILook;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;
import org.ruminaq.eclipse.Messages;
import org.ruminaq.gui.model.diagram.DiagramFactory;
import org.ruminaq.gui.model.diagram.RuminaqDiagram;
import org.ruminaq.logs.ModelerLoggerFactory;
import org.ruminaq.model.FileService;
import org.ruminaq.model.ruminaq.MainTask;
import org.ruminaq.model.ruminaq.RuminaqFactory;
import org.ruminaq.prefs.ProjectProps;
import org.slf4j.Logger;

/**
 * Creates new Ruminaq diagram.
 *
 * @author Marek Jagielski
 */
public class CreateDiagramWizard extends BasicNewResourceWizard {

  private static final Logger LOGGER = ModelerLoggerFactory
      .getLogger(CreateDiagramWizard.class);

  public static final String ID = CreateDiagramWizard.class.getCanonicalName();

  public static final String EXTENSION = "rumi";
  public static final String DIAGRAM_EXTENSION_DOT = "." + EXTENSION;

  /**
   * Set window name.
   */
  @Override
  public void init(IWorkbench workbench,
      IStructuredSelection currentSelection) {
    super.init(workbench, currentSelection);
    setWindowTitle(Messages.createDiagramWizardTitle);
  }

  /**
   * Add main page to wizard.
   */
  @Override
  public void addPages() {
    addPage(new CreateDiagramWizardNamePage(selection));
  }

  /**
   * Perform finish processing for wizard.
   */
  @Override
  public boolean performFinish() {
    CreateDiagramWizardNamePage page = (CreateDiagramWizardNamePage) getPage(
        CreateDiagramWizardNamePage.PAGE_NAME);
    final String projectName = page.getProjectName();
    final String containerName = page.getContainerName();
    final String fileName = page.getFileName();

    try {
      getContainer().run(true, false, (IProgressMonitor monitor) -> doFinish(
          "/" + projectName + "/" + containerName, fileName));
    } catch (InterruptedException e) {
      LOGGER.error(Messages.createDiagramWizardFailed, e);
      MessageDialog.openError(getShell(), Messages.ruminaqFailed,
          e.getMessage());
      Thread.currentThread().interrupt();
      return false;
    } catch (InvocationTargetException e) {
      LOGGER.error(Messages.createDiagramWizardFailed, e);
      MessageDialog.openError(getShell(), Messages.ruminaqFailed,
          e.getTargetException().getMessage());
      return false;
    }
    return true;
  }

  private void doFinish(String containerName, String fileName) {
    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    IResource resource = root.findMember(new Path(containerName));

    IContainer container = (IContainer) resource;
    RuminaqDiagram diagram = DiagramFactory.eINSTANCE.createRuminaqDiagram();
    diagram.eSet(PictogramsPackage.eINSTANCE.getDiagram_Version(),
        IDiagramVersion.CURRENT);
    final ILook look = LookManager.getLook();
    IGaService gaService = Graphiti.getGaService();
    Rectangle rectangle = gaService.createRectangle(diagram);
    rectangle.setForeground(
        gaService.manageColor(diagram, look.getMinorGridLineColor()));
    rectangle.setBackground(
        gaService.manageColor(diagram, look.getGridBackgroundColor()));
    gaService.setSize(rectangle, 1000, 1000);

    IFolder diagramFolder = container.getFolder(null);
    final IFile diagramFile = diagramFolder.getFile(fileName);
    MainTask model = RuminaqFactory.eINSTANCE.createMainTask();
    model.setVersion(ProjectProps.getInstance(resource.getProject())
        .get(ProjectProps.RUMINAQ_VERSION));
    PictogramLink link = PictogramsFactory.eINSTANCE.createPictogramLink();
    link.setPictogramElement(diagram);
    URI uri = URI
        .createPlatformResourceURI(diagramFile.getFullPath().toString(), true);
    FileService.createEmfFileForDiagram(uri, diagram, model);

    getShell().getDisplay().asyncExec(() -> {
      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
          .getActivePage();
      try {
        openEditor(page, diagramFile);
      } catch (PartInitException e) {
        LOGGER.error(Messages.openDiagramFailed, e);
        MessageDialog.openError(getShell(), Messages.ruminaqFailed,
            Messages.openDiagramFailed);
      }
    });
  }

  private static void openEditor(IWorkbenchPage page, IFile input)
      throws PartInitException {
    IDE.openEditor(page, input, true);
  }
}
