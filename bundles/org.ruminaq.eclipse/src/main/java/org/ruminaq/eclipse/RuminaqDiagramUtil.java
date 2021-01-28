/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.ruminaq.eclipse.wizards.diagram.CreateDiagramWizard;
import org.ruminaq.eclipse.wizards.project.CreateSourceFolders;

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
        .startsWith(CreateSourceFolders.TEST_DIAGRAM_FOLDER);
  }

  public static boolean isTest(URI uri) {
    return CreateDiagramWizard.EXTENSION.equals(uri.fileExtension())
        && isInTestDirectory(uri);
  }

  public static boolean isTest(IFile file) {
    return CreateDiagramWizard.EXTENSION.equals(file.getFileExtension())
        && isInTestDirectory(file);
  }
}
