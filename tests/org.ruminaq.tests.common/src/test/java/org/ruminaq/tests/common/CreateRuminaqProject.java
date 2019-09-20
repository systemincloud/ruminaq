/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tests.common;

import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellCloses;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.ruminaq.logs.ModelerLoggerFactory;
import org.slf4j.Logger;

/**
 * Create new Ruminaq project.
 *
 * @author Marek Jagielski
 *
 */
public class CreateRuminaqProject {

  private static final Logger LOGGER = ModelerLoggerFactory
      .getLogger(CreateRuminaqProject.class);

  /**
   * Create new Ruminaq project.
   *
   * @param bot SWTWorkbenchBot
   * @param name name of project
   * @return
   */
  public SWTBotShell execute(SWTWorkbenchBot bot, String name) {
    openProjectWizardFromMainMenu(bot);

    bot.textWithLabel("&Project name:").setText(name);

    SWTBotShell window = bot.activeShell();

    bot.button("Finish").click();

    return window;
  }

  /**
   * Open new project wizard File => New.
   *
   * @param bot SWTWorkbenchBot
   * @param name name of project
   */
  public void openProjectWizardFromMainMenu(SWTWorkbenchBot bot) {
    bot.menu("File").menu("New").menu("Project...").click();
    bot.tree().getTreeItem("Ruminaq").expand();
    bot.tree().getTreeItem("Ruminaq").getNode("Ruminaq Project").select();
    bot.button("Next >").click();
  }

  /**
   * Accept perspective change on new project.
   *
   * @param bot SWTWorkbenchBot
]   */
  public void acceptPerspectiveChange(SWTWorkbenchBot bot) {
    SWTBotShell shellOpenPerspective = bot.shell("Open Associated Perspective?");
    shellOpenPerspective.activate();
    bot.button("Open Perspective").click();

    bot.waitUntil(shellCloses(shellOpenPerspective));
  }

  /**
   * Accept perspective change if pop ups.
   *
   * @param bot SWTWorkbenchBot
]   */
  public void acceptPerspectiveChangeIfPopUps(SWTWorkbenchBot bot) {
    try {
    	acceptPerspectiveChange(bot);
    } catch(WidgetNotFoundException e) {
    	LOGGER.info("No perspective change request");
    }
  }
}
