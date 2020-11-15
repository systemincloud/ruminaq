/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.properties;

import java.util.Objects;
import java.util.Optional;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.ruminaq.eclipse.wizards.diagram.CreateDiagramWizard;
import org.ruminaq.util.EclipseUtil;
import org.ruminaq.util.Result;
import org.ruminaq.util.WidgetSelectedSelectionListener;

/**
 * PropertySection for EmbeddedTask.
 *
 * @author Marek Jagielski
 */
public class PropertyEmbeddedTaskSection
    extends AbstractUserDefinedTaskPropertySection {

//  public static final String MAIN_PREFIX = "main:";
//  public static final String TEST_PREFIX = "test:";
//
//  private void save() {
//    Shell shell = txtTaskName.getShell();
//    String tmp = txtTaskName.getText();
//    if (tmp.startsWith(MAIN_PREFIX))
//      tmp = tmp.replace(MAIN_PREFIX,
//          SourceFolders.MAIN_RESOURCES + "/" + SourceFolders.TASK_FOLDER + "/");
//    else if (tmp.startsWith(TEST_PREFIX))
//      tmp = tmp.replace(TEST_PREFIX,
//          SourceFolders.TEST_RESOURCES + "/" + SourceFolders.TASK_FOLDER + "/");
//
//    final String taskPath = tmp;
//
//    boolean parse = new UpdateEmbeddedTaskFeature(dtp.getFeatureProvider()).load(taskPath);
//    if (parse) {
//      ModelUtil.runModelChange(new Runnable() {
//        @Override
//        public void run() {
//          Object bo = Graphiti.getLinkService()
//              .getBusinessObjectForLinkedPictogramElement(pe);
//          if (bo == null)
//            return;
//          if (bo instanceof EmbeddedTask) {
//            EmbeddedTask et = (EmbeddedTask) bo;
//            et.setImplementationTask(taskPath);
//            UpdateContext context = new UpdateContext(pe);
//            dtp.getFeatureProvider().updateIfPossible(context);
//          }
//        }
//      }, ed, "Set Java Class");
//    } else
//      MessageDialog.openError(shell, "Can't edit value",
//          "Task not found or incorrect.");
//  }
//
//  private void initActions(final PictogramElement pe,
//      final TransactionalEditingDomain ed, final IDiagramTypeProvider dtp,
//      final boolean test, final String path) {
//    if (test)
//      btnNewTest.addSelectionListener(new SelectionAdapter() {
//        @Override
//        public void widgetSelected(SelectionEvent evt) {
////          IWizardDescriptor descriptor = PlatformUI.getWorkbench()
////              .getNewWizardRegistry().findWizard(CreateTestDiagramWizard.ID);
////          try {
////            if (descriptor != null) {
////              IWizard wizard = descriptor.createWizard();
////              IStructuredSelection selection = new StructuredSelection(
////                  JavaCore.create(ResourcesPlugin.getWorkspace().getRoot()
////                      .getProject(EclipseUtil
////                          .getProjectNameFromDiagram(dtp.getDiagram()))));
////              ((CreateTestDiagramWizard) wizard).init(PlatformUI.getWorkbench(),
////                  selection);
////              WizardDialog wd = new WizardDialog(
////                  Display.getDefault().getActiveShell(), wizard);
////              wd.setTitle(wizard.getWindowTitle());
////              wd.open();
////            }
////          } catch (CoreException e) {
////            e.printStackTrace();
////          }
//        }
//      });
//  }
//
//  public void refresh(PictogramElement pe, TransactionalEditingDomain ed) {
//    if (pe != null) {
//      Object bo = Graphiti.getLinkService()
//          .getBusinessObjectForLinkedPictogramElement(pe);
//      if (bo == null)
//        return;
//      String taskPath = ((EmbeddedTask) bo).getImplementationTask();
//      taskPath = taskPath.replaceFirst("/" + SourceFolders.TASK_FOLDER + "/",
//          "");
//      if (taskPath.startsWith(SourceFolders.MAIN_RESOURCES))
//        txtTaskName.setText(
//            taskPath.replace(SourceFolders.MAIN_RESOURCES, MAIN_PREFIX));
//      else if (taskPath.startsWith(SourceFolders.TEST_RESOURCES))
//        txtTaskName.setText(
//            taskPath.replace(SourceFolders.TEST_RESOURCES, TEST_PREFIX));
//    }
//  }

  @Override
  protected SelectionListener selectSelectionListener() {
    return (WidgetSelectedSelectionListener) (SelectionEvent evt) -> {
    ElementTreeSelectionDialog fileDialog = new ElementTreeSelectionDialog(
        Display.getDefault().getActiveShell(), new WorkbenchLabelProvider(),
        new BaseWorkbenchContentProvider());
////    if (test)
////      fileDialog.setInput(ResourcesPlugin.getWorkspace().getRoot()
////          .getProject(
////              EclipseUtil.getProjectNameFromDiagram(dtp.getDiagram()))
////          .getFolder("src"));
////    else
////      fileDialog.setInput(ResourcesPlugin.getWorkspace().getRoot()
////          .getProject(
////              EclipseUtil.getProjectNameFromDiagram(dtp.getDiagram()))
////          .getFolder(SourceFolders.MAIN_RESOURCES));
////    fileDialog.setTitle("Select Diagram File");
////    fileDialog.setMessage("Select diagram file from the tree:");
////    fileDialog.setAllowMultiple(false);
////    fileDialog.addFilter(new ViewerFilter() {
////      @Override
////      public boolean select(Viewer arg0, Object parent, Object element) {
////        if (element instanceof IProject)
////          return true;
////
////        if (element instanceof IFolder) {
////          IPath dirs = ((IFolder) element).getProjectRelativePath();
////          IPath mainPath = new Path(SourceFolders.MAIN_RESOURCES);
////          for (int i = 1; i < mainPath.segmentCount(); i++)
////            if (dirs.equals(mainPath.uptoSegment(i)))
////              return true;
////          if (dirs.matchingFirstSegments(mainPath) == 3)
////            return true;
////          IPath testPath = new Path(SourceFolders.TEST_RESOURCES);
////          for (int i = 1; i < testPath.segmentCount(); i++)
////            if (dirs.equals(testPath.uptoSegment(i)))
////              return true;
////          if (dirs.matchingFirstSegments(testPath) == 3)
////            return true;
////          return false;
////        }
////
////        if (element instanceof IFile) {
////          if (((IFile) element).getFileExtension() == null)
////            return false;
////          if (((IFile) element).getFullPath().toString().equals(path))
////            return false;
////
////          String ext = ((IFile) element).getFileExtension();
//////          if (ext.equals(CreateDiagramWizard.EXTENSION))
////            return true;
////        }
////        return false;
////      }
//
////    });
////    fileDialog.setValidator(new ISelectionStatusValidator() {
////      @Override
////      public IStatus validate(Object[] selection) {
////        if (selection.length == 1 && selection[0] instanceof IFile)
////          return new Status(IStatus.OK, SicPlugin.GUI_ID.s(), 0, "", null);
////        else
////          return new Status(IStatus.ERROR, SicPlugin.GUI_ID.s(), 0, "",
////              null);
////        return null;
////      }
////    });
////    fileDialog.open();
////    Object[] results = fileDialog.getResult();
//
////    if (results != null) {
////      final IPath selectedPath = ((IFile) results[0]).getFullPath()
////          .removeFirstSegments(1);
////      String taskPath = ((IFile) results[0]).getFullPath()
////          .removeFirstSegments(5).toString();
////      String show = "";
////      if (selectedPath.toString().startsWith(SourceFolders.MAIN_RESOURCES))
////        show = MAIN_PREFIX + taskPath;
////      if (selectedPath.toString().startsWith(SourceFolders.TEST_RESOURCES))
////        show = TEST_PREFIX + taskPath;
////
////      if (taskPath != null)
////        txtTaskName.setText(show);
////
////      ModelUtil.runModelChange(new Runnable() {
////        @Override
////        public void run() {
////          String implementationPath = txtTaskName.getText();
////          if (implementationPath == null)
////            return;
////
////          EObject bo = Graphiti.getLinkService()
////              .getBusinessObjectForLinkedPictogramElement(pe);
////          if (bo == null || !(bo instanceof EmbeddedTask))
////            return;
////          EmbeddedTask et = (EmbeddedTask) bo;
////
////          et.setImplementationTask(selectedPath.toString());
////          UpdateContext context = new UpdateContext(pe);
////          dtp.getFeatureProvider().updateIfPossible(context);
////        }
////      }, ed, "Change embedded diagram");
////    }
//  }
    };
  }

  @Override
  protected SelectionListener createSelectionListener() {
    return (WidgetSelectedSelectionListener) (SelectionEvent evt) -> {
      IStructuredSelection selection = new StructuredSelection(
          JavaCore.create(ResourcesPlugin.getWorkspace().getRoot().getProject(
              EclipseUtil.getProjectNameFromDiagram(getDiagram()))));
      Optional
          .ofNullable(PlatformUI.getWorkbench().getNewWizardRegistry()
              .findWizard(CreateDiagramWizard.ID))
          .map(d -> Result.attempt(() -> d.createWizard()))
          .map(r -> r.orElse(null)).filter(Objects::nonNull)
          .filter(CreateDiagramWizard.class::isInstance)
          .map(CreateDiagramWizard.class::cast)
          .ifPresent((CreateDiagramWizard wizard) -> {
            wizard.init(PlatformUI.getWorkbench(), selection);
            WizardDialog wd = new WizardDialog(
                Display.getDefault().getActiveShell(), wizard);
            wd.setTitle(wizard.getWindowTitle());
            wd.open();
          });
    };
  }
}
