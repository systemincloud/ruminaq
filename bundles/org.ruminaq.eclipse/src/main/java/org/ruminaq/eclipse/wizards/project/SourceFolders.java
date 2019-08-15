/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.wizards.project;

import java.util.Arrays;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.ruminaq.eclipse.Messages;
import org.ruminaq.eclipse.RuminaqException;
import org.ruminaq.eclipse.RuminaqRuntimeException;
import org.ruminaq.util.EclipseUtil;

/**
 * Creates directories for eclipse sources.
 *
 * @author Marek Jagielski
 */
public final class SourceFolders {

  public static final String MAIN_RESOURCES = "src/main/resources";
  public static final String TEST_RESOURCES = "src/test/resources";
  public static final String TASK_FOLDER = "tasks";
  public static final String DIAGRAM_FOLDER = MAIN_RESOURCES + "/" + TASK_FOLDER
      + "/";
  public static final String TEST_DIAGRAM_FOLDER = TEST_RESOURCES + "/"
      + TASK_FOLDER + "/";

  private SourceFolders() {
  }

  /**
   * Creates directories for eclipse sources.
   *
   * @param project Eclipse IProject reference
   * @throws RuminaqException
   */
  static void createSourceFolders(IProject project) throws RuminaqException {
    try {
      Arrays.asList(MAIN_RESOURCES, TEST_RESOURCES, TASK_FOLDER, DIAGRAM_FOLDER)
          .stream().forEach((String f) -> {
            try {
              EclipseUtil.createFolderWithParents(project, f);
            } catch (CoreException e) {
              throw new RuminaqRuntimeException(e);
            }
          });
    } catch (RuminaqRuntimeException e) {
      throw new RuminaqException(
          Messages.createProjectWizardFailedSourceFolders, e);
    }
  }
}
