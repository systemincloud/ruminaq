/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.wizards.project;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.m2e.jdt.IClasspathManager;
import org.ruminaq.eclipse.Messages;
import org.ruminaq.eclipse.RuminaqException;
import org.ruminaq.eclipse.api.EclipseExtension;
import org.ruminaq.logs.ModelerLoggerFactory;
import org.ruminaq.util.ServiceUtil;
import org.slf4j.Logger;

/**
 * Writes to .classpath file when new project is created. This configuration can
 * be extended by tasks or extensions.
 *
 * @author Marek Jagielski
 */
public final class JavaClasspathFile {

  private static final Logger LOGGER = ModelerLoggerFactory
      .getLogger(JavaClasspathFile.class);

  private static final String JAVA_PATH = "**/*.java"; //$NON-NLS-1$

  private static final String TARGET_PATH = "target/test-classes"; //$NON-NLS-1$

  private Collection<EclipseExtension> extensions = ServiceUtil
      .getServicesAtLatestVersion(JavaClasspathFile.class,
          EclipseExtension.class);

  /**
   * Writes to .classpath file when new project is created.
   *
   * @param javaProject Eclipse IJavaProject reference
   * @throws RuminaqException something went wrong
   */
  public void setClasspathEntries(IJavaProject javaProject) throws RuminaqException {
    List<IClasspathEntry> entries = new LinkedList<>();
    IPath[] javaPath = new IPath[] { new Path(JAVA_PATH) };
    IPath testOutputLocation = javaProject.getPath().append(TARGET_PATH);
//
//    entries.addAll(
//        extensions
//          .stream()
//          .<List<IClasspathEntry>>map(e -> e.getClasspathEntries(javaProject))
//          .<IClasspathEntry>flatMap(List::stream)
//          .collect(Collectors.toList()));

    entries.add(JavaCore.newSourceEntry(
        javaProject.getPath().append(SourceFolders.MAIN_RESOURCES), javaPath));
    entries.add(JavaCore.newSourceEntry(
        javaProject.getPath().append(SourceFolders.TEST_RESOURCES), javaPath,
        testOutputLocation));

    entries.add(JavaRuntime.getDefaultJREContainerEntry());
    entries.add(JavaCore.newContainerEntry(
        new Path(IClasspathManager.CONTAINER_ID)));

    try {
      javaProject.setRawClasspath(
          entries.toArray(new IClasspathEntry[entries.size()]), null);
    } catch (JavaModelException e) {
      LOGGER.error(Messages.createClasspathFileFailed, e);
      throw new RuminaqException(Messages.createProjectWizardFailed);
    }
  }
}
