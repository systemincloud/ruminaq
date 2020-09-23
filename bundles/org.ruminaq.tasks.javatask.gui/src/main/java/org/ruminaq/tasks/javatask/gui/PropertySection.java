/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.javatask.gui;

import java.util.Optional;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.platform.GFPropertySection;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.dialogs.ITypeInfoFilterExtension;
import org.eclipse.jdt.ui.dialogs.ITypeInfoRequestor;
import org.eclipse.jdt.ui.dialogs.TypeSelectionExtension;
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
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.wizards.IWizardDescriptor;
import org.ruminaq.eclipse.RuminaqDiagramUtil;
import org.ruminaq.gui.model.diagram.RuminaqShape;
import org.ruminaq.model.ruminaq.ModelUtil;
import org.ruminaq.tasks.javatask.client.annotations.JavaTaskInfo;
import org.ruminaq.tasks.javatask.gui.wizards.CreateJavaTaskListener;
import org.ruminaq.tasks.javatask.gui.wizards.CreateJavaTaskWizard;
import org.ruminaq.tasks.javatask.model.javatask.JavaTask;
import org.ruminaq.util.EclipseUtil;

/**
 * PropertySection for JavaTask.
 *
 * @author Marek Jagielski
 */
public class PropertySection extends GFPropertySection
    implements CreateJavaTaskListener {

  private static final int FOUR_COLUMNS = 4;

  private Composite root;
  private CLabel lblClassSelect;
  private Text txtClassName;
  private Button btnClassSelect;
  private Button btnClassNew;

  @Override
  public void createControls(Composite parent,
      TabbedPropertySheetPage tabbedPropertySheetPage) {
    super.createControls(parent, tabbedPropertySheetPage);

    initLayout(parent);
    initComponents();
    initActions();
  }

  private void initLayout(Composite parent) {
    ((GridData) parent.getLayoutData()).verticalAlignment = SWT.FILL;
    ((GridData) parent.getLayoutData()).grabExcessVerticalSpace = true;

    root = new Composite(parent, SWT.NULL);
    root.setLayout(new GridLayout(FOUR_COLUMNS, false));

    lblClassSelect = new CLabel(root, SWT.NONE);
    txtClassName = new Text(root, SWT.BORDER);
    txtClassName
        .setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    btnClassSelect = new Button(root, SWT.NONE);
    btnClassNew = new Button(root, SWT.NONE);
  }

  private void initComponents() {
    lblClassSelect.setText("Java Task Class:");
    btnClassSelect.setText("Select class");
    btnClassNew.setText("Create");
  }

  private void save() {
    Shell shell = txtClassName.getShell();
    boolean parse = new UpdateFeature(
        getDiagramTypeProvider().getFeatureProvider())
            .load(txtClassName.getText());
    if (parse) {
      ModelUtil.runModelChange(() -> {
        Object bo = Graphiti.getLinkService()
            .getBusinessObjectForLinkedPictogramElement(
                getSelectedPictogramElement());
        if (bo == null)
          return;
        if (bo instanceof JavaTask) {
          JavaTask javaTask = (JavaTask) bo;
          javaTask.setImplementationClass(txtClassName.getText());
          UpdateContext context = new UpdateContext(
              getSelectedPictogramElement());
          getDiagramTypeProvider().getFeatureProvider()
              .updateIfPossible(context);
        }
      }, getDiagramContainer().getDiagramBehavior().getEditingDomain(),
          "Set Java Class");
    } else
      MessageDialog.openError(shell, "Can't edit value",
          "Class not found or incorrect.");
  }

  private void initActions() {
    txtClassName.addTraverseListener(new TraverseListener() {
      @Override
      public void keyTraversed(TraverseEvent event) {
        if (event.detail == SWT.TRAVERSE_RETURN)
          save();
      }
    });
    btnClassSelect.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent evt) {
        Shell shell = txtClassName.getShell();
        final IJavaProject project = JavaCore
            .create(ResourcesPlugin.getWorkspace().getRoot().getProject(
                EclipseUtil.getProjectNameFromDiagram(getDiagram())));
        try {
          SelectionDialog dialog = JavaUI.createTypeDialog(shell, null,
              SearchEngine
                  .createJavaSearchScope(new IJavaElement[] { project }),
              IJavaElementSearchConstants.CONSIDER_CLASSES, false, "",
              new TypeSelectionExtension() {
                @Override
                public ITypeInfoFilterExtension getFilterExtension() {
                  ITypeInfoFilterExtension extension = new ITypeInfoFilterExtension() {
                    @Override
                    public boolean select(ITypeInfoRequestor requestor) {
                      try {
                        String pag = requestor.getPackageName();
                        String t = (pag.equals("") ? "" : pag + ".")
                            + requestor.getTypeName();
                        IType type = project.findType(t);
                        if (type == null || !type.exists())
                          return false;
                        IAnnotation[] annotations;
                        annotations = type.getAnnotations();
                        if (annotations == null)
                          return false;
                        IAnnotation sicInfo = null;
                        for (IAnnotation a : annotations)
                          if (a.getElementName()
                              .equals(JavaTaskInfo.class.getSimpleName()))
                            sicInfo = a;
                        if (sicInfo == null)
                          return false;

                        IType supertype = type.newSupertypeHierarchy(null)
                            .getSuperclass(type);
                        if (org.ruminaq.tasks.javatask.client.JavaTask.class
                            .getCanonicalName()
                            .equals(supertype.getFullyQualifiedName()))
                          return true;
                        return false;
                      } catch (JavaModelException e) {
                        return false;
                      }
                    }
                  };
                  return extension;
                }
              });
          if (dialog.open() == SelectionDialog.OK) {
            Object[] result = dialog.getResult();
            String className = ((IType) result[0]).getFullyQualifiedName();

            if (className != null)
              txtClassName.setText(className);

            ModelUtil.runModelChange(new Runnable() {
              public void run() {
                Object bo = Graphiti.getLinkService()
                    .getBusinessObjectForLinkedPictogramElement(
                        getSelectedPictogramElement());
                if (bo == null)
                  return;
                String implementationName = txtClassName.getText();
                if (implementationName != null) {
                  if (bo instanceof JavaTask) {
                    JavaTask javaTask = (JavaTask) bo;
                    javaTask.setImplementationClass(implementationName);
                    UpdateContext context = new UpdateContext(
                        getSelectedPictogramElement());
                    getDiagramTypeProvider().getFeatureProvider()
                        .updateIfPossible(context);
                  }
                }
              }
            }, getDiagramContainer().getDiagramBehavior().getEditingDomain(),
                "Set Java Class");
          }
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    });
    btnClassNew.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent evt) {
        IWizardDescriptor descriptor = PlatformUI.getWorkbench()
            .getNewWizardRegistry().findWizard(CreateJavaTaskWizard.ID);
        try {
          if (descriptor != null) {
            IWizard wizard = descriptor.createWizard();
            String folder = RuminaqDiagramUtil.isTest(EclipseUtil
                .getModelPathFromEObject(getSelectedPictogramElement()))
                    ? EclipseExtensionImpl.TEST_JAVA
                    : EclipseExtensionImpl.MAIN_JAVA;
            String projectName = EclipseUtil.getProjectNameFromDiagram(
                getDiagramContainer().getDiagramTypeProvider().getDiagram());
            IStructuredSelection selection = new StructuredSelection(
                JavaCore.create(ResourcesPlugin.getWorkspace().getRoot()
                    .getProject(projectName).getFolder(folder)));
            ((CreateJavaTaskWizard) wizard).init(PlatformUI.getWorkbench(),
                selection);
            ((CreateJavaTaskWizard) wizard).setProject(selection);
            ((CreateJavaTaskWizard) wizard).setListener(PropertySection.this);
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

  @Override
  public void refresh() {
    if (getSelectedPictogramElement() != null) {
      Object bo = Graphiti.getLinkService()
          .getBusinessObjectForLinkedPictogramElement(
              getSelectedPictogramElement());
      if (bo == null)
        return;
      String className = ((JavaTask) bo).getImplementationClass();
      txtClassName.setText(className);
    }
  }

  @Override
  public void created(IType type) {
    ModelUtil.runModelChange(() -> {
      Optional.of(getSelectedPictogramElement())
          .filter(RuminaqShape.class::isInstance).map(RuminaqShape.class::cast)
          .map(RuminaqShape::getModelObject).filter(JavaTask.class::isInstance)
          .map(JavaTask.class::cast).ifPresent(javaTask -> javaTask
              .setImplementationClass(type.getFullyQualifiedName()));
      getDiagramTypeProvider().getFeatureProvider()
          .updateIfPossible(new UpdateContext(getSelectedPictogramElement()));
    }, getDiagramContainer().getDiagramBehavior().getEditingDomain(),
        "Set Java Class");
  }
}
