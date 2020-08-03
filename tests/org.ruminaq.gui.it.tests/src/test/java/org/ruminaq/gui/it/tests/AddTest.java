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
import org.eclipse.reddeer.gef.api.Palette;
import org.eclipse.reddeer.gef.editor.GEFEditor;
import org.eclipse.reddeer.graphiti.api.ContextButton;
import org.eclipse.reddeer.graphiti.impl.graphitieditpart.LabeledGraphitiEditPart;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.swt.api.MenuItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ruminaq.gui.model.diagram.InternalOutputPortShape;
import org.ruminaq.gui.model.diagram.SimpleConnectionPointShape;
import org.ruminaq.gui.model.diagram.SimpleConnectionShape;
import org.ruminaq.model.ruminaq.EmbeddedTask;
import org.ruminaq.model.ruminaq.InputPort;
import org.ruminaq.model.ruminaq.InternalInputPort;
import org.ruminaq.model.ruminaq.InternalOutputPort;
import org.ruminaq.model.ruminaq.OutputPort;
import org.ruminaq.tasks.constant.model.constant.Constant;
import org.ruminaq.tasks.gate.model.gate.And;
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
    GEFEditor gefEditor = new GEFEditor(diagramName);
    Palette palette = gefEditor.getPalette();
    List<String> tools = palette.getTools();
    assertTrue("Input Port in Palette", tools.contains("Input Port"));
    assertTrue("Output Port in Palette", tools.contains("Output Port"));
  }

  @Test
  public void testAddInputPort() {
    GEFEditor gefEditor = new GEFEditor(diagramName);
    gefEditor.addToolFromPalette("Input Port", 200, 100);
    assertFalse("Editor is always saved", gefEditor.isDirty());
    assertEquals("2 elements added", 3, gefEditor.getNumberOfEditParts());
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
    assertEquals("0 elements left", 1, gefEditor.getNumberOfEditParts());

    gefEditor.addToolFromPalette("Input Port", 200, 100);
    new LabeledGraphitiEditPart("My Input Port").select();
    gefEditor.addToolFromPalette("Input Port", 200, 200);
    new LabeledGraphitiEditPart("My Input Port 1").select();
    gefEditor.addToolFromPalette("Input Port", 200, 300);
    new LabeledGraphitiEditPart("My Input Port 2").select();
  }

  @Test
  public void testAddOutputPort() throws InterruptedException {
    GEFEditor gefEditor = new GEFEditor(diagramName);
    gefEditor.addToolFromPalette("Output Port", 200, 100);
    assertFalse("Editor is always saved", gefEditor.isDirty());
    assertEquals("2 elements added", 3, gefEditor.getNumberOfEditParts());
    LabeledGraphitiEditPart opLabel = new LabeledGraphitiEditPart(
        "My Output Port");
    assertEquals("Label shouldn't have any pad buttons", 0,
        opLabel.getContextButtons().size());

    Thread.sleep(1000);

    assertDiagram(gefEditor, "AddTest.testAddOutputPort.xml");
  }

  @Test
  public void testAddSimpleConnection() throws InterruptedException {
    GEFEditor gefEditor = new GEFEditor(diagramName);
    gefEditor.addToolFromPalette("Input Port", 200, 100);
    gefEditor.addToolFromPalette("Output Port", 400, 300);

    new CreateSimpleConnection(gefEditor,
        new WithBoGraphitiEditPart(InputPort.class),
        new WithBoGraphitiEditPart(OutputPort.class)).execute();
    assertDiagram(gefEditor, "AddTest.testAddSimpleConnection.1.xml");

    new WithShapeGraphitiConnection(SimpleConnectionShape.class).select();
    gefEditor.getContextMenu().getItem("Delete").select();

    Thread.sleep(2000);

    assertDiagram(gefEditor, "AddTest.testAddSimpleConnection.2.xml");
  }

  @Test
  public void testAddSimpleConnectionBetweenTasks()
      throws InterruptedException {
    GEFEditor gefEditor = new GEFEditor(diagramName);
    gefEditor.addToolFromPalette("Constant", 200, 100);

    WithShapeGraphitiEditPart outputPort = new WithShapeGraphitiEditPart(
        InternalOutputPortShape.class);
    assertEquals("Internal Output Port should have 1 context button.", 1,
        outputPort.getContextButtons().size());

    gefEditor.addToolFromPalette("And", 400, 300);

    new CreateSimpleConnection(gefEditor,
        new WithBoGraphitiEditPart(Constant.class, InternalOutputPort.class),
        new WithBoGraphitiEditPart(And.class, InternalInputPort.class))
            .execute();
    assertDiagram(gefEditor, "AddTest.testAddSimpleConnectionBetweenTasks.xml");
  }

  @Test
  public void testAddSimpleConnectionPoint() {
    GEFEditor gefEditor = new GEFEditor(diagramName);
    gefEditor.addToolFromPalette("Input Port", 200, 100);
    gefEditor.addToolFromPalette("Output Port", 400, 100);

    new CreateSimpleConnection(gefEditor,
        new WithBoGraphitiEditPart(InputPort.class),
        new WithBoGraphitiEditPart(OutputPort.class)).execute();

    gefEditor.click(300, 200);

    assertTrue("Can't create connection point if clicked too far",
        gefEditor.getContextMenu().getItems().stream().map(MenuItem::getText)
            .noneMatch("Create connection point"::equals));

    gefEditor.click(300, 102);

    assertTrue("Can create connection point if clicked close",
        gefEditor.getContextMenu().getItems().stream().map(MenuItem::getText)
            .anyMatch("Create connection point"::equals));

    gefEditor.getContextMenu().getItem("Create connection point").select();
    assertEquals("6 elements", 6, gefEditor.getNumberOfEditParts());

    WithShapeGraphitiEditPart connectionPoint = new WithShapeGraphitiEditPart(
        SimpleConnectionPointShape.class);
    assertEquals("Connection point should have 2 buttons.", 2,
        connectionPoint.getContextButtons().size());

    gefEditor.addToolFromPalette("Output Port", 400, 200);

    new CreateSimpleConnection(gefEditor, connectionPoint,
        new WithLabelAssociated("My Output Port 1")).execute();

    assertEquals("8 elements", 8, gefEditor.getNumberOfEditParts());
  }

  @Test
  public void testAddEmbeddedTaskFromPalette() {
    GEFEditor gefEditor = new GEFEditor(diagramName);
    gefEditor.addToolFromPalette("Embedded Task", 200, 100);

    WithBoGraphitiEditPart et = new WithBoGraphitiEditPart(EmbeddedTask.class);
    et.select();

    List<ContextButton> buttons = et.getContextButtons();
    assertEquals("EmbeddedTask should have 2 pad buttons", 1, buttons.size());
  }
}
