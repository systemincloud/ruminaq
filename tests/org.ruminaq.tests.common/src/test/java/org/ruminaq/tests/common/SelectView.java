/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tests.common;

import java.util.stream.Stream;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

public class SelectView {

  public static void closeWelcomeViewIfExists(SWTWorkbenchBot bot) {
    try {
      bot.viewByTitle("Welcome").close();
    } catch (WidgetNotFoundException e) {
      System.out.println("Welcom view not found");
    }
  }

  public static SWTBotView getProjectExplorer(SWTWorkbenchBot bot) {
    return bot.viewByTitle("Project Explorer");
  }

  public static SWTBotTree selectInProjectExplorer(SWTWorkbenchBot bot,
      String projectName, String[] dirs) {
    SWTBotView pe = SelectView.getProjectExplorer(bot);
    SWTBotTree selector = pe.bot().tree();
    SWTBotTreeItem project = selector.getTreeItem(projectName);
    project.select();
    Stream.of(dirs).reduce(project, (node, dir) -> {
      node.expand();
      return node.getNode(dir);
    }, (node, dir) -> node).select();

    return selector;
  }
}
