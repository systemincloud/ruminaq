/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.it.tests;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ruminaq.eclipse.prefs.WorkspacePrefsPage;
import org.ruminaq.logs.ModelerLoggerFactory;
import org.ruminaq.tests.common.SelectView;

/**
 * Test of creating a new eclipse project.
 *
 * @author Marek Jagielski
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class ChangeWorkspacePreferencesTest {

  private static SWTWorkbenchBot bot;

  /**
   * Initialize SWTBot.
   *
   */
  @BeforeClass
  public static void initBot() {
    bot = new SWTWorkbenchBot();
    SelectView.closeWelcomeViewIfExists(bot);
  }

  @AfterClass
  public static void after() {
    bot.resetWorkbench();
  }

  @Test
  public final void testChangeWorkspacePreferences() {
    bot.menu("Window").menu("Preferences").click();
    bot.tree().getTreeItem("Ruminaq").select();
    bot.comboBoxWithLabel("Modeler log level:").setSelection("WARN");
    bot.button("Apply and Close").click();

    Assert.assertEquals("WARN preference should be set", "WARN",
        InstanceScope.INSTANCE.getNode(WorkspacePrefsPage.QUALIFIER)
            .get(ModelerLoggerFactory.MODELER_LOG_LEVEL_PREF, ""));

    bot.menu("Window").menu("Preferences").click();
    bot.tree().getTreeItem("Ruminaq").select();
    bot.comboBoxWithLabel("Modeler log level:").setSelection("ERROR");
    bot.button("Apply and Close").click();

    Assert.assertEquals("ERROR preference should be set", "ERROR",
        InstanceScope.INSTANCE.getNode(WorkspacePrefsPage.QUALIFIER)
            .get(ModelerLoggerFactory.MODELER_LOG_LEVEL_PREF, ""));
  }
}
