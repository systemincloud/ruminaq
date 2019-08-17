/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.wizards.diagram;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;
import org.ruminaq.consts.Constants;
import org.ruminaq.eclipse.Messages;
import org.ruminaq.eclipse.RuminaqRuntimeException;
import org.ruminaq.logs.ModelerLoggerFactory;
import org.ruminaq.model.FileService;
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

  /**
   * Set window name.
   */
  @Override
  public void init(IWorkbench workbench,
      IStructuredSelection currentSelection) {
    super.init(workbench, currentSelection);
    setWindowTitle(Messages.createDiagramWizardTitle);
  }

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
    final String containerName = page.getContainerName();
    final String fileName = page.getFileName();

    try {
      getContainer().run(true, false, (IProgressMonitor monitor) -> {
        try {
          Optional
              .ofNullable(
                  CreateDiagramWizardNamePage.getSelectedObject(selection))
              .map(CreateDiagramWizardNamePage::getProject)
              .ifPresent((IProject p) -> doFinish(
                  "/" + p.getName() + "/" + containerName, fileName));
        } catch (RuminaqRuntimeException e) {
          throw new InvocationTargetException(e);
        }
      });
    } catch (InterruptedException e) {
      LOGGER.error(Messages.createDiagramWizardFailed, e);
      MessageDialog.openError(getShell(), "Error", e.getMessage());
      Thread.currentThread().interrupt();
      return false;
    } catch (InvocationTargetException e) {
      LOGGER.error(Messages.createDiagramWizardFailed, e);
      MessageDialog.openError(getShell(), "Error",
          e.getTargetException().getMessage());
      return false;
    }
    return true;
  }

  private void doFinish(String containerName, String fileName) {
    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    IResource resource = root.findMember(new Path(containerName));

    IContainer container = (IContainer) resource;
    String name = fileName.substring(0,
        fileName.lastIndexOf(Constants.DIAGRAM_EXTENSION_DOT));

    Diagram diagram = Graphiti.getPeCreateService().createDiagram("Ruminaq",
        name, -1, false);
    IFolder diagramFolder = container.getFolder(null);
    final IFile diagramFile = diagramFolder.getFile(fileName);
    URI uri = URI
        .createPlatformResourceURI(diagramFile.getFullPath().toString(), true);
    FileService.createEmfFileForDiagram(uri, diagram);

    getShell().getDisplay().asyncExec(() -> {
      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
          .getActivePage();
      try {
        openEditor(page, diagramFile, true);
      } catch (PartInitException e) {
        throw new RuminaqRuntimeException(e.getMessage(), e);
      }
    });
  }

  private void openEditor(IWorkbenchPage page, IFile input,
      boolean activate) throws PartInitException {
    IDE.openEditor(page, input, true);
  }
}
