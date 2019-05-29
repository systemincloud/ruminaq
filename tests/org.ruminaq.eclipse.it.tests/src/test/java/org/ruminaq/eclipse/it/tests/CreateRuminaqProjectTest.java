/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.it.tests;

import java.util.Collection;

import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ruminaq.eclipse.api.EclipseExtension;
import org.ruminaq.eclipse.it.tests.actions.CreateRuminaqProject;
import org.ruminaq.eclipse.wizards.project.CreateProjectWizard;
import org.ruminaq.util.ServiceUtil;

/**
 * Test of creating a new eclipse project.
 *
 * @author Marek Jagielski
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class CreateRuminaqProjectTest {

  private static SWTWorkbenchBot bot;
  private static Collection<EclipseExtension> extensions;

  @BeforeClass
  public static void initBot() {
    bot = new SWTWorkbenchBot();
    extensions = ServiceUtil
        .getServicesAtLatestVersion(CreateProjectWizard.class,
            EclipseExtension.class);
  }

  @AfterClass
  public static void afterClass() {
    bot.resetWorkbench();
  }

  private static final int PROJECT_SUFFIX_LENGTH = 5;

  @Test
  public final void testCreateProjectTest() {
    new CreateRuminaqProject().execute(bot,
        "test" + RandomStringUtils.randomAlphabetic(PROJECT_SUFFIX_LENGTH));

    bot.resetWorkbench();
  }
}
