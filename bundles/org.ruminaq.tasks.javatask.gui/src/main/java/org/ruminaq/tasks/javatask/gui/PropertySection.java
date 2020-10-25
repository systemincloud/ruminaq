/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.javatask.gui;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.ruminaq.eclipse.RuminaqDiagramUtil;
import org.ruminaq.gui.properties.AbstractUserDefinedTaskPropertySection;
import org.ruminaq.tasks.javatask.client.JavaTask;
import org.ruminaq.tasks.javatask.client.annotations.JavaTaskInfo;
import org.ruminaq.tasks.javatask.gui.wizards.CreateJavaTaskWizard;
import org.ruminaq.util.EclipseUtil;
import org.ruminaq.util.Result;
import org.ruminaq.util.WidgetSelectedSelectionListener;

/**
 * PropertySection for JavaTask.
 *
 * @author Marek Jagielski
 */
public class PropertySection extends AbstractUserDefinedTaskPropertySection {

  /**
   * Return java directory depending on diagram path. For diagrams in test
   * directory return test java source directory. For diagrams in main directory
   * return main java source directory.
   *
   * @param diagram Graphiti Diagram
   * @param pe      selected element
   * @return selected java directory
   */
  private static IStructuredSelection getJavaDirectory(Diagram diagram,
      PictogramElement pe) {
    return new StructuredSelection(
        JavaCore.create(ResourcesPlugin.getWorkspace().getRoot()
            .getProject(EclipseUtil.getProjectNameFromDiagram(diagram))
            .getFolder(Optional.ofNullable(pe)
                .map(EclipseUtil::getModelPathFromEObject)
                .filter(RuminaqDiagramUtil::isTest)
                .map(m -> EclipseExtensionImpl.TEST_JAVA)
                .orElse(EclipseExtensionImpl.MAIN_JAVA))));
  }

  private static Optional<CreateJavaTaskWizard> getWizard() {
    return Optional
        .ofNullable(PlatformUI.getWorkbench().getNewWizardRegistry()
            .findWizard(CreateJavaTaskWizard.ID))
        .map(wd -> Result.attempt(wd::createWizard)).map(r -> r.orElse(null))
        .filter(Objects::nonNull).filter(CreateJavaTaskWizard.class::isInstance)
        .map(CreateJavaTaskWizard.class::cast);
  }

  private static void wizardDialog(IWizard wizard) {
    WizardDialog wd = new WizardDialog(Display.getDefault().getActiveShell(),
        wizard);
    wd.setTitle(wizard.getWindowTitle());
    wd.open();
  }

  private ITypeInfoFilterExtension filterJavaTaskClasses(IJavaProject project) {
    return (ITypeInfoRequestor requestor) -> {
      Optional<IType> type = Optional.ofNullable(requestor)
          .map((ITypeInfoRequestor r) -> {
            String pckg = requestor.getPackageName();
            if (!"".equals(pckg)) {
              pckg += ".";
            }
            return pckg + requestor.getTypeName();
          }).map(t -> Result.attempt(() -> project.findType(t)))
          .map(r -> r.orElse(null)).filter(Objects::nonNull)
          .filter(IType::exists);
      return type.map(t -> t.getAnnotation(JavaTaskInfo.class.getSimpleName()))
          .isPresent()
          && type
              .map(t -> Result.attempt(
                  () -> t.newSupertypeHierarchy(null).getSuperclass(t)))
              .map(r -> r.orElse(null)).filter(Objects::nonNull)
              .map(IType::getFullyQualifiedName)
              .filter(JavaTask.class.getCanonicalName()::equals).isPresent();
    };
  }

  @Override
  protected SelectionListener selectSelectionListener() {
    return (WidgetSelectedSelectionListener) (SelectionEvent evt) -> {
      IJavaProject project = JavaCore
          .create(ResourcesPlugin.getWorkspace().getRoot()
              .getProject(EclipseUtil.getProjectNameFromDiagram(getDiagram())));
      IJavaSearchScope scope = SearchEngine
          .createJavaSearchScope(new IJavaElement[] { project });
      SelectionDialog dialog = Result
          .attempt(() -> JavaUI.createTypeDialog(txtImplementation.getShell(),
              null, scope, IJavaElementSearchConstants.CONSIDER_CLASSES, false,
              "", new TypeSelectionExtension() {
                @Override
                public ITypeInfoFilterExtension getFilterExtension() {
                  return filterJavaTaskClasses(project);
                }
              }))
          .orElse(null);
      Optional.ofNullable(dialog).filter(d -> d.open() == Window.OK)
          .map(SelectionDialog::getResult).map(Stream::of)
          .orElseGet(Stream::empty).findFirst().filter(IType.class::isInstance)
          .map(IType.class::cast).map(IType::getFullyQualifiedName)
          .ifPresent((String clazz) -> {
            txtImplementation.setText(clazz);
            created(clazz);
          });
    };
  }

  @Override
  protected SelectionListener createSelectionListener() {
    return (WidgetSelectedSelectionListener) (SelectionEvent evt) -> {
      IStructuredSelection selection = getJavaDirectory(getDiagram(),
          getSelectedPictogramElement());
      getWizard().ifPresent((CreateJavaTaskWizard wizard) -> {
        wizard.init(PlatformUI.getWorkbench(), selection);
        wizard.setProject(selection);
        wizard.setListener(this);
        wizardDialog(wizard);
      });
    };
  }
}
