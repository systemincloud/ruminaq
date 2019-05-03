/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.api;

import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.javatuples.Triplet;

/**
 *
 * @author Marek Jagielski
 */
public interface EclipseExtension {

  /**
   * Other bundles can enrich the project after it is created.
   *
   * @param javaProject Eclipse JDT class of java project
   *
   * @return Return false if something went wrong.
   */
  default boolean createProjectWizardPerformFinish(IJavaProject javaProject) {
    return true;
  }

  default List<Triplet<String, String, String>> getMavenDependencies() {
    return Collections.<Triplet<String, String, String>>emptyList();
  }

  default List<IClasspathEntry> createClasspathEntries(
      IJavaProject javaProject) {
    return Collections.<IClasspathEntry>emptyList();
  }

  default void initEditor() {
  }
}
