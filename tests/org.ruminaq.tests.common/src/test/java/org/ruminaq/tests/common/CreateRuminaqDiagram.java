/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tests.common;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.finders.ContextMenuHelper;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;

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
   * @param bot SWTWorkbenchBot
   * @param name name of project
   * @param diagramName 
   * @param path 
   */
  public void execute(SWTWorkbenchBot bot, String projectName, String path, String diagramName) {
  	SWTBotView pe = SelectView.getProjectExplorer(bot);
  	SWTBotTree selector = pe.bot().tree();
  	selector.select(projectName);
  	SWTBotMenu menu = new SWTBotMenu(
			ContextMenuHelper.contextMenu(selector, new String[] { "New", "Other..." }));
  	menu.click();
    bot.tree().getTreeItem("Ruminaq").expand();

    bot.tree().getTreeItem("Ruminaq").getNode("Ruminaq Diagram").select();
    
    bot.button("Next >").click();
    
    bot.textWithLabel("&Container:").setText(path);

    bot.textWithLabel("&File name:").setText(diagramName);

    bot.button("Finish").click();
  }
}
