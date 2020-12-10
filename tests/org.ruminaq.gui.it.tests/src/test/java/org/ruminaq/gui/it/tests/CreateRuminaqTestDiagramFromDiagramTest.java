/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.it.tests;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.reddeer.gef.editor.GEFEditor;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.finders.ContextMenuHelper;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ruminaq.eclipse.wizards.diagram.CreateDiagramWizard;
import org.ruminaq.eclipse.wizards.project.CreateSourceFolders;
import org.ruminaq.model.ruminaq.EmbeddedTask;
import org.ruminaq.tests.common.SelectView;
import org.ruminaq.tests.common.reddeer.GuiTest;
import org.ruminaq.tests.common.reddeer.WithBoGraphitiEditPart;

/**
 * Test adding basic elements to diagram.
 *
 * @author Marek Jagielski
 */
@RunWith(RedDeerSuite.class)
public class CreateRuminaqTestDiagramFromDiagramTest extends GuiTest {

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
  public void testCreateTestDiagram()
      throws InterruptedException, CoreException {
    GEFEditor gefEditor = new GEFEditor(diagramName);
    gefEditor.addToolFromPalette("Input Port", 200, 100);
    gefEditor.addToolFromPalette("Input Port", 200, 200);
    gefEditor.addToolFromPalette("Input Port", 200, 300);
    gefEditor.addToolFromPalette("Output Port", 400, 100);
    gefEditor.addToolFromPalette("Output Port", 400, 200);

    String[] path = new String[] { CreateSourceFolders.SRC,
        CreateSourceFolders.MAIN, CreateSourceFolders.RESOURCES,
        CreateSourceFolders.TASK_FOLDER,
        diagramName + CreateDiagramWizard.DIAGRAM_EXTENSION_DOT };

    SWTBotMenu menu = new SWTBotMenu(ContextMenuHelper.contextMenu(
        SelectView.selectInProjectExplorer(bot, projectName, path),
        new String[] { "Ruminaq", "New Test Diagram" }));
    menu.click();

    GEFEditor gefEditorTest = new GEFEditor(diagramName + "Test");
    gefEditorTest.activate();
    assertDiagram(gefEditorTest,
        "CreateRuminaqTestDiagramFromDiagramTest1.xml");

    new WithBoGraphitiEditPart(EmbeddedTask.class).select();
    new WithBoGraphitiEditPart(EmbeddedTask.class).doubleClick();

    gefEditor.addToolFromPalette("Input Port", 200, 400);
    gefEditor.addToolFromPalette("Output Port", 400, 300);

    new SWTBotMenu(ContextMenuHelper.contextMenu(
        SelectView.selectInProjectExplorer(bot, projectName, path),
        new String[] { "Ruminaq", "New Test Diagram" })).click();

    GEFEditor gefEditorTest1 = new GEFEditor(diagramName + "Test_1");
    assertDiagram(gefEditorTest1,
        "CreateRuminaqTestDiagramFromDiagramTest2.xml");

    gefEditorTest.activate();

    WithBoGraphitiEditPart et = new WithBoGraphitiEditPart(EmbeddedTask.class);
    et.select();
    
    et.getContextButton("Update").click();
    
    
  }

}
