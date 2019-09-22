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
import org.ruminaq.tests.common.CreateRuminaqDiagram;
import org.ruminaq.tests.common.CreateRuminaqProject;
import org.ruminaq.tests.common.SelectView;

/**
 * Test of creating a new eclipse project.
 *
 * @author Marek Jagielski
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class CreateRuminaqDiagramFailedTest {

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

  @AfterClass
  public static void after() {
    bot.resetWorkbench();
  }

  private static final int SUFFIX_LENGTH = 5;

  @Test
  public final void testCreateDiagramFailed() {
    String projectName = "test"
        + RandomStringUtils.randomAlphabetic(SUFFIX_LENGTH);

    new CreateRuminaqProject().execute(bot, projectName);
    new CreateRuminaqProject().acceptPerspectiveChangeIfPopUps(bot);

    String path = SourceFolders.DIAGRAM_FOLDER;

    String diagramName = "Diagram_"
        + RandomStringUtils.randomAlphabetic(SUFFIX_LENGTH);
//    System.setProperty(
//        CreateDiagramWizardAspect.FAIL_OPEN_EDITOR_FILE_NAME,
//        diagramName);
    new CreateRuminaqDiagram().execute(bot, projectName, path, diagramName);

//    SWTBotShell failureWindow = bot.shell("Ruminaq failure");
//    failureWindow.activate();

//    Assert.assertNotNull("Failure window should appear", failureWindow);
//
//    bot.button("OK").click();
  }
}
