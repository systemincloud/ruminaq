/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

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
import org.python.pydev.core.IInfo;
import org.python.pydev.shared_ui.EditorUtils;
import org.ruminaq.eclipse.RuminaqDiagramUtil;
import org.ruminaq.gui.properties.AbstractUserDefinedTaskPropertySection;
import org.ruminaq.model.ruminaq.ModelUtil;
import org.ruminaq.tasks.pythontask.gui.util.FindPythonTask;
import org.ruminaq.tasks.pythontask.gui.util.SicGlobalsTwoPanelElementSelector2;
import org.ruminaq.tasks.pythontask.model.pythontask.PythonTask;
import org.ruminaq.tasks.pythontask.ui.wizards.CreatePythonTaskListener;
import org.ruminaq.tasks.pythontask.ui.wizards.CreatePythonTaskWizard;
import org.ruminaq.util.EclipseUtil;

import com.python.pydev.analysis.additionalinfo.AdditionalInfoAndIInfo;

public class PropertySection extends AbstractUserDefinedTaskPropertySection implements CreatePythonTaskListener {

  private PictogramElement pe;
  private TransactionalEditingDomain ed;
  private IDiagramTypeProvider dtp;

  protected void initLayout(Composite parent) {
    ((GridData) parent.getLayoutData()).verticalAlignment = SWT.FILL;
    ((GridData) parent.getLayoutData()).grabExcessVerticalSpace = true;
    Composite root = new Composite(parent, SWT.NULL);
    root.setLayout(new GridLayout(4, false));

    lblImplementation = new CLabel(root, SWT.NONE);
    txtImplementation = new Text(root, SWT.BORDER);
    txtImplementation
        .setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    btnSelect = new Button(root, SWT.NONE);
    btnCreate = new Button(root, SWT.NONE);
  }

  protected void initComponents() {
    lblImplementation.setText("Python Task Class:");
    btnSelect.setText("Select class");
    btnCreate.setText("Create");
  }

  private void save() {
    Shell shell = txtImplementation.getShell();
    boolean parse = new UpdateFeature(dtp.getFeatureProvider())
        .load(txtImplementation.getText());
    if (parse) {
      ModelUtil.runModelChange(new Runnable() {
        public void run() {
          Object bo = Graphiti.getLinkService()
              .getBusinessObjectForLinkedPictogramElement(pe);
          if (bo == null)
            return;
          if (bo instanceof PythonTask) {
            PythonTask pythonTask = (PythonTask) bo;
            pythonTask.setImplementation(txtImplementation.getText());
            UpdateContext context = new UpdateContext(pe);
            dtp.getFeatureProvider().updateIfPossible(context);
          }
        }
      }, ed, "Set Python Class");
    } else
      MessageDialog.openError(shell, "Can't edit value",
          "Class not found or incorrect.");
  }

  protected void initActions() {
    txtImplementation.addTraverseListener(new TraverseListener() {
      @Override
      public void keyTraversed(TraverseEvent event) {
        if (event.detail == SWT.TRAVERSE_RETURN)
          save();
      }
    });
    btnSelect.addSelectionListener(new SelectionAdapter() {
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
            txtImplementation.setText(entry.getDeclaringModuleName());

            ModelUtil.runModelChange(new Runnable() {
              public void run() {
                Object bo = Graphiti.getLinkService()
                    .getBusinessObjectForLinkedPictogramElement(pe);
                if (bo == null)
                  return;
                String implementationName = txtImplementation.getText();
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
    btnCreate.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent evt) {
        IWizardDescriptor descriptor = PlatformUI.getWorkbench()
            .getNewWizardRegistry().findWizard(CreatePythonTaskWizard.ID);
        try {
          if (descriptor != null) {
            IWizard wizard = descriptor.createWizard();
            String folder = RuminaqDiagramUtil
                .isTest(EclipseUtil.getModelPathFromEObject(pe))
                    ? EclipseExtensionImpl.TEST_PYTHON
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

  public void refresh(PictogramElement pe, TransactionalEditingDomain ed) {
    if (pe != null) {
      Object bo = Graphiti.getLinkService()
          .getBusinessObjectForLinkedPictogramElement(pe);
      if (bo == null)
        return;
      String className = ((PythonTask) bo).getImplementation();
      txtImplementation.setText(className);
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
