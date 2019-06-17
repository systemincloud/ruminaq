/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.wizards.project;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.ruminaq.eclipse.Messages;
import org.ruminaq.eclipse.RuminaqException;
import org.ruminaq.util.EclipseUtil;

/**
 * Creates directories for eclipse sources.
 *
 * @author Marek Jagielski
 */
public final class SourceFolders {

  public static final String MAIN_RESOURCES = "src/main/resources"; //$NON-NLS-1$
  public static final String TEST_RESOURCES = "src/test/resources"; //$NON-NLS-1$
  public static final String TASK_FOLDER = "tasks"; //$NON-NLS-1$
  public static final String DIAGRAM_FOLDER = MAIN_RESOURCES + "/" + TASK_FOLDER
      + "/"; //$NON-NLS-1$
  public static final String TEST_DIAGRAM_FOLDER = TEST_RESOURCES + "/"
      + TASK_FOLDER + "/"; //$NON-NLS-1$

  private SourceFolders() {
  }

  /**
   * Creates directories for eclipse sources.
   *
   * @param project Eclipse IProject reference
   * @throws RuminaqException something went wrong
   */
  static void createSourceFolders(IProject project) throws RuminaqException {
    try {
      EclipseUtil.createFolderWithParents(project, MAIN_RESOURCES);
      EclipseUtil.createFolderWithParents(project, DIAGRAM_FOLDER);
      EclipseUtil.createFolderWithParents(project, TEST_RESOURCES);
      EclipseUtil.createFolderWithParents(project, TEST_DIAGRAM_FOLDER);
    } catch (CoreException e) {
      throw new RuminaqException(Messages.createProjectWizardFailed, e);
    }
  }
}
