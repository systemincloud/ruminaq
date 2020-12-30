/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.javatask.it.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.eclipse.reddeer.eclipse.ui.views.properties.PropertySheet;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.swt.SWT;
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
    SWTBotPreferences.KEYBOARD_LAYOUT = "EN_US";
    bot = new SWTWorkbenchBot();
  }

  @AfterClass
  public static void after() {
    bot.resetWorkbench();
  }

  @Test
  public void testAddJavaTask() throws InterruptedException {
    addToolFromPalette("Java Task", 200, 100);

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

    diagramEditor.activate();

    WithBoGraphitiEditPart jt = new WithBoGraphitiEditPart(JavaTask.class);
    jt.select();

    propertiesView.open();
    propertiesView.activate();
    propertiesView.selectTab("Java Task");

    bot.text().setText("test.NotExisting");

    bot.text().pressShortcut(SWT.CR, SWT.LF);

    bot.button("OK").click();

    bot.text().setText("test.Ports");

    bot.text().pressShortcut(SWT.CR, SWT.LF);

    bot.button("Select").click();

    Thread.sleep(1000);

    bot.text().setText("test.Ports");

    Thread.sleep(5000);

    bot.button("OK").click();

    Thread.sleep(2000);

    assertEquals("test.Ports", bot.text().getText(), "Should fill field");

    assertDiagram(diagramEditor, "AddTest.testAddJavaTask.1.xml");

    jt.doubleClick();

    Thread.sleep(1000);

    assertEquals("Ports.java", bot.activeEditor().getTitle(),
        "Should open Java Editor");

    SWTBotEclipseEditor textEditor = bot.activeEditor().toTextEditor();
    textEditor.navigateTo(4, 13);
    textEditor.typeText("(atomic = false)");

    Thread.sleep(1000);

    textEditor.insertText(3, 0,
        "import org.ruminaq.tasks.javatask.client.InputPort;\n");
    textEditor.insertText(3, 0,
        "import org.ruminaq.tasks.javatask.client.OutputPort;\n");
    textEditor.insertText(3, 0,
        "import org.ruminaq.tasks.javatask.client.annotations.InputPortInfo;\n");
    textEditor.insertText(3, 0,
        "import org.ruminaq.tasks.javatask.client.annotations.OutputPortInfo;\n");
    textEditor.insertText(3, 0,
        "import org.ruminaq.tasks.javatask.client.annotations.Parameter;\n");
    textEditor.insertText(3, 0,
        "import org.ruminaq.tasks.javatask.client.data.Complex32;\n");
    textEditor.insertText(3, 0,
        "import org.ruminaq.tasks.javatask.client.data.Complex64;\n");
    Thread.sleep(2000);

    textEditor.insertText(4, 0, "@Parameter(name = \"x\")\n");

    textEditor.insertText(8, 0,
        "\t@InputPortInfo(name = \"a\", dataType = { Complex32.class, Complex64.class })\n");
    textEditor.insertText(9, 0, "\tpublic InputPort a;\n");

    textEditor.insertText(10, 0, "\n");

    textEditor.insertText(11, 0,
        "\t@InputPortInfo(name = \"b\", queue = -1)\n");
    textEditor.insertText(12, 0, "\tpublic InputPort b;\n");

    textEditor.insertText(13, 0, "\n");

    textEditor.insertText(14, 0,
        "\t@OutputPortInfo(name = \"z\", dataType = Complex64.class)\n");
    textEditor.insertText(15, 0, "\tpublic OutputPort z;\n");

    textEditor.insertText(16, 0, "\n");

    textEditor.insertText(17, 0, "\t@Override\n");
    textEditor.insertText(18, 0, "\tpublic void execute(int grp) {\n");
    textEditor.insertText(19, 0, "\t}\n");

    textEditor.save();

    diagramEditor.activate();

    new WithBoGraphitiEditPart(JavaTask.class).select();

    Thread.sleep(1000);

    new WithBoGraphitiEditPart(JavaTask.class).getContextButton("Update")
        .click();

    Thread.sleep(1000);

    assertDiagram(diagramEditor, "AddTest.testAddJavaTask.2.xml");

    textEditor.show();

    textEditor.selectRange(8, 1, textEditor.getTextOnLine(8).length());
    textEditor
        .typeText("@InputPortInfo(name = \"a\", dataType = Complex64.class, queue = 3)\n");
    textEditor.selectRange(11, 1, textEditor.getTextOnLine(11).length());
    textEditor.typeText("@InputPortInfo(name = \"c\")\n");
    textEditor.selectRange(14, 1, textEditor.getTextOnLine(14).length());
    textEditor
        .typeText("@OutputPortInfo(name = \"x\", dataType = Complex32.class)\n");

    textEditor.save();

    diagramEditor.activate();

    new WithBoGraphitiEditPart(JavaTask.class).select();

    Thread.sleep(1000);

    new WithBoGraphitiEditPart(JavaTask.class).getContextButton("Update")
        .click();

    Thread.sleep(1000);

    assertDiagram(diagramEditor, "AddTest.testAddJavaTask.3.xml");
  }

}
