/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.javatask.it.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.eclipse.reddeer.eclipse.ui.views.properties.PropertySheet;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.swt.SWT;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ruminaq.gui.model.diagram.RuminaqDiagram;
import org.ruminaq.tasks.javatask.model.javatask.JavaTask;
import org.ruminaq.tests.common.reddeer.GuiTest;
import org.ruminaq.tests.common.reddeer.WithBoGraphitiEditPart;
import org.ruminaq.tests.common.reddeer.WithShapeGraphitiEditPart;

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
  public void testPropertiesTab() throws InterruptedException {
    addToolFromPalette("Java Task", 200, 100);

    WithBoGraphitiEditPart jt = new WithBoGraphitiEditPart(JavaTask.class);
    jt.select();

    Thread.sleep(1000);

    PropertySheet propertiesView = new PropertySheet();

    propertiesView.open();
    propertiesView.activate();
    propertiesView.selectTab("Description");

    Thread.sleep(1000);

    assertTrue("Description should be filled",
        bot.browser().getText().startsWith("<h3>JavaTask</h3>"));

    propertiesView.selectTab("Java Task");

    bot.button("Create").click();

    bot.textWithLabel("Package:").setText("test");
    bot.textWithLabel("Name:").setText("Parameters");

    bot.button("Next >").click();

    bot.textWithLabel("Name:", 2).setText("y");
    bot.textWithLabel("Default value:", 0).setText("2");
    bot.button("Add", 2).click();

    bot.textWithLabel("Name:", 2).setText("x");
    bot.textWithLabel("Default value:", 0).setText("0");
    bot.button("Add", 2).click();

    bot.button("Finish").click();

    Thread.sleep(3000);

    diagramEditor.activate();

    propertiesView.open();
    propertiesView.activate();
    propertiesView.selectTab("Parameters");

    bot.table(0).select(0);
    bot.text().setText("5");

    bot.text().pressShortcut(SWT.CR, SWT.LF);

    bot.table(0).select(0);
    bot.text().setText("6");

    bot.text().pressShortcut(Keystrokes.ESC);

    Thread.sleep(2000);

    assertDiagram(diagramEditor, "PropertiesTest.testPropertiesTab.xml");
    
    bot.table(0).select(0);
    bot.text().setText("${x}");
    
    bot.text().pressShortcut(SWT.CR, SWT.LF);

    WithShapeGraphitiEditPart rd = new WithShapeGraphitiEditPart(RuminaqDiagram.class);
    rd.click();
    
    propertiesView.open();
    propertiesView.activate();
    propertiesView.selectTab("Parameters");
    
    bot.table(0).select(0);
    assertEquals("x", bot.table().cell(0, 0));
  }
}
