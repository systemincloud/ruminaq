package org.ruminaq.tasks.pythontask.gui;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.wizards.IWizardDescriptor;
import org.python.pydev.shared_ui.EditorUtils;
import org.ruminaq.consts.Constants;
import org.ruminaq.eclipse.RuminaqDiagramUtil;
import org.ruminaq.model.ruminaq.ModelUtil;
import org.ruminaq.tasks.api.IPropertySection;
import org.ruminaq.tasks.pythontask.gui.util.FindPythonTask;
import org.ruminaq.tasks.pythontask.gui.util.SicGlobalsTwoPanelElementSelector2;
import org.ruminaq.tasks.pythontask.model.pythontask.PythonTask;
import org.ruminaq.tasks.pythontask.ui.wizards.CreatePythonTaskListener;
import org.ruminaq.tasks.pythontask.ui.wizards.CreatePythonTaskWizard;
import org.ruminaq.util.EclipseUtil;

import com.python.pydev.analysis.additionalinfo.AdditionalInfoAndIInfo;
import org.python.pydev.core.IInfo;

public class PropertySection
    implements IPropertySection, CreatePythonTaskListener {

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
    this.pe = pe;
    this.ed = ed;
    this.dtp = dtp;

    initLayout(parent);
    initComponents();
    initActions(pe, ed, dtp);
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
    lblClassSelect.setText("Python Task Class:");
    btnClassSelect.setText("Select class");
    btnClassNew.setText("Create");
  }

  private void save() {
    Shell shell = txtClassName.getShell();
    boolean parse = new UpdateFeature(dtp.getFeatureProvider())
        .load(txtClassName.getText());
    if (parse) {
      ModelUtil.runModelChange(new Runnable() {
        public void run() {
          Object bo = Graphiti.getLinkService()
              .getBusinessObjectForLinkedPictogramElement(pe);
          if (bo == null)
            return;
          if (bo instanceof PythonTask) {
            PythonTask pythonTask = (PythonTask) bo;
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
      final TransactionalEditingDomain ed, final IDiagramTypeProvider dtp) {
    txtClassName.addTraverseListener(new TraverseListener() {
      @Override
      public void keyTraversed(TraverseEvent event) {
        if (event.detail == SWT.TRAVERSE_RETURN)
          save();
      }
    });
    btnClassSelect.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent evt) {
        IProject p = ResourcesPlugin.getWorkspace().getRoot()
            .getProject(EclipseUtil.getProjectNameFromPe(pe));
        SicGlobalsTwoPanelElementSelector2 dialog = new SicGlobalsTwoPanelElementSelector2(
            EditorUtils.getShell(), true, "", p);
        dialog.setElements(FindPythonTask.INSTANCE.getInfos(p));

        dialog.open();
        Object[] result = dialog.getResult();
        if (result != null && result.length > 0) {
          for (Object obj : result) {
            IInfo entry;
            if (obj instanceof AdditionalInfoAndIInfo) {
              AdditionalInfoAndIInfo additional = (AdditionalInfoAndIInfo) obj;
              entry = additional.info;
            } else
              entry = (IInfo) obj;
            txtClassName.setText(entry.getDeclaringModuleName());

            ModelUtil.runModelChange(new Runnable() {
              public void run() {
                Object bo = Graphiti.getLinkService()
                    .getBusinessObjectForLinkedPictogramElement(pe);
                if (bo == null)
                  return;
                String implementationName = txtClassName.getText();
                if (implementationName != null) {
                  if (bo instanceof PythonTask) {
                    PythonTask pythonTask = (PythonTask) bo;
                    pythonTask.setImplementation(implementationName);
                    UpdateContext context = new UpdateContext(pe);
                    dtp.getFeatureProvider().updateIfPossible(context);
                  }
                }
              }
            }, ed, "Set Python Class");
          }
        }
      }
    });
    btnClassNew.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent evt) {
        IWizardDescriptor descriptor = PlatformUI.getWorkbench()
            .getNewWizardRegistry().findWizard(CreatePythonTaskWizard.ID);
        try {
          if (descriptor != null) {
            IWizard wizard = descriptor.createWizard();
            String folder = RuminaqDiagramUtil.isTest(
                EclipseUtil.getModelPathFromEObject(pe)) ? EclipseExtensionImpl.TEST_PYTHON
                    : EclipseExtensionImpl.MAIN_PYTHON;
            String projectName = EclipseUtil
                .getProjectNameFromDiagram(dtp.getDiagram());
            IStructuredSelection selection = new StructuredSelection(
                ResourcesPlugin.getWorkspace().getRoot().getProject(projectName)
                    .getFolder(folder));
            ((CreatePythonTaskWizard) wizard).init(PlatformUI.getWorkbench(),
                selection);
            ((CreatePythonTaskWizard) wizard).setListener(PropertySection.this);
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

  @Override
  public void refresh(PictogramElement pe, TransactionalEditingDomain ed) {
    if (pe != null) {
      Object bo = Graphiti.getLinkService()
          .getBusinessObjectForLinkedPictogramElement(pe);
      if (bo == null)
        return;
      String className = ((PythonTask) bo).getImplementation();
      txtClassName.setText(className);
    }
  }

  @Override
  public void created(final String path) {
    ModelUtil.runModelChange(new Runnable() {
      public void run() {
        Object bo = Graphiti.getLinkService()
            .getBusinessObjectForLinkedPictogramElement(pe);
        if (bo == null)
          return;
        if (bo instanceof PythonTask) {
          PythonTask pythonTask = (PythonTask) bo;
          pythonTask.setImplementation(path);
          UpdateContext context = new UpdateContext(pe);
          dtp.getFeatureProvider().updateIfPossible(context);
        }
      }
    }, ed, "Set Python Class");
  }
}
