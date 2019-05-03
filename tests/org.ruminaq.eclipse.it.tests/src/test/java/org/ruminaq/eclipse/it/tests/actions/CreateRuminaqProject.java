/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.it.tests.actions;

import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellCloses;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;

/**
 * Create new Ruminaq project.
 *
 * @author Marek Jagielski
 *
 */
public class CreateRuminaqProject {

  /**
   * Create new Ruminaq project.
   *
   * @param bot SWTWorkbenchBot
   * @param name name of project
   */
  public void execute(SWTWorkbenchBot bot, String name) {
    bot.menu("File").menu("New").menu("Project...").click();

    bot.tree().getTreeItem("Ruminaq").expand();

    bot.tree().getTreeItem("Ruminaq").getNode("Ruminaq Project").select();

    bot.button("Next >").click();

    bot.textWithLabel("&Project name:").setText(name);

    bot.button("Finish").click();

    SWTBotShell shellOpenPerpeitve = bot.shell("Open Associated Perspective?");
    shellOpenPerpeitve.activate();
    bot.button("Open Perspective").click();

    bot.waitUntil(shellCloses(shellOpenPerpeitve));
  }
}
