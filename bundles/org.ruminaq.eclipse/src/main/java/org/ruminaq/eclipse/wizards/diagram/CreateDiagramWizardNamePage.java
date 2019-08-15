/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.wizards.diagram;

import org.eclipse.core.internal.resources.Folder;
import org.eclipse.core.internal.resources.Project;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.core.PackageFragment;
import org.eclipse.jdt.internal.core.PackageFragmentRoot;
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
import org.ruminaq.util.ImageUtil;

/**
 *
 * @author Marek Jagielski
 */
@SuppressWarnings("restriction")
public class CreateDiagramWizardNamePage extends WizardPage {

  public static final String PAGE_NAME = "createDefaultRuminaqDiagramNameWizardPage";
  public static final String DEFAULT_DIAGRAM_NAME = "MyTask";

  private Composite composite;
  private CLabel lblContainer;
  private Text txtContainer;
  private Button btnContainer;
  private CLabel lblFile;
  private Text txtFile;

  private ISelection selection;

  public CreateDiagramWizardNamePage(IStructuredSelection selection) {
    super(PAGE_NAME);
    this.selection = selection;
  }

  /**
   * Sets the window title.
   *
   * @see org.eclipse.jface.wizard.WizardPage#setTitle()
   */
  @Override
  public void setTitle(String title) {
    super.setTitle(Messages.createDiagramWizardTitle);
  }

  @Override
  public void createControl(Composite parent) {
    setDescription(Messages.createDiagramWizardDescription);
    setImageDescriptor(ImageUtil.getImageDescriptor(Image.RUMINAQ_LOGO_64X64));

    Object o = getSelectedObject(selection);
    IProject project = o == null ? null : getProject(o);

    if (project == null) {
      return;
    }

    initLayout(parent);
    initComponents(project.getName(), o);
    initActions(project);

    dialogChanged(project);
    setControl(composite);
  }

  static Object getSelectedObject(ISelection selection) {
    if (selection != null && selection.isEmpty() == false
        && selection instanceof IStructuredSelection) {
      IStructuredSelection ssel = (IStructuredSelection) selection;
      if (ssel.size() > 1)
        return null;
      return ssel.getFirstElement();
    }
    return null;
  }

  static IProject getProject(Object obj) {
    String projectName = "";
    if (obj instanceof Project)
      projectName = ((Project) obj).getName();
    else if (obj instanceof IJavaProject)
      projectName = ((IJavaProject) obj).getElementName();
    else if (obj instanceof IResource)
      projectName = ((IResource) obj).getProject().getName();
    else if (obj instanceof PackageFragment)
      projectName = ((PackageFragment) obj).getJavaProject().getElementName();
    else if (obj instanceof PackageFragmentRoot)
      projectName = ((PackageFragmentRoot) obj).getJavaProject()
          .getElementName();
    return ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
  }

  private void initLayout(Composite parent) {
    composite = new Composite(parent, SWT.NULL);
    composite.setLayout(new GridLayout(3, false));

    lblContainer = new CLabel(composite, SWT.NULL);
    txtContainer = new Text(composite, SWT.BORDER | SWT.SINGLE);
    txtContainer.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    btnContainer = new Button(composite, SWT.PUSH);

    lblFile = new CLabel(composite, SWT.NULL);
    txtFile = new Text(composite, SWT.BORDER | SWT.SINGLE);
    txtFile.setLayoutData(new GridData(GridData.FILL,
        GridData.VERTICAL_ALIGN_BEGINNING, true, false, 2, 1));
  }

  private void initComponents(String projectName, Object obj) {
    lblContainer.setText("&Container:");
    btnContainer.setText("Browse...");
    lblFile.setText("&File name:");

    String diagramBase = getDiagramFolder();

    String dirPath = null;
    if (obj instanceof PackageFragment && ((PackageFragment) obj).getPath()
        .toString().startsWith("/" + projectName + "/" + diagramBase))
      dirPath = ((PackageFragment) obj).getPath().toString()
          .substring(projectName.length() + 2);
    else if (obj instanceof Folder && ((Folder) obj).getFullPath().toString()
        .startsWith("/" + projectName + "/" + diagramBase))
      dirPath = ((Folder) obj).getFullPath().toString()
          .substring(projectName.length() + 2);
    else
      dirPath = diagramBase;

    txtContainer.setText(dirPath);

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

  private void initActions(final IProject project) {
    txtContainer.addModifyListener(e -> dialogChanged(project));
    btnContainer.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        handleBrowse(project);
      }
    });
    txtFile.addModifyListener(e -> dialogChanged(project));
  }

  private void handleBrowse(IProject project) {
    ElementTreeSelectionDialog fileDialog = new ElementTreeSelectionDialog(
        getShell(), new WorkbenchLabelProvider(),
        new BaseWorkbenchContentProvider());
    fileDialog.setInput(project.getFolder(getResourceFolder()));
    fileDialog.setTitle("Select directory");
    fileDialog.setAllowMultiple(false);
    fileDialog.addFilter(new ViewerFilter() {
      @Override
      public boolean select(Viewer arg0, Object parent, Object element) {
        if (element instanceof IProject) {
          return true;
        }

        if (element instanceof IFolder) {
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
          if (dirs.matchingFirstSegments(testPath) == 3) {
            return true;
          }
          return false;
        }

        return false;
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

  private void dialogChanged(IProject project) {
    if (getContainerName().length() == 0) {
      updateStatus("File container must be specified");
      return;
    }

    IResource container = ResourcesPlugin.getWorkspace().getRoot().findMember(
        new Path("/" + project.getName() + "/" + getContainerName()));

    if (container == null || (container.getType()
        & (IResource.PROJECT | IResource.FOLDER)) == 0) {
      updateStatus("File container must exist");
    } else if (!container.isAccessible()) {
      updateStatus("Project must be writable");
    } else if (getFileName().length() == 0) {
      updateStatus("File name must be specified");
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

  public String getContainerName() {
    return txtContainer.getText();
  }

  public String getFileName() {
    return txtFile.getText();
  }
}
