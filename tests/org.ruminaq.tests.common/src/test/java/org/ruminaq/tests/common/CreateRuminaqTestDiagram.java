/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tests.common;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.finders.ContextMenuHelper;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;

/**
 * Create new Ruminaq project.
 *
 * @author Marek Jagielski
 *
 */
public class CreateRuminaqTestDiagram extends CreateRuminaqDiagram {

  @Override
  public void openDiagramWizardFromProjectContextMenu(SWTWorkbenchBot bot,
      String projectName, String... dirs) {
    SWTBotTree selector = SelectView.selectInProjectExplorer(bot, projectName,
        dirs);
    SWTBotMenu menu = new SWTBotMenu(ContextMenuHelper.contextMenu(selector,
        new String[] { "New", "Other..." }));
    menu.click();
    bot.tree().getTreeItem("Ruminaq").expand();

    bot.tree().getTreeItem("Ruminaq").getNode("Ruminaq Diagram Test").select();

    bot.button("Next >").click();
  }
}
