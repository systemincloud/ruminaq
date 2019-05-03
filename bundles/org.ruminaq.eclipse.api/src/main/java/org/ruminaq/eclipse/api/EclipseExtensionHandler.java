/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.api;

import java.util.List;

import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.javatuples.Triplet;

/**
 *
 * @author Marek Jagielski
 */
public interface EclipseExtensionHandler {

  boolean createProjectWizardPerformFinish(IJavaProject javaProject);

  List<Triplet<String, String, String>> getMavenDependencies();

  List<IClasspathEntry> createClasspathEntries(IJavaProject javaProject);

  void initEditor();
}
