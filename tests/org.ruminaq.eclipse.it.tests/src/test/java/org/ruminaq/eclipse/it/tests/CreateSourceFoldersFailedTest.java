/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.it.tests;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ruminaq.tests.common.SelectView;

/**
 * Test of creating a new eclipse project.
 *
 * @author Marek Jagielski
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class CreateSourceFoldersFailedTest {

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

  private static final int PROJECT_SUFFIX_LENGTH = 5;

  @Test
  public final void testCreateProjectFailed() {
//    String projectName = "test"
//        + RandomStringUtils.randomAlphabetic(PROJECT_SUFFIX_LENGTH);
//    System.setProperty(
//        EclipseUtilAspect.FAIL_CREATE_SOURCE_FOLDERS_PROJECT_NAME,
//        projectName);
//    new CreateRuminaqProject().execute(bot, projectName);
//    new CreateRuminaqProject().acceptPerspectiveChangeIfPopUps(bot);
//
//    SWTBotShell failureWindow = bot.shell("Ruminaq failure");
//    failureWindow.activate();
//
//    Assert.assertNotNull("Could not create source folders", failureWindow);
//
//    bot.button("OK").click();
  }
}
