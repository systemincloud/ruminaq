/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.api;

import java.util.Collections;
import java.util.List;
import org.apache.maven.model.Dependency;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;

/**
 * Service api extending eclipse integration.
 *
 * @author Marek Jagielski
 */
public interface EclipseExtension {

  /**
   * Other bundles can enrich the eclipse project file after it is created.
   *
   * @param javaProject Eclipse JDT class of java project
   *
   * @return Return false if something went wrong.
   */
  default boolean createProjectWizardPerformFinish(IProject javaProject) {
    return true;
  }

  /**
   * Other bundles can add maven dependencies.
   *
   * @return Return list of dependencies
   */
  default List<Dependency> getMavenDependencies() {
    return Collections.<Dependency>emptyList();
  }

  /**
   * Other bundles can add inputs to classpath file.
   *
   * @param javaProject Eclipse JDT class of java project
   *
   * @return Return list of IClasspathEntry
   */
  default List<IClasspathEntry> getClasspathEntries(IJavaProject javaProject) {
    return Collections.<IClasspathEntry>emptyList();
  }

  /**
   * Notify other bundles that editor is open.
   */
  default void initEditor() {
  }
}
