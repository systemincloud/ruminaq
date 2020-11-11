/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tests.common;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.matchers.WidgetMatcherFactory;
import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.eclipse.finder.waits.WaitForEditor;
import org.eclipse.swtbot.swt.finder.finders.ContextMenuHelper;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.ui.IEditorReference;
import org.hamcrest.Matcher;

/**
 * Create new Ruminaq project.
 *
 * @author Marek Jagielski
 *
 */
public class CreateRuminaqDiagram {

  /**
   * Create new Ruminaq daigram.
   *
   * @param bot         SWTWorkbenchBot
   * @param projectName name of project
   * @param path        destination file path
   * @param diagramName diagram name
   */
  public void execute(SWTWorkbenchBot bot, String projectName, String path,
      String diagramName) {
    openDiagramWizardFromProjectContextMenu(bot, projectName);

    bot.textWithLabel("&Container:").setText(path);

    bot.textWithLabel("&File name:").setText(diagramName + ".rumi");

    bot.activeShell();

    bot.button("Finish").click();
  }

  /**
   * Open wizard.
   *
   * @param bot         SWTWorkbenchBot
   * @param projectName
   * @param dirs        selection file path
   */
  public void openDiagramWizardFromProjectContextMenu(SWTWorkbenchBot bot,
      String projectName, String... dirs) {
    SWTBotTree selector = SelectView.selectInProjectExplorer(bot, projectName,
        dirs);
    SWTBotMenu menu = new SWTBotMenu(ContextMenuHelper.contextMenu(selector,
        new String[] { "New", "Other..." }));
    menu.click();
    bot.tree().getTreeItem("Ruminaq").expand();

    bot.tree().getTreeItem("Ruminaq").getNode("Ruminaq Diagram").select();

    bot.button("Next >").click();
  }

  /**
   * Wait.
   *
   * @param bot         SWTWorkbenchBot
   * @param diagramName diagram name
   */
  public void waitUntilDiagramOpens(SWTWorkbenchBot bot, String diagramName) {
    Matcher<IEditorReference> withPartName = WidgetMatcherFactory
        .withPartName(diagramName);
    WaitForEditor waitForEditor = Conditions.waitForEditor(withPartName);
    bot.waitUntilWidgetAppears(waitForEditor);
  }
}
