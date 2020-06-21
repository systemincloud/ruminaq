/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.javatask.gui.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.ruminaq.tasks.javatask.gui.EclipseExtensionImpl;
import org.ruminaq.util.EclipseUtil;

public class CreateProjectWizard {

  public boolean performFinish(IJavaProject newProject) throws CoreException {
    createSourceFolders(newProject.getProject());
    return true;
  }

  private void createSourceFolders(IProject project) throws CoreException {
    EclipseUtil.createFolderWithParents(project,
        EclipseExtensionImpl.MAIN_JAVA);
    EclipseUtil.createFileInFolder(project, EclipseExtensionImpl.MAIN_JAVA,
        "PLACEHOLDER_FOR_JAVA");
    EclipseUtil.createFolderWithParents(project,
        EclipseExtensionImpl.TEST_JAVA);
    EclipseUtil.createFileInFolder(project, EclipseExtensionImpl.TEST_JAVA,
        "PLACEHOLDER_FOR_JAVA");
  }
}
