/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.it.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.List;
import org.eclipse.reddeer.eclipse.ui.views.properties.PropertySheet;
import org.eclipse.reddeer.gef.api.Palette;
import org.eclipse.reddeer.graphiti.api.ContextButton;
import org.eclipse.reddeer.graphiti.impl.graphitieditpart.LabeledGraphitiEditPart;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.swt.api.MenuItem;
import org.eclipse.swt.SWT;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ruminaq.gui.model.diagram.SimpleConnectionPointShape;
import org.ruminaq.gui.model.diagram.SimpleConnectionShape;
import org.ruminaq.model.ruminaq.EmbeddedTask;
import org.ruminaq.model.ruminaq.InputPort;
import org.ruminaq.model.ruminaq.OutputPort;
import org.ruminaq.tests.common.reddeer.CreateSimpleConnection;
import org.ruminaq.tests.common.reddeer.GuiTest;
import org.ruminaq.tests.common.reddeer.WithBoGraphitiEditPart;
import org.ruminaq.tests.common.reddeer.WithLabelAssociated;
import org.ruminaq.tests.common.reddeer.WithShapeGraphitiConnection;
import org.ruminaq.tests.common.reddeer.WithShapeGraphitiEditPart;

/**
 * Test adding basic elements to diagram.
 *
 * @author Marek Jagielski
 */
@RunWith(RedDeerSuite.class)
public class AddTest extends GuiTest {

  @Test
  public void testPalette() {
    Palette palette = diagramEditor.getPalette();
    List<String> tools = palette.getTools();
    assertTrue("Input Port in Palette", tools.contains("Input Port"));
    assertTrue("Output Port in Palette", tools.contains("Output Port"));
  }

  @Test
  public void testAddInputPort() throws InterruptedException {
    PropertySheet propertiesView = new PropertySheet();

    addToolFromPalette("Input Port", 200, 100);
    assertFalse("Editor is always saved", diagramEditor.isDirty());
    assertEquals("2 elements added", 3, diagramEditor.getNumberOfEditParts());
    LabeledGraphitiEditPart ipLabel = new LabeledGraphitiEditPart(
        "My Input Port");
    assertEquals("Label shouldn't have any pad buttons", 0,
        ipLabel.getContextButtons().size());
    assertTrue("Label has no 'Delete' in context menu",
        ipLabel.getContextButtons().stream().map(ContextButton::getText)
            .filter("Delete"::equals).findAny().isEmpty());
    WithBoGraphitiEditPart ip = new WithBoGraphitiEditPart(InputPort.class);
    ip.select();

    List<ContextButton> buttons = ip.getContextButtons();
    assertEquals("InputPort should have 2 pad buttons", 2, buttons.size());

    ip.getContextButton("Delete").click();
    assertEquals("0 elements left", 1, diagramEditor.getNumberOfEditParts());

    addToolFromPalette("Input Port", 200, 100);
    new LabeledGraphitiEditPart("My Input Port").select();
    addToolFromPalette("Input Port", 200, 200);
    new LabeledGraphitiEditPart("My Input Port 1").select();
    addToolFromPalette("Input Port", 200, 300);
    new LabeledGraphitiEditPart("My Input Port 2").select();

    ip = new WithBoGraphitiEditPart(InputPort.class);
    ip.select();

    propertiesView.open();
    propertiesView.activate();
    propertiesView.selectTab("Description");
    propertiesView.selectTab("General");
    propertiesView.selectTab("Input Port");
    
    bot.checkBox("Asynchronous").click();
    Thread.sleep(1000);
    assertDiagram(diagramEditor, "AddTest.testAddInputPort.1.xml");
    bot.checkBox("Asynchronous").click();
    bot.checkBox("Hold last").click();
    Thread.sleep(1000);
    assertDiagram(diagramEditor, "AddTest.testAddInputPort.2.xml");
    bot.text().setText("2");
    bot.text().pressShortcut(SWT.CR, SWT.LF);
    bot.spinner().setSelection(3);
    Thread.sleep(1000);
    assertDiagram(diagramEditor, "AddTest.testAddInputPort.3.xml");
  }

  @Test
  public void testAddOutputPort() throws InterruptedException {
    addToolFromPalette("Output Port", 200, 100);
    assertFalse("Editor is always saved", diagramEditor.isDirty());
    assertEquals("2 elements added", 3, diagramEditor.getNumberOfEditParts());
    LabeledGraphitiEditPart opLabel = new LabeledGraphitiEditPart(
        "My Output Port");
    assertEquals("Label shouldn't have any pad buttons", 0,
        opLabel.getContextButtons().size());

    Thread.sleep(1000);

    assertDiagram(diagramEditor, "AddTest.testAddOutputPort.xml");
  }

  @Test
  public void testAddSimpleConnection() throws InterruptedException {
    addToolFromPalette("Input Port", 200, 100);
    addToolFromPalette("Output Port", 400, 300);

    new CreateSimpleConnection(diagramEditor,
        new WithBoGraphitiEditPart(InputPort.class),
        new WithBoGraphitiEditPart(OutputPort.class)).execute();
    assertDiagram(diagramEditor, "AddTest.testAddSimpleConnection.1.xml");

    new WithShapeGraphitiConnection(SimpleConnectionShape.class).select();
    diagramEditor.getContextMenu().getItem("Delete").select();

    Thread.sleep(2000);

    assertDiagram(diagramEditor, "AddTest.testAddSimpleConnection.2.xml");
  }

  @Test
  public void testAddSimpleConnectionPoint() throws InterruptedException {
    addToolFromPalette("Input Port", 200, 100);
    addToolFromPalette("Output Port", 400, 100);

    new CreateSimpleConnection(diagramEditor,
        new WithBoGraphitiEditPart(InputPort.class),
        new WithBoGraphitiEditPart(OutputPort.class)).execute();

    diagramEditor.click(300, 200);

    assertTrue("Can't create connection point if clicked too far",
        diagramEditor.getContextMenu().getItems().stream()
            .map(MenuItem::getText)
            .noneMatch("Create connection point"::equals));

    diagramEditor.click(300, 102);

    assertTrue("Can create connection point if clicked close",
        diagramEditor.getContextMenu().getItems().stream()
            .map(MenuItem::getText)
            .anyMatch("Create connection point"::equals));

    diagramEditor.getContextMenu().getItem("Create connection point").select();
    assertEquals("6 elements", 6, diagramEditor.getNumberOfEditParts());

    WithShapeGraphitiEditPart connectionPoint = new WithShapeGraphitiEditPart(
        SimpleConnectionPointShape.class);
    connectionPoint.select();
    assertEquals("Connection point should have 2 buttons.", 2,
        connectionPoint.getContextButtons().size());

    addToolFromPalette("Output Port", 400, 200);

    new CreateSimpleConnection(diagramEditor, connectionPoint,
        new WithLabelAssociated("My Output Port 1")).execute();

    assertEquals("8 elements", 8, diagramEditor.getNumberOfEditParts());
  }

  @Test
  public void testAddSimpleConnectionPointAfterBend()
      throws InterruptedException {
    addToolFromPalette("Input Port", 200, 100);
    addToolFromPalette("Output Port", 400, 100);

    new CreateSimpleConnection(diagramEditor,
        new WithBoGraphitiEditPart(InputPort.class),
        new WithBoGraphitiEditPart(OutputPort.class)).execute();

    WithShapeGraphitiConnection connection = new WithShapeGraphitiConnection(
        SimpleConnectionShape.class);
    connection.select();

    addBendpoint(connection, 300, 200);
    diagramEditor.click(350, 150);
    diagramEditor.getContextMenu().getItem("Create connection point").select();

    Thread.sleep(1000);

    assertDiagram(diagramEditor,
        "AddTest.testAddSimpleConnectionPointAfterBend.xml");
  }

  @Test
  public void testAddEmbeddedTaskFromPalette() throws InterruptedException {
    diagramEditor.getPalette().activateTool("Embedded Task", null);
    addToolFromPalette("Embedded Task", 200, 100);

    WithBoGraphitiEditPart et = new WithBoGraphitiEditPart(EmbeddedTask.class);
    et.select();

    List<ContextButton> buttons = et.getContextButtons();
    assertEquals("EmbeddedTask should have 2 pad buttons", 2, buttons.size());
  }
}
