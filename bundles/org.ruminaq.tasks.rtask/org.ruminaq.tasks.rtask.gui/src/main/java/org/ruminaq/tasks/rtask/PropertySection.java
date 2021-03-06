/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.rtask;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.statet.ltk.ast.core.AstNode;
import org.eclipse.statet.ltk.core.LTK;
import org.eclipse.statet.ltk.model.core.ISourceUnitManager;
import org.eclipse.statet.r.core.model.IRLangSourceElement;
import org.eclipse.statet.r.core.model.IRModelInfo;
import org.eclipse.statet.r.core.model.IRModelManager;
import org.eclipse.statet.r.core.model.IRWorkspaceSourceUnit;
import org.eclipse.statet.r.core.model.RElementAccess;
import org.eclipse.statet.r.core.model.RModel;
import org.eclipse.statet.r.core.rsource.ast.Assignment;
import org.eclipse.statet.r.core.rsource.ast.FCall;
import org.eclipse.statet.r.core.rsource.ast.FCall.Arg;
import org.eclipse.statet.r.core.rsource.ast.FCall.Args;
import org.eclipse.statet.r.core.rsource.ast.RAstNode;
import org.eclipse.statet.r.core.rsource.ast.Symbol;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.wizards.IWizardDescriptor;
import org.ruminaq.eclipse.EclipseUtil;
import org.ruminaq.eclipse.RuminaqDiagramUtil;
import org.ruminaq.model.ruminaq.ModelUtil;
import org.ruminaq.tasks.rtask.features.UpdateFeature;
import org.ruminaq.tasks.rtask.model.rtask.RTask;
import org.ruminaq.tasks.rtask.ui.wizards.CreateRTaskListener;
import org.ruminaq.tasks.rtask.ui.wizards.CreateRTaskWizard;

public class PropertySection implements CreateRTaskListener {

  public static final String MAIN_PREFIX = "main:";
  public static final String TEST_PREFIX = "test:";

  private Composite root;
  private CLabel lblClassSelect;
  private Text txtClassName;
  private Button btnClassSelect;
  private Button btnClassNew;

  private PictogramElement pe;
  private TransactionalEditingDomain ed;
  private IDiagramTypeProvider dtp;

  public PropertySection(Composite parent, PictogramElement pe,
      TransactionalEditingDomain ed, IDiagramTypeProvider dtp) {
    String path = EclipseUtil.getUriOf(pe).toString();
    boolean test = RuminaqDiagramUtil
        .isTest(EclipseUtil.getUriOf(pe));

    this.pe = pe;
    this.ed = ed;
    this.dtp = dtp;

    initLayout(parent);
    initComponents();
    initActions(pe, ed, dtp, test, path);
    addStyles();
  }

  private void initLayout(Composite parent) {
    ((GridData) parent.getLayoutData()).verticalAlignment = SWT.FILL;
    ((GridData) parent.getLayoutData()).grabExcessVerticalSpace = true;
    root = new Composite(parent, SWT.NULL);
    root.setLayout(new GridLayout(4, false));

    lblClassSelect = new CLabel(root, SWT.NONE);
    txtClassName = new Text(root, SWT.BORDER);
    txtClassName
        .setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    btnClassSelect = new Button(root, SWT.NONE);
    btnClassNew = new Button(root, SWT.NONE);
  }

  private void initComponents() {
    lblClassSelect.setText("R Task Class:");
    btnClassSelect.setText("Select class");
    btnClassNew.setText("Create");
  }

  private void save() {
    Shell shell = txtClassName.getShell();
    boolean parse = new UpdateFeature(dtp.getFeatureProvider())
        .load(txtClassName.getText());
    if (parse) {
      ModelUtil.runModelChange(new Runnable() {
        @Override
        public void run() {
          Object bo = Graphiti.getLinkService()
              .getBusinessObjectForLinkedPictogramElement(pe);
          if (bo == null)
            return;
          if (bo instanceof RTask) {
            RTask pythonTask = (RTask) bo;
            pythonTask.setImplementation(txtClassName.getText());
            UpdateContext context = new UpdateContext(pe);
            dtp.getFeatureProvider().updateIfPossible(context);
          }
        }
      }, ed, "Set Python Class");
    } else
      MessageDialog.openError(shell, "Can't edit value",
          "Class not found or incorrect.");
  }

  private void initActions(final PictogramElement pe,
      final TransactionalEditingDomain ed, final IDiagramTypeProvider dtp,
      final boolean test, final String path) {
    txtClassName.addTraverseListener(new TraverseListener() {
      @Override
      public void keyTraversed(TraverseEvent event) {
        if (event.detail == SWT.TRAVERSE_RETURN)
          save();
      }
    });
    btnClassSelect.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent evt) {
        Shell shell = txtClassName.getShell();
        final ISourceUnitManager sum = LTK.getSourceUnitManager();

        ElementTreeSelectionDialog fileDialog = new ElementTreeSelectionDialog(
            shell, new WorkbenchLabelProvider(),
            new BaseWorkbenchContentProvider());

        if (test)
          fileDialog.setInput(
              EclipseUtil.getProjectOf(dtp.getDiagram()).getFolder("src"));
        else
          fileDialog.setInput(EclipseUtil.getProjectOf(dtp.getDiagram())
              .getFolder(EclipseExtensionImpl.MAIN_R));

        fileDialog.setTitle("Select R File");
        fileDialog.setMessage("Select R file from the tree:");
        fileDialog.setAllowMultiple(false);
        fileDialog.addFilter(new ViewerFilter() {
          @Override
          public boolean select(Viewer arg0, Object parent, Object element) {
            if (element instanceof IProject)
              return true;

            if (element instanceof IFolder) {
              IPath dirs = ((IFolder) element).getProjectRelativePath();
              IPath mainPath = new Path(EclipseExtensionImpl.MAIN_R);
              for (int i = 1; i < mainPath.segmentCount(); i++)
                if (dirs.equals(mainPath.uptoSegment(i)))
                  return true;
              if (dirs.matchingFirstSegments(mainPath) == 3)
                return true;
              IPath testPath = new Path(EclipseExtensionImpl.TEST_R);
              for (int i = 1; i < testPath.segmentCount(); i++)
                if (dirs.equals(testPath.uptoSegment(i)))
                  return true;
              if (dirs.matchingFirstSegments(testPath) == 3)
                return true;
              return false;
            }

            if (element instanceof IFile) {
              IFile file = (IFile) element;
              if (file.getFileExtension() == null)
                return false;
              if (file.getFullPath().toString().equals(path))
                return false;

              String ext = file.getFileExtension();
              if (ext.equals("R")) {
                IContentDescription contentDescription;
                try {
                  contentDescription = file.getContentDescription();
                  IContentType contentType = contentDescription
                      .getContentType();
                  IRWorkspaceSourceUnit su = (IRWorkspaceSourceUnit) sum
                      .getSourceUnit(LTK.PERSISTENCE_CONTEXT, element,
                          contentType, true, null);
                  IRModelInfo mi = (IRModelInfo) su.getModelInfo(
                      RModel.R_TYPE_ID, IRModelManager.MODEL_FILE,
                      new NullProgressMonitor());
                  IRLangSourceElement se = mi.getSourceElement();

                  AstNode node = se.getAdapter(AstNode.class);
                  for (int i = 0; i < node.getChildCount(); i++) {
                    AstNode el = node.getChild(i);
                    if (el instanceof Assignment) {
                      Assignment a = (Assignment) el;
                      RAstNode right = a.getRightChild();
                      if (right instanceof FCall) {
                        FCall rightCall = (FCall) right;
                        for (Object rightAttachment : rightCall
                            .getAttachments()) {
                          if (rightAttachment instanceof RElementAccess) {
                            if ("R6Class"
                                .equals(((RElementAccess) rightAttachment)
                                    .getSegmentName())) {
                              Args args = rightCall.getArgsChild();
                              for (Arg arg : args.getChildren()) {
                                for (RAstNode ac : arg.getChildren()) {
                                  if (ac instanceof Symbol
                                      && "inherit".equals(ac.getText())) {
                                    RAstNode v = arg.getValueChild();
                                    if (v instanceof Symbol
                                        && "RTask".equals(v.getText()))
                                      return true;
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                } catch (CoreException e) {
                }
              }
            }
            return false;
          }
        });
        fileDialog.setValidator(new ISelectionStatusValidator() {
          @Override
          public IStatus validate(Object[] selection) {
//            if (selection.length == 1 && selection[0] instanceof IFile)
//              return new Status(IStatus.OK, SicPlugin.GUI_ID.s(), 0, "", null);
//            else
//              return new Status(IStatus.ERROR, SicPlugin.GUI_ID.s(), 0, "",
//                  null);
            return null;
          }
        });
        fileDialog.open();
        Object[] results = fileDialog.getResult();

        if (results != null) {
          final IPath selectedPath = ((IFile) results[0]).getFullPath()
              .removeFirstSegments(1);
          String taskPath = ((IFile) results[0]).getFullPath()
              .removeFirstSegments(4).toString();
          String show = "";
          if (selectedPath.toString().startsWith(EclipseExtensionImpl.MAIN_R))
            show = MAIN_PREFIX + taskPath;
          if (selectedPath.toString().startsWith(EclipseExtensionImpl.TEST_R))
            show = TEST_PREFIX + taskPath;

          if (taskPath != null)
            txtClassName.setText(show);

          ModelUtil.runModelChange(new Runnable() {
            @Override
            public void run() {
              String implementationPath = txtClassName.getText();
              if (implementationPath == null)
                return;

              EObject bo = Graphiti.getLinkService()
                  .getBusinessObjectForLinkedPictogramElement(pe);
              if (bo == null || !(bo instanceof RTask))
                return;
              RTask rt = (RTask) bo;

              rt.setImplementation(selectedPath.toString());
              UpdateContext context = new UpdateContext(pe);
              dtp.getFeatureProvider().updateIfPossible(context);
            }
          }, ed, "Change embedded diagram");
        }
      }
    });
    btnClassNew.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent evt) {
        IWizardDescriptor descriptor = PlatformUI.getWorkbench()
            .getNewWizardRegistry().findWizard(CreateRTaskWizard.ID);
        try {
          if (descriptor != null) {
            IWizard wizard = descriptor.createWizard();
            String folder = RuminaqDiagramUtil
                .isTest(EclipseUtil.getUriOf(pe))
                    ? EclipseExtensionImpl.TEST_R
                    : EclipseExtensionImpl.MAIN_R;
            IStructuredSelection selection = new StructuredSelection(
                EclipseUtil.getProjectOf(dtp.getDiagram()).getFolder(folder));
//                    ((CreateRTaskWizard) wizard).init(PlatformUI.getWorkbench(), selection);
            ((CreateRTaskWizard) wizard).setListener(PropertySection.this);
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

  private void addStyles() {
    root.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    lblClassSelect
        .setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    txtClassName
        .setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
  }

  public void refresh(PictogramElement pe, TransactionalEditingDomain ed) {
    if (pe != null) {
      Object bo = Graphiti.getLinkService()
          .getBusinessObjectForLinkedPictogramElement(pe);
      if (bo == null)
        return;
      String className = ((RTask) bo).getImplementation();
      if (className.startsWith(EclipseExtensionImpl.MAIN_R))
        txtClassName.setText(
            className.replace(EclipseExtensionImpl.MAIN_R + "/", MAIN_PREFIX));
      else if (className.startsWith(EclipseExtensionImpl.TEST_R))
        txtClassName.setText(
            className.replace(EclipseExtensionImpl.TEST_R + "/", TEST_PREFIX));
    }
  }

  @Override
  public void created(final String path) {
    final String p = new Path(path).removeFirstSegments(1).toString();
    ModelUtil.runModelChange(new Runnable() {
      @Override
      public void run() {
        Object bo = Graphiti.getLinkService()
            .getBusinessObjectForLinkedPictogramElement(pe);
        if (bo == null)
          return;
        if (bo instanceof RTask) {
          RTask rTask = (RTask) bo;
          rTask.setImplementation(p);
          UpdateContext context = new UpdateContext(pe);
          dtp.getFeatureProvider().updateIfPossible(context);
        }
      }
    }, ed, "Set R Class");
  }

}
