/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.javatask.gui;

import java.util.Objects;
import java.util.Optional;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.dialogs.ITypeInfoFilterExtension;
import org.eclipse.jdt.ui.dialogs.ITypeInfoRequestor;
import org.eclipse.jdt.ui.dialogs.TypeSelectionExtension;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.wizards.IWizardDescriptor;
import org.ruminaq.eclipse.RuminaqDiagramUtil;
import org.ruminaq.gui.properties.AbstractUserDefinedTaskPropertySection;
import org.ruminaq.model.ruminaq.ModelUtil;
import org.ruminaq.tasks.javatask.client.annotations.JavaTaskInfo;
import org.ruminaq.tasks.javatask.gui.wizards.CreateJavaTaskListener;
import org.ruminaq.tasks.javatask.gui.wizards.CreateJavaTaskWizard;
import org.ruminaq.tasks.javatask.model.javatask.JavaTask;
import org.ruminaq.util.EclipseUtil;
import org.ruminaq.util.Result;
import org.ruminaq.util.WidgetSelectedSelectionListener;

/**
 * PropertySection for JavaTask.
 *
 * @author Marek Jagielski
 */
public class PropertySection extends AbstractUserDefinedTaskPropertySection
    implements CreateJavaTaskListener {

  @Override
  protected void initActions() {
    txtImplementation.addTraverseListener((TraverseEvent event) -> {
      if (event.detail == SWT.TRAVERSE_RETURN) {
        save();
      }
    });
    btnSelect.addSelectionListener(
        (WidgetSelectedSelectionListener) (SelectionEvent evt) -> {
          IJavaProject project = JavaCore
              .create(ResourcesPlugin.getWorkspace().getRoot().getProject(
                  EclipseUtil.getProjectNameFromDiagram(getDiagram())));
          IJavaSearchScope scope = SearchEngine
              .createJavaSearchScope(new IJavaElement[] { project });
          SelectionDialog dialog = Result.attempt(
              () -> JavaUI.createTypeDialog(txtImplementation.getShell(), null,
                  scope, IJavaElementSearchConstants.CONSIDER_CLASSES, false,
                  "", new TypeSelectionExtension() {
                    @Override
                    public ITypeInfoFilterExtension getFilterExtension() {
                      return (ITypeInfoRequestor requestor) -> {
                        String pag = requestor.getPackageName();
                        String typeName = (pag.equals("") ? "" : pag + ".")
                            + requestor.getTypeName();
                        Optional<IType> type = Optional
                            .ofNullable(Result
                                .attempt(() -> project.findType(typeName)))
                            .map(r -> r.orElse(null)).filter(Objects::nonNull)
                            .filter(IType::exists);

                        return type
                            .map(t -> t.getAnnotation(
                                JavaTaskInfo.class.getSimpleName()))
                            .isPresent()
                            && type
                                .map(t -> Result
                                    .attempt(() -> t.newSupertypeHierarchy(null)
                                        .getSuperclass(t)))
                                .map(r -> r.orElse(null))
                                .filter(Objects::nonNull)
                                .map(IType::getFullyQualifiedName)
                                .filter(
                                    org.ruminaq.tasks.javatask.client.JavaTask.class
                                        .getCanonicalName()::equals)
                                .isPresent();
                      };
                    }
                  }))
              .orElse(null);
          if (dialog != null && dialog.open() == Window.OK) {
            Object[] result = dialog.getResult();
            String className = ((IType) result[0]).getFullyQualifiedName();

            if (className != null)
              txtImplementation.setText(className);

            Optional.ofNullable(txtImplementation.getText()).ifPresent(
                implementationName -> ModelUtil.runModelChange(() -> {
                  selectedModelObject(JavaTask.class).ifPresent(
                      jt -> jt.setImplementationPath(implementationName));
                  getDiagramTypeProvider().getFeatureProvider()
                      .updateIfPossible(
                          new UpdateContext(getSelectedPictogramElement()));
                }, getDiagramContainer().getDiagramBehavior()
                    .getEditingDomain(), Messages.propertySectionSetCommand));
          }
        });
    btnCreate.addSelectionListener(
        (WidgetSelectedSelectionListener) (SelectionEvent evt) -> {
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
        });
  }

  @Override
  public void created(IType type) {
    ModelUtil.runModelChange(() -> {
      selectedModelObject(JavaTask.class).ifPresent(javaTask -> javaTask
          .setImplementationPath(type.getFullyQualifiedName()));
      getDiagramTypeProvider().getFeatureProvider()
          .updateIfPossible(new UpdateContext(getSelectedPictogramElement()));
    }, getDiagramContainer().getDiagramBehavior().getEditingDomain(),
        Messages.propertySectionSetCommand);
  }
}
