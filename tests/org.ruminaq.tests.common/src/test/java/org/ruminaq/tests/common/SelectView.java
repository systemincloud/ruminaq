package org.ruminaq.tests.common;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.ruminaq.logs.ModelerLoggerFactory;
import org.slf4j.Logger;

public class SelectView {

  private static final Logger LOGGER = ModelerLoggerFactory
      .getLogger(SelectView.class);
  
	public static void closeWelcomeViewIfExists(SWTWorkbenchBot bot) {
		try {
		  bot.viewByTitle("Welcome").close();
		}
		catch (WidgetNotFoundException e) {
			LOGGER.debug("Welcom view not found");
		}
	}
	
	public static SWTBotView getProjectExplorer(SWTWorkbenchBot bot) {
		return bot.viewByTitle("Project Explorer");
	}
}
