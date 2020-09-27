/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.javatask.it.tests;

import static org.junit.Assert.assertTrue;
import org.eclipse.reddeer.eclipse.ui.views.properties.PropertySheet;
import org.eclipse.reddeer.gef.api.EditPart;
import org.eclipse.reddeer.gef.editor.GEFEditor;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ruminaq.tasks.javatask.model.javatask.JavaTask;
import org.ruminaq.tests.common.reddeer.GuiTest;
import org.ruminaq.tests.common.reddeer.WithBoGraphitiEditPart;

/**
 * Test properties view.
 *
 * @author Marek Jagielski
 */
@RunWith(RedDeerSuite.class)
public class PropertiesTest extends GuiTest {

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
  public void testDescrptionTab() throws InterruptedException {
    GEFEditor gefEditor = new GEFEditor(diagramName);
    gefEditor.addToolFromPalette("Java Task", 200, 100);

    WithBoGraphitiEditPart jt = new WithBoGraphitiEditPart(JavaTask.class);
    ((EditPart) jt).select();

    Thread.sleep(1000);

    PropertySheet propertiesView = new PropertySheet();

    propertiesView.open();
    propertiesView.activate();
    propertiesView.selectTab("Description");

    assertTrue("Description should be filled",
        bot.browser().getText().startsWith("<h3>JavaTask</h3>"));
  }
  
  @Test
  public void testPropertyTab() throws InterruptedException {
    GEFEditor gefEditor = new GEFEditor(diagramName);
    gefEditor.addToolFromPalette("Java Task", 200, 100);

    WithBoGraphitiEditPart jt = new WithBoGraphitiEditPart(JavaTask.class);
    ((EditPart) jt).select();

    Thread.sleep(1000);

    PropertySheet propertiesView = new PropertySheet();

    propertiesView.open();
    propertiesView.activate();
    propertiesView.selectTab("Java Task");
  }

}
