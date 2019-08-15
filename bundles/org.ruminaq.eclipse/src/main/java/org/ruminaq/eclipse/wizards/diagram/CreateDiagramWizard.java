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
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;
import org.ruminaq.consts.Constants;
import org.ruminaq.eclipse.Messages;
import org.ruminaq.eclipse.RuminaqRuntimeException;
import org.ruminaq.model.FileService;

/**
 * Creates new Ruminaq diagram.
 *
 * @author Marek Jagielski
 */
public class CreateDiagramWizard extends BasicNewResourceWizard {

  public static final String ID = CreateDiagramWizard.class.getCanonicalName();

  /**
   * Sets the window title.
   *
   * @see org.eclipse.jface.wizard.Wizard#setWindowTitle()
   */
  @Override
  public void setWindowTitle(final String newTitle) {
    super.setWindowTitle(Messages.createDiagramWizardTitle);
  }

  @Override
  public void addPages() {
    addPage(new CreateDiagramWizardNamePage(selection));
  }

  @Override
  public boolean performFinish() {
    CreateDiagramWizardNamePage page = ((CreateDiagramWizardNamePage) getPage(
        CreateDiagramWizardNamePage.PAGE_NAME));
    final String containerName = page.getContainerName();
    final String fileName = page.getFileName();

    try {
      getContainer().run(true, false, (IProgressMonitor monitor) -> {
          try {
            Optional
                .ofNullable(
                    CreateDiagramWizardNamePage.getSelectedObject(selection))
                .map(o -> CreateDiagramWizardNamePage.getProject(o))
                .ifPresent(p -> {
                  try {
                    doFinish("/" + p.getName() + "/" + containerName,
                        fileName, null);
                  } catch (CoreException e) {
                    throw new RuminaqRuntimeException(e);
                  }
                });
          } catch (RuminaqRuntimeException e) {
            throw new InvocationTargetException(e);
          }
      });
    } catch (InterruptedException e) {
      return false;
    } catch (InvocationTargetException e) {
      Throwable realException = e.getTargetException();
      MessageDialog.openError(getShell(), "Error", realException.getMessage());
      return false;
    }
    return true;
  }

  private void doFinish(String containerName, String fileName,
      IProgressMonitor monitor) throws CoreException {
    // create a sample file
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
        IDE.openEditor(page, diagramFile, true);
      } catch (PartInitException e) {
      }
    });
  }
}
