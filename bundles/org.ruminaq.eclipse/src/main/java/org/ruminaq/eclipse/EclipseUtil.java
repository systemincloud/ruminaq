/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse;

import java.io.ByteArrayInputStream;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.swt.widgets.Table;
import org.ruminaq.util.Try;

/**
 * Util class.
 *
 * @author Marek Jagielski
 */
public final class EclipseUtil {

  private EclipseUtil() {
    // util class
  }

  /**
   * Return project of eclipse resource.
   *
   * @param obj eclipse resource
   * @return eclipse project
   */
  public static IProject getProjectFromSelection(Object obj) {
    String projectName = null;
    if (obj instanceof IProject) {
      projectName = ((IProject) obj).getName();
    } else if (obj instanceof IJavaProject) {
      projectName = ((IJavaProject) obj).getElementName();
    } else if (obj instanceof IResource) {
      projectName = ((IResource) obj).getProject().getName();
    } else if (obj instanceof IPackageFragment) {
      projectName = ((IPackageFragment) obj).getJavaProject().getElementName();
    } else if (obj instanceof IPackageFragmentRoot) {
      projectName = ((IPackageFragmentRoot) obj).getJavaProject()
          .getElementName();
    }
    return Optional.ofNullable(projectName)
        .map(pn -> ResourcesPlugin.getWorkspace().getRoot().getProject(pn))
        .orElse(null);
  }

  /**
   * Create folders.
   *
   * @param project eclipse project
   * @param path    folder path
   * @return result
   */
  public static Try<CoreException> createFolderWithParents(IProject project,
      String path) {
    return Optional.ofNullable(path).map(p -> p.split("/")).map(Stream::of)
        .orElseGet(Stream::empty)
        .reduce(new SimpleEntry<>(Try.<CoreException>success(), ""),
            (SimpleEntry<Try<CoreException>, String> parentPath,
                String segment) -> {
              String currentPath = parentPath.getValue() + "/" + segment;
              return Optional.of(parentPath).filter(p -> !p.getKey().isFailed())
                  .map(p -> new SimpleEntry<>(
                      Optional.of(currentPath).map(project::getFolder)
                          .filter(Predicate.not(IFolder::exists))
                          .map(f -> Try.check(() -> f.create(true, true,
                              new NullProgressMonitor())))
                          .orElseGet(() -> Try.success()),
                      currentPath))
                  .orElseGet(() -> {
                    parentPath.setValue(currentPath);
                    return parentPath;
                  });
            }, (a, b) -> Optional.of(a).filter(r -> r.getKey().isFailed())
                .orElse(b))
        .getKey();
  }

  /**
   * Create file.
   *
   * @param project eclipse project
   * @param path    folder path
   * @param name    file name
   * @return result
   */
  public static Try<CoreException> createFileInFolder(IProject project,
      String path, String name) {
    IFile file = project.getFile(path + "/" + name);
    return Optional.of(file).filter(IFile::exists)
        .map(f -> Try.<CoreException>success())
        .orElseGet(() -> Try
            .check(() -> file.create(new ByteArrayInputStream(new byte[0]),
                true, new NullProgressMonitor())));
  }

  /**
   * Remove selected rows from given swt Table.
   *
   * @param table swt Table
   */
  public static void removeSelectedRows(Table table) {
    IntStream.of(table.getSelectionIndices()).boxed()
        .sorted(Collections.reverseOrder()).forEach(table::remove);
  }

  /**
   * Check if non-empty value is present in column table.
   *
   * @param table  table to check
   * @param column column index
   * @param value  value to look for
   * @return nonempty value in column
   */
  public static boolean hasNonEmptyValueInTable(Table table, int column,
      String value) {
    return !"".equals(value) && Stream.of(table.getItems())
        .map(i -> i.getText(column)).noneMatch(value::equals);
  }

  /**
   * Check if selected elements in table are one block.
   *
   * @param table table to check
   * @return selections in one block
   */
  public static boolean tableSelectionsConsecutive(Table table) {
    int[] s = table.getSelectionIndices();
    return IntStream.range(0, s.length - 1)
        .noneMatch(i -> Math.abs(s[i] - s[i + 1]) > 1);
  }
}
