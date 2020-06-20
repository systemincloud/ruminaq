/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.javatask.it.tests;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.matchers.WidgetMatcherFactory;
import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.eclipse.finder.waits.WaitForEditor;
import org.eclipse.swtbot.swt.finder.finders.ContextMenuHelper;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.ui.IEditorReference;
import org.hamcrest.Matcher;
import org.ruminaq.tests.common.SelectView;

/**
 * Create new JavaTask.
 *
 * @author Marek Jagielski
 *
 */
public class CreateJavaTask {

  /**
   * Create new JavaTask class.
   *
   * @param bot         SWTWorkbenchBot
   * @param name        name of project
   * @param diagramName
   * @param path
   */
  public void execute(SWTWorkbenchBot bot, String projectName, String path,
      String diagramName) {
    openJavaTaskWizardFromProjectContextMenu(bot, projectName);

    bot.textWithLabel("&Container:").setText(path);

    bot.textWithLabel("&File name:").setText(diagramName + ".rumi");

    bot.activeShell();

    bot.button("Finish").click();
  }

  public void openJavaTaskWizardFromProjectContextMenu(SWTWorkbenchBot bot,
      String projectName, String... dirs) {
    SWTBotTree selector = SelectView.selectInProjectExplorer(bot, projectName,
        dirs);
    SWTBotMenu menu = new SWTBotMenu(ContextMenuHelper.contextMenu(selector,
        new String[] { "New", "Other..." }));
    menu.click();
    bot.tree().getTreeItem("Ruminaq").expand();
    bot.tree().getTreeItem("Ruminaq").getNode("Commons").expand();


    bot.tree().getTreeItem("Ruminaq").getNode("Commons").getNode("Java Task Class").select();

    bot.button("Next >").click();
  }

  public void waitUntilDiagramOpens(SWTWorkbenchBot bot, String diagramName) {
    Matcher<IEditorReference> withPartName = WidgetMatcherFactory
        .withPartName(diagramName);
    WaitForEditor waitForEditor = Conditions.waitForEditor(withPartName);
    bot.waitUntilWidgetAppears(waitForEditor);
  }
}
