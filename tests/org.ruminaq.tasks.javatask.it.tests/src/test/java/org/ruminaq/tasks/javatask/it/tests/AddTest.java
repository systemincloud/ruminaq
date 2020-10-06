/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.javatask.it.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.eclipse.reddeer.eclipse.ui.views.properties.PropertySheet;
import org.eclipse.reddeer.gef.editor.GEFEditor;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ruminaq.tasks.javatask.model.javatask.JavaTask;
import org.ruminaq.tests.common.reddeer.GuiTest;
import org.ruminaq.tests.common.reddeer.WithBoGraphitiEditPart;

/**
 * Test adding basic elements to diagram.
 *
 * @author Marek Jagielski
 */
@RunWith(RedDeerSuite.class)
public class AddTest extends GuiTest {

  private static SWTWorkbenchBot bot;

  @BeforeClass
  public static void initBot() {
    bot = new SWTWorkbenchBot();
    SWTBotPreferences.KEYBOARD_LAYOUT = "EN_US";
  }

  @AfterClass
  public static void after() {
    bot.resetWorkbench();
  }

  @Test
  public void testAddJavaTask() throws InterruptedException {
    GEFEditor gefEditor = new GEFEditor(diagramName);
    gefEditor.addToolFromPalette("Java Task", 200, 100);

    PropertySheet propertiesView = new PropertySheet();

    propertiesView.open();
    propertiesView.activate();
    propertiesView.selectTab("Java Task");
    
    bot.button("Create").click();
    
    bot.textWithLabel("Package:").setText("test");
    bot.textWithLabel("Name:").setText("Ports");

    bot.button("Next >").click();

    bot.button("Finish").click();

    Thread.sleep(10000);

    gefEditor.activate();

    WithBoGraphitiEditPart jt = new WithBoGraphitiEditPart(JavaTask.class);
    jt.select();

    propertiesView.open();
    propertiesView.activate();
    propertiesView.selectTab("Java Task");

    bot.button("Select class").click();

    Thread.sleep(1000);

    bot.text().setText("test.Ports");

    Thread.sleep(5000);

    bot.button("OK").click();

    Thread.sleep(2000);

    assertEquals("test.Ports", bot.text().getText(), "Should fill field");

    assertDiagram(gefEditor, "AddTest.testAddJavaTask.1.xml");

    jt.doubleClick();

    Thread.sleep(1000);

    assertEquals("Ports.java", bot.activeEditor().getTitle(),
        "Should open Java Editor");
    
    SWTBotEclipseEditor textEditor = bot.activeEditor().toTextEditor();
    textEditor.navigateTo(4, 13);
    textEditor.typeText("(atomic = false)");
    textEditor.save();

    gefEditor.activate();
    
    new WithBoGraphitiEditPart(JavaTask.class).getContextButton("Update").click();

    Thread.sleep(1000);

    assertDiagram(gefEditor, "AddTest.testAddJavaTask.2.xml");
  }

}
