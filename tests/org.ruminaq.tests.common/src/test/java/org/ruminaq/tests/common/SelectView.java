/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tests.common;

import java.util.stream.Stream;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

public class SelectView {

  public static void closeWelcomeViewIfExists(SWTWorkbenchBot bot) {
    bot.views().stream().filter(v -> "Welcome".equals(v.getTitle()))
        .forEach(SWTBotView::close);
  }

  public static SWTBotView getProjectExplorer(SWTWorkbenchBot bot) {
    return bot.viewByTitle("Project Explorer");
  }

  /**
   * Select and return resource in Project Explorer view.
   *
   * @param bot         bot instance
   * @param projectName eclipse project
   * @param dirs        path to resource
   * @return selected resource
   */
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
