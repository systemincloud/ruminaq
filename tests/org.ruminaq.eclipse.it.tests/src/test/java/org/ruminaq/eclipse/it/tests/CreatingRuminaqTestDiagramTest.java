/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.it.tests;

import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.waits.ICondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.junit.AfterClass;
import org.junit.Assert;
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
  public final void testChoosingProjectFromMainMenu() {
    String projectName = "test"
        + RandomStringUtils.randomAlphabetic(PROJECT_SUFFIX_LENGTH);
    new CreateRuminaqProject().execute(bot, projectName);
    new CreateRuminaqProject().acceptPerspectiveChangeIfPopUps(bot);

    String folder = "modules";

    bot.tree().getTreeItem(projectName).expand();
    bot.tree().getTreeItem(projectName).getNode(SourceFolders.TEST_RESOURCES)
        .select();
    bot.tree().getTreeItem(projectName).getNode(SourceFolders.TEST_RESOURCES)
        .expand();
    bot.tree().getTreeItem(projectName).getNode(SourceFolders.TEST_RESOURCES)
        .getNode(SourceFolders.TASK_FOLDER).select();
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

    new CreateRuminaqTestDiagram().openDiagramWizardFromProjectContextMenu(bot,
        projectName, SourceFolders.TEST_RESOURCES, SourceFolders.TASK_FOLDER,
        folder);

    Assert.assertEquals("Window of title should be set", "New Test Diagram",
        bot.activeShell().getText());

    bot.textWithLabel("New Test Diagram");
    bot.text("This wizard creates a new Ruminaq Diagram Test.");

    bot.textWithLabel("&Project:").setText("");

    SWTBotText msg = bot.text(" Project must be specified");

    Assert.assertNotNull(msg);

    bot.button("Browse...").click();

    bot.tree().getTreeItem(projectName).select();
    bot.tree().getTreeItem(projectName).expand();
    bot.button("OK").click();

    bot.textWithLabel("&Container:").setText("");

    msg = bot.text(" File container must be specified");

    Assert.assertNotNull(msg);

    bot.textWithLabel("&Container:")
        .setText("src/test/resources/tasks/notexisting");

    msg = bot.text(" File container must exist");

    Assert.assertNotNull(msg);

    bot.button("Browse...", 1).click();

    bot.tree().getTreeItem(SourceFolders.TASK_FOLDER).select();
    bot.tree().getTreeItem(SourceFolders.TASK_FOLDER).expand();
    bot.tree().getTreeItem(SourceFolders.TASK_FOLDER).getNode(folder).select();

    bot.button("OK").click();

    Assert.assertEquals("Container should be set",
        "src/test/resources/tasks/modules",
        bot.textWithLabel("&Container:").getText());

    bot.textWithLabel("&File name:").setText("");

    msg = bot.text(" File name must be specified");

    bot.textWithLabel("&File name:").setText("Diagram.txt");

    msg = bot.text(" File extension must be rumi");

    Assert.assertNotNull(msg);
  }
}
