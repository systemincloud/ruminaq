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
import org.eclipse.core.resources.IProject;
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
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;
import org.ruminaq.consts.Constants;
import org.ruminaq.eclipse.Messages;
import org.ruminaq.model.FileService;

/**
 * Creates new Ruminaq diagram.
 *
 * @author Marek Jagielski
 */
public class CreateDiagramWizard extends BasicNewResourceWizard {

  public static final String ID = CreateDiagramWizard.class.getCanonicalName();

  protected CreateDiagramWizardNamePage page;

  private IStructuredSelection structuredSelection;

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
  public void init(IWorkbench workbench, IStructuredSelection selection) {
    super.init(workbench, selection);
    this.structuredSelection = selection;
  }

  @Override
  public void addPages() {
    page = new CreateDiagramWizardNamePage(structuredSelection);
    addPage(page);
  }

  @Override
  public boolean performFinish() {
    final String containerName = page.getContainerName();
    final String fileName = page.getFileName();

    IRunnableWithProgress op = new IRunnableWithProgress() {
      @Override
      public void run(IProgressMonitor monitor)
          throws InvocationTargetException {
        try {
          Object o = CreateDiagramWizardNamePage
              .getSelectedObject(structuredSelection);
          IProject project = o == null ? null
              : CreateDiagramWizardNamePage.getProject(o);
          doFinish("/" + project.getName() + "/" + containerName, fileName,
              null);
        } catch (CoreException e) {
          throw new InvocationTargetException(e);
        }
      }
    };
    try {
      getContainer().run(true, false, op);
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
        IWorkbenchPage page = PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getActivePage();
        try {
          IDE.openEditor(page, diagramFile, true);
        } catch (PartInitException e) {
        }
    });
  }
}
