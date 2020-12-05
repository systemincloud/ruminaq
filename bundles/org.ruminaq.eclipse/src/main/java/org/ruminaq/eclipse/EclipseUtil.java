/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse;

import java.io.ByteArrayInputStream;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.ruminaq.util.Result;
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
  public static IProject getProjectOf(Object obj) {
    return Arrays
        .<Supplier<Optional<IProject>>>asList(
            () -> Optional.of(obj).filter(IProject.class::isInstance)
                .map(IProject.class::cast),
            () -> Optional.of(obj).filter(IJavaProject.class::isInstance)
                .map(IJavaProject.class::cast).map(IJavaProject::getProject),
            () -> Optional.of(obj).filter(IResource.class::isInstance)
                .map(IResource.class::cast).map(IResource::getProject),
            () -> Optional.of(obj).filter(IPackageFragment.class::isInstance)
                .map(IPackageFragment.class::cast)
                .map(IPackageFragment::getJavaProject)
                .map(IJavaProject::getProject),
            () -> Optional.of(obj)
                .filter(IPackageFragmentRoot.class::isInstance)
                .map(IPackageFragmentRoot.class::cast)
                .map(IPackageFragmentRoot::getJavaProject)
                .map(IJavaProject::getProject),
            () -> Optional.of(obj).filter(PictogramElement.class::isInstance)
                .map(PictogramElement.class::cast)
                .map(PictogramElement::eResource)
                .map(r -> r.getURI().segment(1)).map(URI::decode)
                .map(u -> ResourcesPlugin.getWorkspace().getRoot()
                    .getProject(u)),
            () -> Optional.of(obj).filter(EObject.class::isInstance)
                .map(EObject.class::cast)
                .map(EclipseUtil::getProjectFromEObject))
        .stream()
        .reduce((a,
            b) -> () -> Optional.of(a).map(Supplier::get)
                .filter(Optional::isPresent).orElseGet(b::get))
        .orElseGet(() -> Optional::empty).get().orElse(null);
  }

  private static IProject getProjectFromEObject(EObject eobject) {
    Optional<URI> uri = Optional.of(getUriOf(eobject));
    IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
    return uri.map(URI::toString).map(workspaceRoot::findMember)
        .map(IResource::getProject)
        .orElseGet(() -> uri.filter(u -> u.segmentCount() > 0)
            .map(u -> u.segment(0)).map(workspaceRoot::findMember)
            .map(IResource::getProject).orElse(null));
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
              String currentPath = new Path(parentPath.getValue())
                  .append(segment).toString();
              return Optional.of(parentPath).filter(p -> !p.getKey().isFailed())
                  .map(p -> new SimpleEntry<>(Optional.of(currentPath)
                      .map(project::getFolder)
                      .filter(Predicate.not(IFolder::exists))
                      .map(f -> Try.check(() -> f.create(true, true,
                          new NullProgressMonitor())))
                      .orElseGet(Try::success), currentPath))
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
   * Open file.
   *
   * @param file eclipse file
   */
  public static void openFileInDefaultEditor(IFile file) {
    Result.attempt(() -> IDE.openEditor(
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(),
        file, true));
  }

  /**
   * Uri of EObject.
   *
   * @param eObject object to check
   * @return uri
   */
  public static URI getUriOf(EObject eObject) {
    URI uri = EcoreUtil.getURI(eObject).trimFragment();
    if (uri.isPlatform()) {
      uri = URI.createURI(uri.toPlatformString(true));
    }
    return uri;
  }

  public static URI removeFristSegments(URI uri, int nb) {
    nb++;
    String[] segs = uri.segments();
    String tmp = "";
    int i = 0;
    for (String s : segs) {
      i++;
      if (i < nb)
        continue;
      if (i > nb)
        tmp += "/";
      tmp += s;
    }
    return URI.createURI(tmp);
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
