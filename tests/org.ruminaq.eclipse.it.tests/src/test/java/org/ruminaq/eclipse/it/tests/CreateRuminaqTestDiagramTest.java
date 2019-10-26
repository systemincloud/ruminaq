/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.it.tests;

import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ruminaq.eclipse.wizards.project.SourceFolders;
import org.ruminaq.tests.common.CreateRuminaqProject;
import org.ruminaq.tests.common.CreateRuminaqTestDiagram;
import org.ruminaq.tests.common.SelectView;

/**
 * Test of creating a new eclipse project.
 *
 * @author Marek Jagielski
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class CreateRuminaqTestDiagramTest {

  private static SWTWorkbenchBot bot;

  /**
   * Initialize SWTBot.
   *
   */
  @BeforeClass
  public static void initBot() {
    bot = new SWTWorkbenchBot();
    SelectView.closeWelcomeViewIfExists(bot);
  }

  /**
   * Open new project wizard File => New.
   *
   * @param bot  SWTWorkbenchBot
   * @param name name of project
   */
  @AfterClass
  public static void after() {
    bot.resetWorkbench();
  }

  private static final int PROJECT_SUFFIX_LENGTH = 5;
  private static final int DIAGRAM_SUFFIX_LENGTH = 5;

  @Test
  public final void testCreateDiagram() throws InterruptedException {
    String projectName = "test"
        + RandomStringUtils.randomAlphabetic(PROJECT_SUFFIX_LENGTH);
    new CreateRuminaqProject().execute(bot, projectName);
    new CreateRuminaqProject().acceptPerspectiveChangeIfPopUps(bot);

    Thread.sleep(5000);

    String path = SourceFolders.TEST_DIAGRAM_FOLDER;

    String diagramName = "Diagram_"
        + RandomStringUtils.randomAlphabetic(DIAGRAM_SUFFIX_LENGTH);
    new CreateRuminaqTestDiagram().execute(bot, projectName, path, diagramName);

    Thread.sleep(5000);

    new CreateRuminaqTestDiagram().waitUntilDiagramOpens(bot, diagramName);
  }
}
