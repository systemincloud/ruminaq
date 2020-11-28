/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.wizards.project;

import java.util.Arrays;
import org.eclipse.core.resources.IProject;
import org.ruminaq.eclipse.EclipseUtil;
import org.ruminaq.eclipse.Messages;
import org.ruminaq.eclipse.RuminaqException;
import org.ruminaq.logs.ModelerLoggerFactory;
import org.ruminaq.util.Try;
import org.slf4j.Logger;

/**
 * Creates directories for eclipse sources.
 *
 * @author Marek Jagielski
 */
public final class CreateSourceFolders {

  private static final Logger LOGGER = ModelerLoggerFactory
      .getLogger(CreateSourceFolders.class);

  public static final String SRC = "src";
  public static final String MAIN = "main";
  public static final String TEST = "test";
  public static final String RESOURCES = "resources";
  public static final String MAIN_RESOURCES = SRC + "/" + MAIN + "/"
      + RESOURCES;
  public static final String TEST_RESOURCES = SRC + "/" + TEST + "/"
      + RESOURCES;
  public static final String TASK_FOLDER = "tasks";
  public static final String DIAGRAM_FOLDER = MAIN_RESOURCES + "/"
      + TASK_FOLDER;
  public static final String TEST_DIAGRAM_FOLDER = TEST_RESOURCES + "/"
      + TASK_FOLDER;

  private CreateSourceFolders() {
  }

  /**
   * Creates directories for eclipse sources.
   *
   * @param project Eclipse IProject reference
   * @return Try optionally with RuminaqException 
   */
  public static Try<RuminaqException> execute(IProject project) {
    return Arrays
        .asList(MAIN_RESOURCES, TEST_RESOURCES, DIAGRAM_FOLDER,
            TEST_DIAGRAM_FOLDER)
        .stream().map(f -> EclipseUtil.createFolderWithParents(project, f))
        .filter(Try::isFailed)
        .peek(r -> LOGGER.error(
            Messages.createProjectWizardFailedSourceFolders, r.getError()))
        .findAny()
        .map(r -> r.wrapError(e -> new RuminaqException(
            Messages.createProjectWizardFailedSourceFolders, e)))
        .orElseGet(Try::success);
  }
}
