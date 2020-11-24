/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.javatask.it.tests;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
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
public class CreatingJavaTaskTest {

  private static SWTWorkbenchBot bot;
  private static IWorkspace workspace;

  /**
   * Initialize SWTBot.
   *
   */
  @BeforeClass
  public static void initBot() {
    bot = new SWTWorkbenchBot();
    workspace = ResourcesPlugin.getWorkspace();
    SelectView.closeWelcomeViewIfExists(bot);
  }

  @AfterClass
  public static void after() {
    bot.resetWorkbench();
  }

  private static final int PROJECT_SUFFIX_LENGTH = 5;

  @Test
  public final void testCreateJavaTaskFromContextMenu()
      throws InterruptedException {
    String projectName = "test"
        + RandomStringUtils.randomAlphabetic(PROJECT_SUFFIX_LENGTH);
    new CreateRuminaqProject().execute(bot, projectName);
    new CreateRuminaqProject().acceptPerspectiveChangeIfPopUps(bot);

    Thread.sleep(5000);

    new CreateJavaTask().openJavaTaskWizardFromProjectContextMenu(bot,
        projectName);

    Assert.assertEquals("Window title should be set", "New Java Class",
        bot.activeShell().getText());

    bot.textWithLabel("Package:").setText("test");
    bot.textWithLabel("Name:").setText("First");

    bot.button("Finish").click();

    Thread.sleep(3000);

    Assert.assertEquals("Should open java editor with title", "First.java",
        bot.activeEditor().getTitle());
  }

  @Test
  public final void testJavaTaskOptions()
      throws InterruptedException, CoreException {
    String projectName = "test"
        + RandomStringUtils.randomAlphabetic(PROJECT_SUFFIX_LENGTH);
    new CreateRuminaqProject().execute(bot, projectName);
    new CreateRuminaqProject().acceptPerspectiveChangeIfPopUps(bot);

    Thread.sleep(5000);

    new CreateJavaTask().openJavaTaskWizardFromProjectContextMenu(bot,
        projectName);

    bot.textWithLabel("Package:").setText("test");
    bot.textWithLabel("Name:").setText("NonAtomic");

    bot.button("Next >").click();

    bot.checkBox("atomic").click();

    Thread.sleep(1000);

    bot.button("Finish").click();

    Thread.sleep(3000);

    Assert.assertEquals("Java class created",
        toString(this.getClass().getResourceAsStream("NonAtomic.javatest")),
        toString(workspace.getRoot().getProject(projectName)
            .getFile("src/main/java/test/NonAtomic.java").getContents()));

    new CreateJavaTask().openJavaTaskWizardFromProjectContextMenu(bot,
        projectName);

    bot.textWithLabel("Package:").setText("test");
    bot.textWithLabel("Name:").setText("NonAtomicGeneratorExternalSource");

    bot.button("Next >").click();

    bot.checkBox("atomic").click();
    bot.checkBox("generator").click();
    bot.checkBox("external source").click();
    bot.checkBox("runnerStart").click();
    bot.checkBox("runnerStop").click();

    Thread.sleep(1000);

    bot.button("Finish").click();

    Thread.sleep(3000);

    Assert.assertEquals("Java class created",
        toString(this.getClass()
            .getResourceAsStream("NonAtomicGeneratorExternalSource.javatest")),
        toString(workspace.getRoot().getProject(projectName)
            .getFile("src/main/java/test/NonAtomicGeneratorExternalSource.java")
            .getContents()));

    new CreateJavaTask().openJavaTaskWizardFromProjectContextMenu(bot,
        projectName);

    bot.textWithLabel("Package:").setText("test");
    bot.textWithLabel("Name:").setText("Constant");

    bot.button("Next >").click();

    bot.checkBox("constant").click();

    Thread.sleep(1000);

    bot.button("Finish").click();

    Thread.sleep(3000);

    Assert.assertEquals("Java class created",
        toString(this.getClass().getResourceAsStream("Constant.javatest")),
        toString(workspace.getRoot().getProject(projectName)
            .getFile("src/main/java/test/Constant.java").getContents()));

    new CreateJavaTask().openJavaTaskWizardFromProjectContextMenu(bot,
        projectName);

    bot.textWithLabel("Package:").setText("test");
    bot.textWithLabel("Name:").setText("Ports");

    bot.button("Next >").click();

    bot.textWithLabel("Name:", 0).setText("a");
    bot.button("Add", 0).click();
    bot.table(0).select(0);
    bot.button("Remove", 0).click();

    bot.textWithLabel("Name:", 0).setText("a");
    bot.button("Add", 0).click();

    bot.textWithLabel("Name:", 0).setText("b");
    bot.comboBox(0).setSelection("Complex32");
    bot.button("Add", 0).click();

    bot.textWithLabel("Name:", 0).setText("c");
    bot.checkBox("asynchronous").click();
    bot.button("Add", 0).click();
    bot.checkBox("asynchronous").click();

    bot.textWithLabel("Name:", 0).setText("d");
    bot.checkBox("hold last data").click();
    bot.button("Add", 0).click();

    bot.textWithLabel("Name:", 0).setText("e");
    bot.comboBox(0).setSelection("Complex32");
    bot.spinner(0).setSelection(1);
    bot.button("Add", 0).click();
    bot.spinner(0).setSelection(-1);

    bot.textWithLabel("Name:", 0).setText("f");
    bot.spinner(1).setSelection(3);
    bot.button("Add", 0).click();

    bot.textWithLabel("Name:", 0).setText("g");
    bot.checkBox("inf").click();
    bot.button("Add", 0).click();

    bot.table(0).getTableItem(5).dragAndDrop(bot.table(0).getTableItem(6));

    bot.textWithLabel("Name:", 1).setText("h");
    bot.button("Add", 1).click();
    bot.table(1).select(0);
    bot.button("Remove", 1).click();

    bot.textWithLabel("Name:", 1).setText("h");
    bot.comboBox(1).setSelection("Complex32");
    bot.button("Add", 1).click();
    
    bot.textWithLabel("Name:", 1).setText("i");
    bot.comboBox(1).setSelection("Complex32");
    bot.button("Add", 1).click();

    bot.table(1).getTableItem(0).dragAndDrop(bot.table(1).getTableItem(1));

    Thread.sleep(1000);

    bot.button("Finish").click();

    Thread.sleep(3000);

    Assert.assertEquals("Java class created",
        toString(this.getClass().getResourceAsStream("Ports.javatest")),
        toString(workspace.getRoot().getProject(projectName)
            .getFile("src/main/java/test/Ports.java").getContents()));

    new CreateJavaTask().openJavaTaskWizardFromProjectContextMenu(bot,
        projectName);

    bot.textWithLabel("Package:").setText("test");
    bot.textWithLabel("Name:").setText("Parameters");

    bot.button("Next >").click();

    bot.textWithLabel("Name:", 2).setText("toRemove");
    bot.button("Add", 2).click();

    bot.textWithLabel("Name:", 2).setText("x");
    bot.textWithLabel("Default value:", 0).setText("0");
    bot.button("Add", 2).click();

    bot.textWithLabel("Name:", 2).setText("x");
    bot.textWithLabel("Name:", 2).setText("y");
    bot.button("Add", 2).click();

    bot.table(2).select(0);
    bot.button("Remove", 2).click();

    bot.button("Finish").click();

    Thread.sleep(3000);

    Assert.assertEquals("Java class created",
        toString(this.getClass().getResourceAsStream("Parameters.javatest")),
        toString(workspace.getRoot().getProject(projectName)
            .getFile("src/main/java/test/Parameters.java").getContents()));
  }

  private static String toString(InputStream stream) {
    return new BufferedReader(new InputStreamReader(stream)).lines()
        .collect(Collectors.joining("\n"));
  }
}
