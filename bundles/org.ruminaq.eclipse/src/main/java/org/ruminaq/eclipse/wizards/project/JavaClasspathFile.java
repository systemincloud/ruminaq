/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.wizards.project;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;
import org.osgi.service.component.annotations.Reference;
import org.ruminaq.consts.Constants;
import org.ruminaq.eclipse.api.EclipseExtensionHandler;

/**
 * Writes to .classpath file when new project is created. This configuration can
 * be extended by tasks or extensions.
 *
 * @author Marek Jagielski
 */
public final class JavaClasspathFile {

  @Reference
  private EclipseExtensionHandler extensions;

  /**
   * Writes to .classpath file when new project is created.
   *
   * @param javaProject Eclipse IJavaProject reference
   */
  public void setClasspathEntries(IJavaProject javaProject)
      throws JavaModelException {
    List<IClasspathEntry> entries = new LinkedList<>();
    IPath[] javaPath = new IPath[] { new Path("**/*.java") };
    IPath testOutputLocation = javaProject.getPath()
        .append("target/test-classes");

    entries.addAll(extensions.createClasspathEntries(javaProject));

    entries.add(JavaCore.newSourceEntry(
        javaProject.getPath().append(SourceFolders.MAIN_RESOURCES), javaPath));
    entries.add(JavaCore.newSourceEntry(
        javaProject.getPath().append(SourceFolders.TEST_RESOURCES), javaPath,
        testOutputLocation));

    entries.add(JavaRuntime.getDefaultJREContainerEntry());
    entries.add(
        JavaCore.newContainerEntry(new Path(Constants.MAVEN_CONTAINER_ID)));

    javaProject.setRawClasspath(
        entries.toArray(new IClasspathEntry[entries.size()]), null);
  }
}
