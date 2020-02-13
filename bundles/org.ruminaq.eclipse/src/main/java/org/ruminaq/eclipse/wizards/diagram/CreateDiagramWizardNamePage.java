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
import org.eclipse.osgi.util.NLS;
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

  private static final int COLUMNS_IN_VIEW = 3;
  private static final int ONE_CELL = 1;
  private static final int TWO_CELLS = 2;

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

  protected static class ShowOnlyProjects extends ViewerFilter {
    @Override
    public boolean select(Viewer arg0, Object parent, Object element) {
      return Selectable.valueOf(element.getClass()) == Selectable.PROJECT;
    }
  }

  protected static class ShowDiagramFolder extends ViewerFilter {

    private String diagramFolder;

    public ShowDiagramFolder(String diagramFolder) {
      this.diagramFolder = diagramFolder;
    }

    @Override
    public boolean select(Viewer arg0, Object parent, Object element) {
      Selectable selectable = Selectable.valueOf(element.getClass());

      if (selectable != Selectable.FOLDER) {
        return false;
      }

      IPath currentPath = ((IFolder) element).getProjectRelativePath();
      IPath diagramPath = new Path(diagramFolder);
      return diagramPath.matchingFirstSegments(currentPath) == currentPath
          .segmentCount()
          || currentPath.matchingFirstSegments(diagramPath) == diagramPath
              .segmentCount();
    }
  }

  /**
   * Create new Diagram page entry constructor.
   *
   * @param selection selected item in Project Explorer
   */
  public CreateDiagramWizardNamePage(IStructuredSelection selection) {
    super(PAGE_NAME);
    this.selection = selection;
    super.setTitle(Messages.createDiagramWizardTitle);
    super.setDescription(Messages.createDiagramWizardDescription);
  }

  /**
   * Create UI controls.
   *
   * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
   */
  @Override
  public void createControl(Composite parent) {
    setImageDescriptor(ImageUtil.getImageDescriptor(Image.RUMINAQ_LOGO_64X64));

    Object selected = getSelectedObject(selection).orElse(null);

    initLayout(parent);
    initComponents(selected);
    initActions();

    dialogChanged();
    setControl(composite);
  }

  static Optional<Object> getSelectedObject(ISelection selection) {
    return Optional.ofNullable(selection)
        .filter(Predicate.not(ISelection::isEmpty))
        .filter(s -> s instanceof IStructuredSelection)
        .map(s -> (IStructuredSelection) selection).filter(ss -> ss.size() == 1)
        .map(IStructuredSelection::getFirstElement);
  }

  /**
   * Layout of wizard.
   *
   * @param parent outer composite
   */
  private void initLayout(Composite parent) {
    composite = new Composite(parent, SWT.NULL);
    composite.setLayout(new GridLayout(COLUMNS_IN_VIEW, false));

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
        GridData.VERTICAL_ALIGN_BEGINNING, true, false, TWO_CELLS, ONE_CELL));
  }

  /**
   * Set default values.
   *
   * @param selectedObject object selected in Project Explorer
   */
  private void initComponents(Object selectedObject) {
    lblProject.setText(Messages.createDiagramWizardProject);
    btnProject.setText(Messages.createDiagramWizardProjectBrowse);
    lblContainer.setText(Messages.createDiagramWizardContainer);
    btnContainer.setText(Messages.createDiagramWizardContainerBrowse);
    lblFile.setText(Messages.createDiagramWizardFilename);

    Optional<IProject> project = Optional.ofNullable(selectedObject)
        .map(EclipseUtil::getProjectFromSelection);
    String projectName = project.map(IProject::getName).orElse("");

    txtProject.setText(projectName);

    String diagramBase = getDiagramFolder();

    String path = null;
    if (selectedObject != null) {
      Selectable selectable = Selectable.valueOf(selectedObject.getClass());
      if (selectable == Selectable.PACKAGEFRAGMENT) {
        path = ((PackageFragment) selectedObject).getPath().toString();
      } else if (selectable == Selectable.FOLDER) {
        path = ((Folder) selectedObject).getFullPath().toString();
      } else {
        path = "";
      }
    } else {
      path = "";
    }

    String dirPath = null;
    if (path.startsWith(format("/{0}/{1}", projectName, diagramBase))) {
      dirPath = path.substring(projectName.length() + "./".length());
    } else {
      dirPath = diagramBase;
    }

    txtContainer.setText(dirPath);

    String filename = getDefaultName();
    int i = 1;
    while (project.isPresent() && Optional.ofNullable(findFile(
        (IContainer) project.get().findMember(getContainerName()), filename))
        .isPresent()) {
      filename = getDefaultName(Integer.toString(i));
      i++;
    }
    txtFile.setText(filename);
  }

  protected String getResourceFolder() {
    return SourceFolders.MAIN_RESOURCES;
  }

  protected String getDiagramFolder() {
    return SourceFolders.DIAGRAM_FOLDER;
  }

  protected String getDefaultName() {
    return getDefaultName("");
  }

  protected String getDefaultName(String suffix) {
    return DEFAULT_DIAGRAM_NAME + suffix
        + CreateDiagramWizard.DIAGRAM_EXTENSION_DOT;
  }

  /**
   * Set listeners.
   *
   */
  private void initActions() {
    txtProject.addModifyListener(e -> dialogChanged());
    btnProject.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        handleBrowseProject();
      }
    });
    txtContainer.addModifyListener(e -> dialogChanged());
    btnContainer.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        handleBrowse(ResourcesPlugin.getWorkspace().getRoot()
            .getProject(txtProject.getText()));
      }
    });
    txtFile.addModifyListener(e -> dialogChanged());
  }

  /**
   * Choose project in workspace.
   *
   */
  private void handleBrowseProject() {
    ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
        getShell(), new WorkbenchLabelProvider(),
        new BaseWorkbenchContentProvider());
    dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
    dialog.setTitle(Messages.createDiagramWizardProjectChoose);
    dialog.setAllowMultiple(false);
    dialog.addFilter(new ShowOnlyProjects());
    dialog.open();
    Object[] results = dialog.getResult();

    if (results != null) {
      txtProject.setText(((IProject) results[0]).getName());
    }
  }

  /**
   * Choose folder.
   *
   */
  private void handleBrowse(IProject project) {
    ElementTreeSelectionDialog fileDialog = new ElementTreeSelectionDialog(
        getShell(), new WorkbenchLabelProvider(),
        new BaseWorkbenchContentProvider());
    fileDialog.setInput(project.getFolder(getResourceFolder()));
    fileDialog.setTitle(Messages.createDiagramWizardContainerChoose);
    fileDialog.setAllowMultiple(false);
    fileDialog.addFilter(new ShowDiagramFolder(getDiagramFolder()));
    fileDialog.open();
    Object[] results = fileDialog.getResult();

    if (results != null) {
      final IPath selectedPath = ((IFolder) results[0]).getFullPath()
          .removeFirstSegments(1);
      txtContainer.setText(selectedPath.toString());
    }
  }

  /**
   * Validate fields.
   *
   */
  private void dialogChanged() {
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
      updateStatus(Messages.createDiagramWizardStatusFileNotValid);
    } else if (!getFileName().endsWith("." + getExtension())) {
      updateStatus(
          NLS.bind(Messages.createDiagramWizardStatusFileExtensionNotValid,
              getExtension()));
    } else if (Optional
        .ofNullable(findFile((IContainer) container, getFileName()))
        .isPresent()) {
      updateStatus(Messages.createDiagramWizardStatusFileExists);
    } else {
      updateStatus(null);
    }
  }

  protected IResource findFile(IContainer container, String fileName) {
    return container.findMember(fileName);
  }

  protected String getExtension() {
    return CreateDiagramWizard.EXTENSION;
  }

  private void updateStatus(String message) {
    setErrorMessage(message);
    setPageComplete(message == null);
  }

  /**
   * Getter for project.
   *
   */
  public String getProjectName() {
    return txtProject.getText();
  }

  /**
   * Getter for directory.
   *
   */
  public String getContainerName() {
    return txtContainer.getText();
  }

  /**
   * Getter for diagram filename.
   *
   */
  public String getFileName() {
    return txtFile.getText();
  }
}
