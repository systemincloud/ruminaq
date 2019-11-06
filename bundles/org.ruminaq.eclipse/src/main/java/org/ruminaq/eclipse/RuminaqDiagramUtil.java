/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse;

import java.io.File;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.osgi.framework.util.FilePath;
import org.ruminaq.eclipse.wizards.diagram.CreateDiagramWizard;
import org.ruminaq.eclipse.wizards.project.SourceFolders;

/**
 * Diagram helper functions.
 *
 * @author Marek Jagielski
 */
public final class RuminaqDiagramUtil {

  private RuminaqDiagramUtil() {
    // Only static methods class
  }

  private static boolean isInTestDirectory(IFile file) {
    return isInTestDirectory(file.getProjectRelativePath().segments());
  }

  private static boolean isInTestDirectory(URI uri) {
    if (uri.isPlatform()) {
      return isInTestDirectory(
          uri.segmentsList().stream().skip(2).toArray(String[]::new));
    } else {
      return isInTestDirectory(
          uri.segmentsList().stream().skip(1).toArray(String[]::new));
    }
  }

  private static boolean isInTestDirectory(String... segments) {
    return Stream.of(segments).collect(Collectors.joining("/"))
        .startsWith(SourceFolders.TEST_DIAGRAM_FOLDER);
  }

  private static boolean isInTestDirectory(URI uri, String basePath) {
    String file = uri.toFileString()
        .replace(Stream.of(new FilePath(basePath).getSegments()).collect(
            Collectors.joining(File.separator, File.separator, "")), "");
    String folder = file.replace(basePath, "");
    if (folder.contains(":")) {
      folder = folder.substring(folder.indexOf(':') + 1);
    }
    folder = folder.replace(File.separator, "/");
    return folder.startsWith(SourceFolders.TEST_DIAGRAM_FOLDER);
  }

  public static boolean isTest(URI uri) {
    return CreateDiagramWizard.EXTENSION.equals(uri.fileExtension())
        && isInTestDirectory(uri);
  }

  public static boolean isTest(IFile file) {
    return CreateDiagramWizard.EXTENSION.equals(file.getFileExtension())
        && isInTestDirectory(file);
  }

  public static boolean isTest(URI uri, String basePath) {
    return CreateDiagramWizard.EXTENSION.equals(uri.fileExtension())
        && isInTestDirectory(uri, basePath);
  }
}