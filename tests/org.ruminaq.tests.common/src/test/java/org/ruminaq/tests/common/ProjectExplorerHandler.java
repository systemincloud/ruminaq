/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tests.common;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.navigator.resources.ProjectExplorer;

/**
 * Project Explorer actions.
 *
 * @author Marek Jagielski
 *
 */
public class ProjectExplorerHandler {

  /**
   * Select node in Project Explorer.
   *
   * @param bot         SWTWorkbenchBot
   * @param projectName name of project
   * @param dirs        following nodes
   * @return
   */
  public void select(SWTWorkbenchBot bot, String projectName, String... dirs) {
    SelectView.selectInProjectExplorer(bot, projectName, dirs).setFocus();
  }

  /**
   * Show in Project Explorer.
   *
   * @param bot SWTWorkbenchBot
   * @return
   */
  public void show(SWTWorkbenchBot bot) {
    SWTBotView modelExplorerView = bot
        .viewById(IPageLayout.ID_PROJECT_EXPLORER);
    boolean linkWithEditorInitialStatus = modelExplorerView
        .toolbarToggleButton("Link with Editor").isChecked();
    modelExplorerView.toolbarToggleButton("Link with Editor").click();
    if (!linkWithEditorInitialStatus) {
      bot.waitUntil(new LinkWithEditorStateCondition(modelExplorerView, true));
    } else {
      bot.waitUntil(new LinkWithEditorStateCondition(modelExplorerView, false));
    }
  }

  private class LinkWithEditorStateCondition extends DefaultCondition {

    private SWTBotView modelExplorerView;

    private boolean activated;

    public LinkWithEditorStateCondition(SWTBotView modelExplorerView,
        boolean activated) {
      this.modelExplorerView = modelExplorerView;
      this.activated = activated;
    }

    @Override
    public boolean test() throws Exception {
      ProjectExplorer explorerView = (ProjectExplorer) modelExplorerView
          .getViewReference().getView(false);
      return this.activated == explorerView.getCommonViewer()
          .getCommonNavigator().isLinkingEnabled();
    }

    @Override
    public String getFailureMessage() {
      return "The link with editor state should be " + this.activated;
    }
  }
}
