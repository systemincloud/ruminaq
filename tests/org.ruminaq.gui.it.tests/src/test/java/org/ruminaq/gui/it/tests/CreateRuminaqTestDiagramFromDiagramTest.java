/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.it.tests;

import static org.junit.Assert.assertEquals;
import java.util.List;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.reddeer.eclipse.ui.markers.Marker;
import org.eclipse.reddeer.eclipse.ui.views.markers.AllMarkersView;
import org.eclipse.reddeer.eclipse.ui.views.properties.PropertySheet;
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
import org.ruminaq.model.ruminaq.InputPort;
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
    addToolFromPalette("Input Port", 200, 100);
    new WithBoGraphitiEditPart(InputPort.class).doubleClick();
    addToolFromPalette("Input Port", 200, 200);
    addToolFromPalette("Input Port", 200, 300);
    addToolFromPalette("Output Port", 400, 100);
    addToolFromPalette("Output Port", 400, 200);

    String[] path = new String[] { CreateSourceFolders.SRC,
        CreateSourceFolders.MAIN, CreateSourceFolders.RESOURCES,
        CreateSourceFolders.TASK_FOLDER,
        diagramName + CreateDiagramWizard.DIAGRAM_EXTENSION_DOT };

    SWTBotMenu newTestmMenu = new SWTBotMenu(ContextMenuHelper.contextMenu(
        SelectView.selectInProjectExplorer(bot, projectName, path),
        new String[] { "Ruminaq", "New Test Diagram" }));
    newTestmMenu.click();

    GEFEditor gefEditorTest = new GEFEditor(diagramName + "Test");
    gefEditorTest.activate();
    assertDiagram(gefEditorTest,
        "CreateRuminaqTestDiagramFromDiagramTest1.xml");

    new WithBoGraphitiEditPart(EmbeddedTask.class).select();
    new WithBoGraphitiEditPart(EmbeddedTask.class).doubleClick();

    addToolFromPalette("Input Port", 200, 400);
    addToolFromPalette("Output Port", 400, 300);

    new SWTBotMenu(ContextMenuHelper.contextMenu(
        SelectView.selectInProjectExplorer(bot, projectName, path),
        new String[] { "Ruminaq", "New Test Diagram" })).click();

    GEFEditor gefEditorTest1 = new GEFEditor(diagramName + "Test_1");
    assertDiagram(gefEditorTest1,
        "CreateRuminaqTestDiagramFromDiagramTest2.xml");

    gefEditorTest.activate();

    WithBoGraphitiEditPart et = new WithBoGraphitiEditPart(EmbeddedTask.class);
    et.select();

    SWTBotMenu udpateDiagramMenu = new SWTBotMenu(ContextMenuHelper.contextMenu(
        SelectView.selectInProjectExplorer(bot, projectName,
            new String[] { CreateSourceFolders.SRC, CreateSourceFolders.TEST,
                CreateSourceFolders.RESOURCES, CreateSourceFolders.TASK_FOLDER,
                diagramName + "Test"
                    + CreateDiagramWizard.DIAGRAM_EXTENSION_DOT }),
        new String[] { "Ruminaq", "Update all Tasks" }));
    udpateDiagramMenu.click();

    assertDiagram(gefEditorTest,
        "CreateRuminaqTestDiagramFromDiagramTest3.xml");

    diagramEditor.activate();
    addToolFromPalette("Embedded Task", 250, 300);

    Thread.sleep(1000);
    new WithBoGraphitiEditPart(EmbeddedTask.class).select();

    PropertySheet propertiesView = new PropertySheet();

    propertiesView.open();
    propertiesView.activate();
    propertiesView.selectTab("Description");
    propertiesView.selectTab("Embedded Task");

    bot.button().click();

    Thread.sleep(1000);

    bot.tree().getTreeItem("src").expand();
    bot.tree().getTreeItem("src").getNode("test").expand();
    bot.tree().getTreeItem("src").getNode("test").getNode("resources").expand();
    bot.tree().getTreeItem("src").getNode("test").getNode("resources")
        .getNode("tasks").expand();
    bot.tree().getTreeItem("src").getNode("test").getNode("resources")
        .getNode("tasks").getNode(diagramName + "Test.rumi").select();

    bot.button("OK").click();

    diagramEditor.activate();

    Thread.sleep(1000);

    AllMarkersView markersView = new AllMarkersView();
    markersView.open();
    markersView.activate();
    List<Marker> markers = markersView.getMarker("Ruminaq");
    assertEquals("Marker should have description",
        "Loop in embedding tasks detected.", markers.get(0).getDescription());

    propertiesView.open();
    propertiesView.activate();
    propertiesView.selectTab("Embedded Task");

    bot.button("Create").click();
    bot.button("Finish").click();

    diagramEditor.activate();

    assertDiagram(diagramEditor,
        "CreateRuminaqTestDiagramFromDiagramTest4.xml");
  }

}
