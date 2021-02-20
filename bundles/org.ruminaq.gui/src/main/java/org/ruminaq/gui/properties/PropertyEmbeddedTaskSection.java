/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.properties;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.ruminaq.eclipse.EclipseUtil;
import org.ruminaq.eclipse.wizards.diagram.CreateDiagramWizard;
import org.ruminaq.eclipse.wizards.project.CreateSourceFolders;
import org.ruminaq.util.Result;
import org.ruminaq.util.WidgetSelectedSelectionListener;

/**
 * PropertySection for EmbeddedTask.
 *
 * @author Marek Jagielski
 */
public class PropertyEmbeddedTaskSection
    extends AbstractUserDefinedTaskPropertySection {

  private static final Path MAIN_PATH = new Path(
      CreateSourceFolders.MAIN_RESOURCES);

  private static final Path TEST_PATH = new Path(
      CreateSourceFolders.TEST_RESOURCES);

  private static Optional<IPath> folderRelativePath(Object element) {
    return Optional.of(element).filter(IFolder.class::isInstance)
        .map(IFolder.class::cast).map(IFolder::getProjectRelativePath);
  }

  private static Optional<IFile> file(Object element) {
    return Optional.of(element).filter(IFile.class::isInstance)
        .map(IFile.class::cast);
  }

  private static boolean startsWithPath(Object element, Path path) {
    return folderRelativePath(element)
        .filter(dirs -> dirs.matchingFirstSegments(path) >= path.segmentCount())
        .isPresent()
        || folderRelativePath(element)
            .filter(p -> IntStream.range(1, path.segmentCount())
                .mapToObj(path::uptoSegment).anyMatch(p::equals))
            .isPresent();
  }

  @Override
  protected SelectionListener selectSelectionListener() {
    return (WidgetSelectedSelectionListener) (SelectionEvent evt) -> {
      ElementTreeSelectionDialog fileDialog = createSelectDialog();
      fileDialog.open();
      Optional.ofNullable(fileDialog.getResult()).map(Stream::of)
          .orElseGet(Stream::empty).findFirst().filter(IFile.class::isInstance)
          .map(IFile.class::cast).map(IFile::getFullPath)
          .map(p -> p.removeFirstSegments(1)).map(IPath::toString)
          .ifPresent(this::setImplementation);
    };
  }

  private ElementTreeSelectionDialog createSelectDialog() {
    ElementTreeSelectionDialog fileDialog = new ElementTreeSelectionDialog(
        Display.getDefault().getActiveShell(), new WorkbenchLabelProvider(),
        new BaseWorkbenchContentProvider());
    fileDialog.setInput(EclipseUtil.getProjectOf(getDiagram()));
    fileDialog.setTitle("Select Diagram File");
    fileDialog.setMessage("Select diagram file from the tree:");
    fileDialog.setAllowMultiple(false);
    fileDialog.addFilter(new ViewerFilter() {
      @Override
      public boolean select(Viewer arg0, Object parent, Object element) {
        return startsWithPath(element, MAIN_PATH)
            || startsWithPath(element, TEST_PATH)
            || (file(element).map(IFile::getFileExtension)
                .filter(CreateDiagramWizard.EXTENSION::equals).isPresent()
                && file(element).map(IFile::getFullPath).map(IPath::toString)
                    .filter(EclipseUtil.getUriOf(getSelectedPictogramElement())
                        .path()::equals)
                    .isEmpty());
      }
    });
    fileDialog.setValidator((Object[] selection) -> {
      if (Stream.of(selection).findFirst().filter(IFile.class::isInstance)
          .isPresent()) {
        return new Status(IStatus.OK, PropertyEmbeddedTaskSection.class, 0, "",
            null);
      } else {
        return new Status(IStatus.ERROR, PropertyEmbeddedTaskSection.class, 0,
            "", null);
      }
    });
    return fileDialog;
  }

  @Override
  protected SelectionListener createSelectionListener() {
    return (WidgetSelectedSelectionListener) (SelectionEvent evt) -> Optional
        .ofNullable(PlatformUI.getWorkbench().getNewWizardRegistry()
            .findWizard(CreateDiagramWizard.ID))
        .map(d -> Result.attempt(d::createWizard)).map(r -> r.orElse(null))
        .filter(Objects::nonNull).filter(CreateDiagramWizard.class::isInstance)
        .map(CreateDiagramWizard.class::cast)
        .ifPresent((CreateDiagramWizard wizard) -> {
          wizard.init(PlatformUI.getWorkbench(), new StructuredSelection(
              JavaCore.create(EclipseUtil.getProjectOf(getDiagram()))));
          WizardDialog wd = new WizardDialog(
              Display.getDefault().getActiveShell(), wizard);
          wd.setTitle(wizard.getWindowTitle());
          wizard.setListener(this);
          wd.open();
        });
  }
}
