/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.it.tests;

import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ruminaq.eclipse.wizards.project.CreateSourceFolders;
import org.ruminaq.tests.common.CreateRuminaqProject;
import org.ruminaq.tests.common.CreateRuminaqTestDiagram;
import org.ruminaq.tests.common.SelectView;

/**
 * Test of creating a new eclipse project.
 *
 * @author Marek Jagielski
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class CreatingRuminaqTestDiagramTest {

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
  public final void testChoosingProjectFromMainMenu()
      throws InterruptedException {
    String projectName = "test"
        + RandomStringUtils.randomAlphabetic(PROJECT_SUFFIX_LENGTH);
    new CreateRuminaqProject().execute(bot, projectName);
    new CreateRuminaqProject().acceptPerspectiveChangeIfPopUps(bot);

    Thread.sleep(6000);

    String folder = "modules";

    bot.tree().getTreeItem(projectName).expand();
    bot.tree().getTreeItem(projectName)
        .getNode(CreateSourceFolders.TEST_RESOURCES).select();
    bot.tree().getTreeItem(projectName)
        .getNode(CreateSourceFolders.TEST_RESOURCES).expand();
    bot.tree().getTreeItem(projectName)
        .getNode(CreateSourceFolders.TEST_RESOURCES)
        .getNode(CreateSourceFolders.TASK_FOLDER).select();
    SWTBotMenu menu = bot.tree().contextMenu("New");
    bot.waitUntil(new DefaultCondition() {

      @Override
      public boolean test() throws Exception {
        return menu.hasMenu();
      }

      @Override
      public String getFailureMessage() {
        return "Menu not visible";
      }
    });
    bot.menu("Folder").click();
    bot.textWithLabel("Folder &name:").setText(folder);
    bot.button("Finish").click();

    Thread.sleep(3000);

    bot.tree().getTreeItem(projectName).contextMenu("Refresh");

    Thread.sleep(3000);

    new CreateRuminaqTestDiagram().openDiagramWizardFromProjectContextMenu(bot,
        projectName, CreateSourceFolders.TEST_RESOURCES,
        CreateSourceFolders.TASK_FOLDER, folder);

    Assert.assertEquals("Window of title should be set", "New Test Diagram",
        bot.activeShell().getText());

    bot.textWithLabel("&Project:").setText("");

    Assert.assertFalse("Can't create project when name is empty",
        bot.button("Finish").isEnabled());
    String msg = bot.label(" Project must be specified").getText();
    Assert.assertNotNull(msg);

    bot.button("Browse...").click();

    bot.tree().getTreeItem(projectName).select();
    bot.tree().getTreeItem(projectName).expand();
    bot.button("OK").click();

    bot.textWithLabel("&Container:").setText("");

    msg = bot.label(" File container must be specified").getText();

    Assert.assertNotNull(msg);

    bot.textWithLabel("&Container:")
        .setText("src/test/resources/tasks/notexisting");

    msg = bot.label(" File container must exist").getText();

    Assert.assertNotNull(msg);

    bot.button("Browse...", 1).click();

    bot.tree().getTreeItem(CreateSourceFolders.TASK_FOLDER).select();
    bot.tree().getTreeItem(CreateSourceFolders.TASK_FOLDER).expand();
    bot.tree().getTreeItem(CreateSourceFolders.TASK_FOLDER).getNode(folder)
        .select();

    bot.button("OK").click();

    Assert.assertEquals("Container should be set",
        "src/test/resources/tasks/modules",
        bot.textWithLabel("&Container:").getText());

    bot.textWithLabel("&File name:").setText("");

    msg = bot.label(" File name must be specified").getText();

    bot.textWithLabel("&File name:").setText("Diagram.txt");

    msg = bot.label(" File extension must be rumi").getText();

    Assert.assertNotNull(msg);
  }
}
