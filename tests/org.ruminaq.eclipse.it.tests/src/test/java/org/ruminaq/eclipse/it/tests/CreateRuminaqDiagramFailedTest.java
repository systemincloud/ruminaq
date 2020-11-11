/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.it.tests;

import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellCloses;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.commons.io.FilenameUtils;
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

  private static final int SUFFIX_LENGTH = 5;

  @Test
  public final void testCreateDiagramFailed()
      throws IOException, InterruptedException {

    String logFilePath = LOG_DIR + FilenameUtils
        .getName(RandomStringUtils.randomAlphabetic(SUFFIX_LENGTH) + ".log");

    File logFile = new File(logFilePath);
    logFile.createNewFile();

    System.setProperty(LoggerAspect.FILE_PATH, logFilePath);

    String projectName = "test"
        + RandomStringUtils.randomAlphabetic(SUFFIX_LENGTH);

    new CreateRuminaqProject().execute(bot, projectName);
    new CreateRuminaqProject().acceptPerspectiveChangeIfPopUps(bot);

    String path = SourceFolders.DIAGRAM_FOLDER;

    String diagramName = "Diagram_"
        + RandomStringUtils.randomAlphabetic(SUFFIX_LENGTH);
    System.setProperty(CreateDiagramWizardAspect.FAIL_OPEN_EDITOR_FILE_NAME,
        diagramName);
    new CreateRuminaqDiagram().execute(bot, projectName, path, diagramName);

    Thread.sleep(5000);

    SWTBotShell failureWindow = bot.shell("Ruminaq failure");
    failureWindow.activate();

    Assert.assertNotNull("Failure window should appear", failureWindow);

    bot.button("OK").click();

    bot.waitUntil(shellCloses(failureWindow), 10000);

    System.setProperty(LoggerAspect.FILE_PATH, "");

    Assert.assertTrue("Check logs",
        new String(Files.readAllBytes(Paths.get(logFilePath))).startsWith(IOUtil
            .toString(CreateRuminaqDiagramFailedTest.class.getResourceAsStream(
                CreateRuminaqDiagramFailedTest.class.getSimpleName()
                    + ".log"))));
  }
}
