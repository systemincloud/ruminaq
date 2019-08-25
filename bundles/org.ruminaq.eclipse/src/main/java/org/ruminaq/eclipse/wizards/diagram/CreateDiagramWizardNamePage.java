/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.wizards.diagram;

import static java.text.MessageFormat.format;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.eclipse.core.internal.resources.Folder;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.internal.core.PackageFragment;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.ruminaq.consts.Constants;
import org.ruminaq.eclipse.Image;
import org.ruminaq.eclipse.Messages;
import org.ruminaq.eclipse.wizards.project.SourceFolders;
import org.ruminaq.util.EclipseUtil;
import org.ruminaq.util.ImageUtil;

/**
 * Create new Diagram page.
 *
 * @author Marek Jagielski
 */
public class CreateDiagramWizardNamePage extends WizardPage {

  public static final String PAGE_NAME = "createDefaultRuminaqDiagramNameWizardPage";
  public static final String DEFAULT_DIAGRAM_NAME = "MyTask";

  private Composite composite;
  private CLabel lblProject;
  private Text txtProject;
  private Button btnProject;
  private CLabel lblContainer;
  private Text txtContainer;
  private Button btnContainer;
  private CLabel lblFile;
  private Text txtFile;

  private ISelection selection;

  public CreateDiagramWizardNamePage(IStructuredSelection selection) {
    super(PAGE_NAME);
    this.selection = selection;
    super.setTitle(Messages.createDiagramWizardTitle);
    super.setDescription(Messages.createDiagramWizardDescription);
  }

  @Override
  public void createControl(Composite parent) {
    setImageDescriptor(ImageUtil.getImageDescriptor(Image.RUMINAQ_LOGO_64X64));

    Object selected = getSelectedObject(selection);

    initLayout(parent);
    initComponents(selected);
    initActions(selected);

    dialogChanged(selected);
    setControl(composite);
  }

  static Object getSelectedObject(ISelection selection) {
    return Optional.ofNullable(selection)
        .filter(Predicate.not(ISelection::isEmpty))
        .filter(s -> s instanceof IStructuredSelection)
        .map(s -> (IStructuredSelection) selection).filter(ss -> ss.size() == 1)
        .map(IStructuredSelection::getFirstElement).orElse(null);
  }


  private void initLayout(Composite parent) {
    composite = new Composite(parent, SWT.NULL);
    composite.setLayout(new GridLayout(3, false));

    lblProject = new CLabel(composite, SWT.NULL);
    txtProject = new Text(composite, SWT.BORDER | SWT.SINGLE);
    txtProject.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    btnProject = new Button(composite, SWT.PUSH);

    lblContainer = new CLabel(composite, SWT.NULL);
    txtContainer = new Text(composite, SWT.BORDER | SWT.SINGLE);
    txtContainer.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    btnContainer = new Button(composite, SWT.PUSH);

    lblFile = new CLabel(composite, SWT.NULL);
    txtFile = new Text(composite, SWT.BORDER | SWT.SINGLE);
    txtFile.setLayoutData(new GridData(GridData.FILL,
        GridData.VERTICAL_ALIGN_BEGINNING, true, false, 2, 1));
  }

  private void initComponents(Object selectedObject) {
    lblProject.setText(Messages.createDiagramWizardProject);
    btnProject.setText(Messages.createDiagramWizardProjectBrowse);
    lblContainer.setText(Messages.createDiagramWizardContainer);
    btnContainer.setText(Messages.createDiagramWizardContainerBrowse);
    lblFile.setText(Messages.createDiagramWizardFilename);

    Optional<IProject> project = EclipseUtil.getProjectFromSelection(selectedObject);

    txtProject.setText(project.map(IProject::getName).orElse(""));

    String diagramBase = getDiagramFolder();

    txtContainer.setText(project.map(p -> {
      String dirPath;
      if (selectedObject instanceof PackageFragment
          && ((PackageFragment) selectedObject).getPath().toString()
              .startsWith(format("/{0}/{1}", p.getName(), diagramBase))) {
        dirPath = ((PackageFragment) selectedObject).getPath().toString()
            .substring(p.getName().length() + 2);
      } else if (selectedObject instanceof Folder
          && ((Folder) selectedObject).getFullPath().toString()
              .startsWith(format("/{0}/{1}", p.getName(), diagramBase))) {
        dirPath = ((Folder) selectedObject).getFullPath().toString()
            .substring(p.getName().length() + 2);
      } else {
        dirPath = diagramBase;
      }
      return dirPath;
    }).orElse(""));

    String filename = getDefaultName();
    txtFile.setText(filename);
  }

  protected String getResourceFolder() {
    return SourceFolders.MAIN_RESOURCES;
  }

  protected String getDiagramFolder() {
    return SourceFolders.DIAGRAM_FOLDER;
  }

  protected String getDefaultName() {
    return DEFAULT_DIAGRAM_NAME + Constants.DIAGRAM_EXTENSION_DOT;
  }

  private void initActions(Object selectedObject) {
    txtProject.addModifyListener(e -> dialogChanged(selectedObject));
    btnProject.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        handleBrowseProject();
      }
    });
    txtContainer.addModifyListener(e -> dialogChanged(selectedObject));
    btnContainer.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        handleBrowse(ResourcesPlugin.getWorkspace().getRoot()
            .getProject(txtProject.getText()));
      }
    });
    txtFile.addModifyListener(e -> dialogChanged(selectedObject));
  }

  private void handleBrowseProject() {
    ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
        getShell(), new WorkbenchLabelProvider(),
        new BaseWorkbenchContentProvider());
    dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
    dialog.setTitle(Messages.createDiagramWizardProjectChoose);
    dialog.setAllowMultiple(false);
    dialog.addFilter(new ViewerFilter() {
      @Override
      public boolean select(Viewer arg0, Object parent, Object element) {
        return element instanceof IProject;
      }
    });
    dialog.open();
    Object[] results = dialog.getResult();

    if (results != null) {
      txtProject.setText(((IProject) results[0]).getName());
    }
  }

  private void handleBrowse(IProject project) {
    ElementTreeSelectionDialog fileDialog = new ElementTreeSelectionDialog(
        getShell(), new WorkbenchLabelProvider(),
        new BaseWorkbenchContentProvider());
    fileDialog.setInput(project.getFolder(getResourceFolder()));
    fileDialog.setTitle(Messages.createDiagramWizardContainerChoose);
    fileDialog.setAllowMultiple(false);
    fileDialog.addFilter(new ViewerFilter() {
      @Override
      public boolean select(Viewer arg0, Object parent, Object element) {
        if (element instanceof IProject) {
          return true;
        } else if (element instanceof IFolder) {
          IPath dirs = ((IFolder) element).getProjectRelativePath();
          IPath mainPath = new Path(SourceFolders.MAIN_RESOURCES);
          for (int i = 1; i < mainPath.segmentCount(); i++) {
            if (dirs.equals(mainPath.uptoSegment(i))) {
              return true;
            }
          }
          if (dirs.matchingFirstSegments(mainPath) == 3) {
            return true;
          }
          IPath testPath = new Path(SourceFolders.TEST_RESOURCES);
          for (int i = 1; i < testPath.segmentCount(); i++) {
            if (dirs.equals(testPath.uptoSegment(i))) {
              return true;
            }
          }
          return dirs.matchingFirstSegments(testPath) == 3;
        } else {
          return false;
        }
      }
    });
    fileDialog.open();
    Object[] results = fileDialog.getResult();

    if (results != null) {
      final IPath selectedPath = ((IFolder) results[0]).getFullPath()
          .removeFirstSegments(1);
      txtContainer.setText(selectedPath.toString());
    }
  }

  private void dialogChanged(Object selectedObject) {
    Optional<IProject> project = Stream
        .of(ResourcesPlugin.getWorkspace().getRoot().getProjects())
        .filter(p -> txtProject.getText().equals(p.getName())).findFirst();

    if (!project.isPresent()) {
      updateStatus(Messages.createDiagramWizardStatusProjectNotSpecified);
      return;
    }

    if (getContainerName().length() == 0) {
      updateStatus(Messages.createDiagramWizardStatusContainerNotSpecified);
      return;
    }

    IResource container = project.get().findMember(getContainerName());

    if (container == null || (container.getType()
        & (IResource.PROJECT | IResource.FOLDER)) == 0) {
      updateStatus(Messages.createDiagramWizardStatusContainerNotExists);
    } else if (!container.isAccessible()) {
      updateStatus(Messages.createDiagramWizardStatusContainerNotWritale);
    } else if (getFileName().length() == 0) {
      updateStatus(Messages.createDiagramWizardStatusFileNotSpecified);
    } else if (getFileName().replace('\\', '/').indexOf('/', 1) > 0) {
      updateStatus("File name must be valid");
    } else if (!getFileName().endsWith("." + getExtension())) {
      updateStatus("File extension must be \"" + getExtension() + "\"");
    } else if (((IContainer) container).findMember(getFileName()) != null) {
      updateStatus("File \"" + getFileName() + "\"already exists");
    } else {
      updateStatus(null);
    }
  }

  protected String getExtension() {
    return Constants.EXTENSION;
  }

  private void updateStatus(String message) {
    setErrorMessage(message);
    setPageComplete(message == null);
  }

  public String getProjectName() {
    return txtProject.getText();
  }

  public String getContainerName() {
    return txtContainer.getText();
  }

  public String getFileName() {
    return txtFile.getText();
  }
}
