package org.ruminaq.tests.common;

import java.util.stream.Stream;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.ruminaq.logs.ModelerLoggerFactory;
import org.slf4j.Logger;

public class SelectView {

  private static final Logger LOGGER = ModelerLoggerFactory
      .getLogger(SelectView.class);

  public static void closeWelcomeViewIfExists(SWTWorkbenchBot bot) {
    try {
      bot.viewByTitle("Welcome").close();
    } catch (WidgetNotFoundException e) {
      LOGGER.debug("Welcom view not found");
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
