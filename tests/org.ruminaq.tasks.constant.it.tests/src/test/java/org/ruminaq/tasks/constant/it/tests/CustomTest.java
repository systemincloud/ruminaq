/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.constant.it.tests;

import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ruminaq.model.ruminaq.InternalOutputPort;
import org.ruminaq.tests.common.reddeer.GuiTest;
import org.ruminaq.tests.common.reddeer.WithBoGraphitiEditPart;

/**
 * Test custom features.
 *
 * @author Marek Jagielski
 */
@RunWith(RedDeerSuite.class)
public class CustomTest extends GuiTest {

  private static SWTWorkbenchBot bot;

  @BeforeClass
  public static void initBot() {
    bot = new SWTWorkbenchBot();
  }

  @AfterClass
  public static void after() {
    bot.resetWorkbench();
  }

  @Test
  public void testInternalPortBreakpoint() throws InterruptedException {
    addToolFromPalette("Constant", 200, 100);

    WithBoGraphitiEditPart ip = new WithBoGraphitiEditPart(
        InternalOutputPort.class);
    ip.select();
    diagramEditor.getContextMenu().getItem("Toggle Breakpoint").select();

    Thread.sleep(1000);

    diagramEditor.getContextMenu().getItem("Breakpoint Properties...").select();

    Thread.sleep(1000);

    bot.button("Cancel").click();

    Thread.sleep(1000);

    ip.select();

    diagramEditor.getContextMenu().getItem("Disable Breakpoint").select();

    Thread.sleep(1000);

    diagramEditor.getContextMenu().getItem("Enable Breakpoint").select();

    Thread.sleep(1000);

    diagramEditor.getContextMenu().getItem("Toggle Breakpoint").select();
  }

}
