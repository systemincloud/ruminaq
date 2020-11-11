/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.it.tests;

import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ruminaq.tests.common.CreateRuminaqProject;
import org.ruminaq.tests.common.OpenRuminaqProjectProperties;
import org.ruminaq.tests.common.SelectView;

/**
 * Test of creating a new eclipse project.
 *
 * @author Marek Jagielski
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class ChangeProjectPropertiesTest {

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

  private static final int PROJECT_SUFFIX_LENGTH = 5;

  @Test
  public final void testChangeProjectProperties() throws InterruptedException {
    String projectName = "test"
        + RandomStringUtils.randomAlphabetic(PROJECT_SUFFIX_LENGTH);
    new CreateRuminaqProject().execute(bot, projectName);
    new CreateRuminaqProject().acceptPerspectiveChangeIfPopUps(bot);

    new OpenRuminaqProjectProperties().execute(bot, projectName);

    SWTBotButton btnUpgrade = bot.buttonInGroup("Upgrade", "General");

    Assert.assertFalse("Upgrade button is disabled", btnUpgrade.isEnabled());

    new OpenRuminaqProjectProperties().applyAndclose(bot);
  }
}
