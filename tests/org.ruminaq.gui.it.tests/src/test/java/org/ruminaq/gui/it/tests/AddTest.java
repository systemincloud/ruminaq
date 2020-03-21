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
import org.ruminaq.gui.model.diagram.SimpleConnectionPointShape;
import org.ruminaq.model.ruminaq.InputPort;
import org.ruminaq.model.ruminaq.OutputPort;
import org.ruminaq.tests.common.reddeer.CreateSimpleConnection;
import org.ruminaq.tests.common.reddeer.WithBoGraphitiEditPart;
import org.ruminaq.tests.common.reddeer.WithLabelAssociated;
import org.ruminaq.tests.common.reddeer.WithShapeGraphitiEditPart;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.Diff;

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
  public void testAddOutputPort() {
    GEFEditor gefEditor = new GEFEditor(diagramName);
    gefEditor.addToolFromPalette("Output Port", 200, 100);
    assertFalse("Editor is always saved", gefEditor.isDirty());
    assertEquals("2 elements added", 3, gefEditor.getNumberOfEditParts());
    LabeledGraphitiEditPart opLabel = new LabeledGraphitiEditPart(
        "My Output Port");
    assertEquals("Label shouldn't have any pad buttons", 0,
        opLabel.getContextButtons().size());

    Diff diff = DiffBuilder
        .compare(Input.fromStream(
            AddTest.class.getResourceAsStream("AddTest.testAddOutputPort.xml")))
        .withTest(
            Input.fromFile(gefEditor.getAssociatedFile().getAbsolutePath()))
        .build();
    assertFalse(diff.toString(), diff.hasDifferences());
  }

  @Test
  public void testAddSimpleConnection() {
    GEFEditor gefEditor = new GEFEditor(diagramName);
    gefEditor.addToolFromPalette("Input Port", 200, 100);
    gefEditor.addToolFromPalette("Output Port", 400, 300);

    new CreateSimpleConnection(gefEditor,
        new WithBoGraphitiEditPart(InputPort.class),
        new WithBoGraphitiEditPart(OutputPort.class)).execute();
    assertEquals("5 elements", 5, gefEditor.getNumberOfEditParts());
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
    
    gefEditor.addToolFromPalette("Output Port", 400, 200);
    
    new CreateSimpleConnection(gefEditor,
        new WithShapeGraphitiEditPart(SimpleConnectionPointShape.class),
        new WithLabelAssociated("My Output Port 1")).execute();
    
    assertEquals("8 elements", 8, gefEditor.getNumberOfEditParts());
  }
}
