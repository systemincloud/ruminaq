package org.ruminaq.tasks.embeddedtask;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.wizards.IWizardDescriptor;
import org.ruminaq.consts.Constants.SicPlugin;
import org.ruminaq.eclipse.ConstantsUtil;
import org.ruminaq.eclipse.wizards.diagram.CreateDiagramWizard;
import org.ruminaq.eclipse.wizards.diagram.CreateTestDiagramWizard;
import org.ruminaq.eclipse.wizards.project.SourceFolders;
import org.ruminaq.model.ruminaq.EmbeddedTask;
import org.ruminaq.model.util.ModelUtil;
import org.ruminaq.tasks.api.IPropertySection;
import org.ruminaq.tasks.embeddedtask.features.UpdateFeature;
import org.ruminaq.util.EclipseUtil;

public class PropertySection implements IPropertySection {

  public static final String MAIN_PREFIX = "main:";
  public static final String TEST_PREFIX = "test:";

  private Composite root;
  private CLabel lblTaskSelect;
  private Text txtTaskName;
  private Button btnTaskSelect;
  private Button btnNewDiagram;
  private Button btnNewTest;

  private PictogramElement pe;
  private TransactionalEditingDomain ed;
  private IDiagramTypeProvider dtp;

  public PropertySection(Composite parent, PictogramElement pe,
      TransactionalEditingDomain ed, IDiagramTypeProvider dtp) {
    String path = EclipseUtil.getModelPathFromEObject(pe).toString();
    boolean test = ConstantsUtil
        .isTest(EclipseUtil.getModelPathFromEObject(pe));

    this.pe = pe;
    this.ed = ed;
    this.dtp = dtp;

    initLayout(parent, test);
    initComponents(test);
    initActions(pe, ed, dtp, test, path);
    addStyles(test);
  }

  private void initLayout(Composite parent, boolean test) {
    ((GridData) parent.getLayoutData()).verticalAlignment = SWT.FILL;
    ((GridData) parent.getLayoutData()).grabExcessVerticalSpace = true;
    root = new Composite(parent, SWT.NULL);
    root.setLayout(new GridLayout(5, false));

    lblTaskSelect = new CLabel(root, SWT.NONE);
    txtTaskName = new Text(root, SWT.BORDER);
    txtTaskName
        .setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    btnTaskSelect = new Button(root, SWT.NONE);
    btnNewDiagram = new Button(root, SWT.NONE);

    if (test)
      btnNewTest = new Button(root, SWT.NONE);
    else
      new Label(root, SWT.NONE);
  }

  private void initComponents(boolean test) {
    lblTaskSelect.setText("Diagram:");
    btnTaskSelect.setText("Select diagram");
    btnNewDiagram.setText("New Diagram");
    if (test)
      btnNewTest.setText("New Test Diagram");
  }

  private void save() {
    Shell shell = txtTaskName.getShell();
    String tmp = txtTaskName.getText();
    if (tmp.startsWith(MAIN_PREFIX))
      tmp = tmp.replace(MAIN_PREFIX,
          SourceFolders.MAIN_RESOURCES + "/" + SourceFolders.TASK_FOLDER + "/");
    else if (tmp.startsWith(TEST_PREFIX))
      tmp = tmp.replace(TEST_PREFIX,
          SourceFolders.TEST_RESOURCES + "/" + SourceFolders.TASK_FOLDER + "/");

    final String taskPath = tmp;

    boolean parse = new UpdateFeature(dtp.getFeatureProvider()).load(taskPath);
    if (parse) {
      ModelUtil.runModelChange(new Runnable() {
        @Override
        public void run() {
          Object bo = Graphiti.getLinkService()
              .getBusinessObjectForLinkedPictogramElement(pe);
          if (bo == null)
            return;
          if (bo instanceof EmbeddedTask) {
            EmbeddedTask et = (EmbeddedTask) bo;
            et.setImplementationTask(taskPath);
            UpdateContext context = new UpdateContext(pe);
            dtp.getFeatureProvider().updateIfPossible(context);
          }
        }
      }, ed, "Set Java Class");
    } else
      MessageDialog.openError(shell, "Can't edit value",
          "Task not found or incorrect.");
  }

  private void initActions(final PictogramElement pe,
      final TransactionalEditingDomain ed, final IDiagramTypeProvider dtp,
      final boolean test, final String path) {
    txtTaskName.addTraverseListener(new TraverseListener() {
      @Override
      public void keyTraversed(TraverseEvent event) {
        if (event.detail == SWT.TRAVERSE_RETURN)
          save();
      }
    });
    btnTaskSelect.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent evt) {
        Shell shell = txtTaskName.getShell();

        ElementTreeSelectionDialog fileDialog = new ElementTreeSelectionDialog(
            shell, new WorkbenchLabelProvider(),
            new BaseWorkbenchContentProvider());
        if (test)
          fileDialog.setInput(ResourcesPlugin.getWorkspace().getRoot()
              .getProject(
                  EclipseUtil.getProjectNameFromDiagram(dtp.getDiagram()))
              .getFolder("src"));
        else
          fileDialog.setInput(ResourcesPlugin.getWorkspace().getRoot()
              .getProject(
                  EclipseUtil.getProjectNameFromDiagram(dtp.getDiagram()))
              .getFolder(SourceFolders.MAIN_RESOURCES));
        fileDialog.setTitle("Select Diagram File");
        fileDialog.setMessage("Select diagram file from the tree:");
        fileDialog.setAllowMultiple(false);
        fileDialog.addFilter(new ViewerFilter() {
          @Override
          public boolean select(Viewer arg0, Object parent, Object element) {
            if (element instanceof IProject)
              return true;

            if (element instanceof IFolder) {
              IPath dirs = ((IFolder) element).getProjectRelativePath();
              IPath mainPath = new Path(SourceFolders.MAIN_RESOURCES);
              for (int i = 1; i < mainPath.segmentCount(); i++)
                if (dirs.equals(mainPath.uptoSegment(i)))
                  return true;
              if (dirs.matchingFirstSegments(mainPath) == 3)
                return true;
              IPath testPath = new Path(SourceFolders.TEST_RESOURCES);
              for (int i = 1; i < testPath.segmentCount(); i++)
                if (dirs.equals(testPath.uptoSegment(i)))
                  return true;
              if (dirs.matchingFirstSegments(testPath) == 3)
                return true;
              return false;
            }

            if (element instanceof IFile) {
              if (((IFile) element).getFileExtension() == null)
                return false;
              if (((IFile) element).getFullPath().toString().equals(path))
                return false;

              String ext = ((IFile) element).getFileExtension();
              if (ext.equals(CreateDiagramWizard.EXTENSION))
                return true;
            }
            return false;
          }

        });
        fileDialog.setValidator(new ISelectionStatusValidator() {
          @Override
          public IStatus validate(Object[] selection) {
            if (selection.length == 1 && selection[0] instanceof IFile)
              return new Status(IStatus.OK, SicPlugin.GUI_ID.s(), 0, "", null);
            else
              return new Status(IStatus.ERROR, SicPlugin.GUI_ID.s(), 0, "",
                  null);
          }
        });
        fileDialog.open();
        Object[] results = fileDialog.getResult();

        if (results != null) {
          final IPath selectedPath = ((IFile) results[0]).getFullPath()
              .removeFirstSegments(1);
          String taskPath = ((IFile) results[0]).getFullPath()
              .removeFirstSegments(5).toString();
          String show = "";
          if (selectedPath.toString().startsWith(SourceFolders.MAIN_RESOURCES))
            show = MAIN_PREFIX + taskPath;
          if (selectedPath.toString().startsWith(SourceFolders.TEST_RESOURCES))
            show = TEST_PREFIX + taskPath;

          if (taskPath != null)
            txtTaskName.setText(show);

          ModelUtil.runModelChange(new Runnable() {
            @Override
            public void run() {
              String implementationPath = txtTaskName.getText();
              if (implementationPath == null)
                return;

              EObject bo = Graphiti.getLinkService()
                  .getBusinessObjectForLinkedPictogramElement(pe);
              if (bo == null || !(bo instanceof EmbeddedTask))
                return;
              EmbeddedTask et = (EmbeddedTask) bo;

              et.setImplementationTask(selectedPath.toString());
              UpdateContext context = new UpdateContext(pe);
              dtp.getFeatureProvider().updateIfPossible(context);
            }
          }, ed, "Change embedded diagram");
        }
      }
    });
    btnNewDiagram.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent evt) {
        IWizardDescriptor descriptor = PlatformUI.getWorkbench()
            .getNewWizardRegistry().findWizard(CreateDiagramWizard.ID);
        try {
          if (descriptor != null) {
            IWizard wizard = descriptor.createWizard();
            IStructuredSelection selection = new StructuredSelection(JavaCore
                .create(ResourcesPlugin.getWorkspace().getRoot().getProject(
                    EclipseUtil.getProjectNameFromDiagram(dtp.getDiagram()))));
            ((CreateDiagramWizard) wizard).init(PlatformUI.getWorkbench(),
                selection);
            WizardDialog wd = new WizardDialog(
                Display.getDefault().getActiveShell(), wizard);
            wd.setTitle(wizard.getWindowTitle());
            wd.open();
          }
        } catch (CoreException e) {
          e.printStackTrace();
        }
      }
    });
    if (test)
      btnNewTest.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent evt) {
          IWizardDescriptor descriptor = PlatformUI.getWorkbench()
              .getNewWizardRegistry().findWizard(CreateTestDiagramWizard.ID);
          try {
            if (descriptor != null) {
              IWizard wizard = descriptor.createWizard();
              IStructuredSelection selection = new StructuredSelection(
                  JavaCore.create(ResourcesPlugin.getWorkspace().getRoot()
                      .getProject(EclipseUtil
                          .getProjectNameFromDiagram(dtp.getDiagram()))));
              ((CreateTestDiagramWizard) wizard).init(PlatformUI.getWorkbench(),
                  selection);
              WizardDialog wd = new WizardDialog(
                  Display.getDefault().getActiveShell(), wizard);
              wd.setTitle(wizard.getWindowTitle());
              wd.open();
            }
          } catch (CoreException e) {
            e.printStackTrace();
          }
        }
      });
  }

  private void addStyles(boolean test) {
    root.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    lblTaskSelect
        .setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    txtTaskName
        .setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    btnTaskSelect
        .setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    btnNewDiagram
        .setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    if (test)
      btnNewTest
          .setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
  }

  @Override
  public void refresh(PictogramElement pe, TransactionalEditingDomain ed) {
    if (pe != null) {
      Object bo = Graphiti.getLinkService()
          .getBusinessObjectForLinkedPictogramElement(pe);
      if (bo == null)
        return;
      String taskPath = ((EmbeddedTask) bo).getImplementationTask();
      taskPath = taskPath.replaceFirst("/" + SourceFolders.TASK_FOLDER + "/",
          "");
      if (taskPath.startsWith(SourceFolders.MAIN_RESOURCES))
        txtTaskName.setText(
            taskPath.replace(SourceFolders.MAIN_RESOURCES, MAIN_PREFIX));
      else if (taskPath.startsWith(SourceFolders.TEST_RESOURCES))
        txtTaskName.setText(
            taskPath.replace(SourceFolders.TEST_RESOURCES, TEST_PREFIX));
    }
  }
}
