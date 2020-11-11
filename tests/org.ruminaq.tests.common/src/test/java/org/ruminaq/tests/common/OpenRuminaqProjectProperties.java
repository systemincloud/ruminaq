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
public class OpenRuminaqProjectProperties {

  /**
   * Create new Ruminaq daigram.
   *
   * @param bot         SWTWorkbenchBot
   * @param projectName name of project
   */
  public void execute(SWTWorkbenchBot bot, String projectName) {
    SWTBotTree selector = SelectView.selectInProjectExplorer(bot, projectName,
        new String[0]);
    SWTBotMenu menu = new SWTBotMenu(
        ContextMenuHelper.contextMenu(selector, new String[] { "Properties" }));
    menu.click();
    bot.tree().getTreeItem("Ruminaq").select();
  }

  public void applyAndclose(SWTWorkbenchBot bot) {
    bot.button("Apply and Close").click();
  }
}
