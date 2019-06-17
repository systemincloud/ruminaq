/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.util;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.osgi.framework.util.FilePath;
import org.ruminaq.consts.Constants;
import org.ruminaq.eclipse.wizards.project.SourceFolders;

@SuppressWarnings("restriction")
public class ConstantsUtil {

  public static boolean isInTestDirectory(IFile file) {
    String folder = "";
    String[] segments = file.getProjectRelativePath().segments();
    StringBuilder sb = new StringBuilder("");
    for (int i = 0; i < segments.length; i++)
      sb.append(segments[i]).append("/");
    sb.deleteCharAt(sb.length() - 1);
    folder = sb.toString();
    if (folder.startsWith(SourceFolders.TEST_DIAGRAM_FOLDER))
      return true;
    else
      return false;
  }

  public static boolean isInTestDirectory(URI uri) {
    String folder = "";
    int start = uri.isPlatform() ? 2 : 1;
    String[] segments = uri.segments();
    StringBuilder sb = new StringBuilder("");
    for (int i = start; i < segments.length; i++)
      sb.append(segments[i]).append("/");
    sb.deleteCharAt(sb.length() - 1);
    folder = sb.toString();
    if (folder.startsWith(SourceFolders.TEST_DIAGRAM_FOLDER))
      return true;
    else
      return false;
  }

  public static boolean isInTestDirectory(URI uri, String basePath) {
    String folder = "";
    String[] segments = (new FilePath(basePath)).getSegments();
    StringBuilder sb = new StringBuilder(File.separator);
    for (int i = 0; i < segments.length; i++)
      sb.append(segments[i]).append(File.separator);
    String file = uri.toFileString().replace(sb.toString(), "");
    if (file != null)
      folder = file.replace(basePath, "");
    if (folder.contains(":"))
      folder = folder.substring(folder.indexOf(":") + 1);
    folder = folder.replace(File.separator, "/");
    if (folder.startsWith(SourceFolders.TEST_DIAGRAM_FOLDER))
      return true;
    else
      return false;
  }

  public static boolean isTest(URI uri) {
    if (Constants.EXTENSION.equals(uri.fileExtension())
        && isInTestDirectory(uri))
      return true;
    else
      return false;
  }

  public static boolean isTest(IFile file) {
    if (Constants.EXTENSION.equals(file.getFileExtension())
        && isInTestDirectory(file))
      return true;
    return false;
  }

  public static boolean isTest(URI uri, String basePath) {
    if (Constants.EXTENSION.equals(uri.fileExtension())
        && isInTestDirectory(uri, basePath))
      return true;
    return false;
  }
}
