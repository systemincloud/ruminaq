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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;
import org.ruminaq.eclipse.Messages;
import org.ruminaq.eclipse.prefs.ProjectProps;
import org.ruminaq.eclipse.wizards.task.CreateUserDefinedTaskListener;
import org.ruminaq.gui.model.FileService;
import org.ruminaq.gui.model.diagram.DiagramFactory;
import org.ruminaq.gui.model.diagram.RuminaqDiagram;
import org.ruminaq.logs.ModelerLoggerFactory;
import org.ruminaq.model.ruminaq.MainTask;
import org.ruminaq.model.ruminaq.RuminaqFactory;
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

  private CreateUserDefinedTaskListener listener;

  public void setListener(CreateUserDefinedTaskListener listener) {
    this.listener = listener;
  }

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

    IFolder diagramFolder = container.getFolder(null);
    IFile diagramFile = diagramFolder.getFile(fileName);
    MainTask model = RuminaqFactory.eINSTANCE.createMainTask();
    model.setVersion(ProjectProps.getInstance(resource.getProject())
        .get(ProjectProps.RUMINAQ_VERSION));
    diagram.setMainTask(model);
    URI uri = URI
        .createPlatformResourceURI(diagramFile.getFullPath().toString(), true);
    FileService.createEmfFileForDiagram(uri, diagram, model);

    getShell().getDisplay().asyncExec(() -> {
      Optional.ofNullable(listener).ifPresent(l -> l.setImplementation(
          diagramFile.getFullPath().removeFirstSegments(1).toString()));
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

  /**
   * Open diagram file internal method.
   */
  private static void openEditor(IWorkbenchPage page, IFile input)
      throws PartInitException {
    IDE.openEditor(page, input, true);
  }
}
