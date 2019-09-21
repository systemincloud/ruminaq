/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.it.tests;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.maven.shared.utils.io.IOUtil;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ruminaq.tests.common.CreateRuminaqProject;
import org.ruminaq.tests.common.SelectView;

/**
 * Test of creating a new eclipse project.
 *
 * @author Marek Jagielski
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class CreateRuminaqProjectFailedTest {

  private static final String LOG_DIR = "target/logs/";

  private static SWTWorkbenchBot bot;

  /**
   * Initialize SWTBot.
   *
   */
  @BeforeClass
  public static void initBot() {
    new File(LOG_DIR).mkdirs();
    bot = new SWTWorkbenchBot();
    SelectView.closeWelcomeViewIfExists(bot);

  }

  @AfterClass
  public static void after() {
    bot.resetWorkbench();
  }

  private static final int PROJECT_SUFFIX_LENGTH = 5;

  @Test
  public final void testCreateProjectFailed() throws IOException {
    String projectName = "test"
        + RandomStringUtils.randomAlphabetic(PROJECT_SUFFIX_LENGTH);

    System.setProperty(
        JavaProjectAspect.FAIL_CREATE_OUTPUT_LOCATION_PROJECT_NAME,
        projectName);

    String logFilePath = "target/logs/" + RandomStringUtils
        .randomAlphabetic(PROJECT_SUFFIX_LENGTH) + ".log";

    File logFile = new File(logFilePath);
    logFile.createNewFile();

    System.setProperty(LoggerAspect.FILE_PATH, logFilePath);
    new CreateRuminaqProject().execute(bot, projectName);
    new CreateRuminaqProject().acceptPerspectiveChangeIfPopUps(bot);

    SWTBotShell failureWindow = bot.shell("Ruminaq failure");
    failureWindow.activate();

    Assert.assertNotNull("Failure window should appear", failureWindow);

    bot.button("OK").click();
    System.setProperty(LoggerAspect.FILE_PATH, "");

    Assert.assertEquals("Check logs",
        IOUtil
            .toString(CreateRuminaqProjectFailedTest.class.getResourceAsStream(
                CreateRuminaqProjectFailedTest.class.getSimpleName() + ".log")),
            new String(Files.readAllBytes(Paths.get(logFilePath))));
    ;
  }
}
