/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.it.tests;

import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.eclipse.graphiti.ui.internal.parts.ShapeEditPart;
import org.eclipse.reddeer.gef.editor.GEFEditor;
import org.eclipse.reddeer.graphiti.api.GraphitiEditPart;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ruminaq.gui.model.diagram.InputPortShape;
import org.ruminaq.gui.model.diagram.LabelShape;
import org.ruminaq.gui.model.diagram.SimpleConnectionPointShape;
import org.ruminaq.model.ruminaq.InputPort;
import org.ruminaq.model.ruminaq.OutputPort;
import org.ruminaq.tests.common.reddeer.CreateSimpleConnection;
import org.ruminaq.tests.common.reddeer.GuiTest;
import org.ruminaq.tests.common.reddeer.MoveShape;
import org.ruminaq.tests.common.reddeer.WithBoGraphitiEditPart;
import org.ruminaq.tests.common.reddeer.WithShapeGraphitiEditPart;
import org.ruminaq.tests.common.reddeer.WithTextLabel;

/**
 * Test moving basic elements.
 *
 * @author Marek Jagielski
 */
@RunWith(RedDeerSuite.class)
public class MoveTest extends GuiTest {

  @Test
  public void testMovePort() {
    GEFEditor gefEditor = new GEFEditor(diagramName);
    gefEditor.addToolFromPalette("Input Port", 200, 100);
    gefEditor.addToolFromPalette("Output Port", 400, 300);

    WithBoGraphitiEditPart ip = new WithBoGraphitiEditPart(InputPort.class);
    ip.select();

    new MoveShape(gefEditor, ip, -10, -20).execute();
    InputPortShape shape = Optional.of(ip).map(GraphitiEditPart::getGEFEditPart)
        .filter(ShapeEditPart.class::isInstance).map(ShapeEditPart.class::cast)
        .map(ShapeEditPart::getPictogramElement)
        .filter(InputPortShape.class::isInstance)
        .map(InputPortShape.class::cast).orElseThrow();
    assertEquals("X should change", 190, shape.getX());
    assertEquals("Y should change", 80, shape.getY());
  }

  @Test
  public void testMoveOverLabel() throws InterruptedException {
    GEFEditor gefEditor = new GEFEditor(diagramName);
    gefEditor.addToolFromPalette("Input Port", 200, 100);

    WithBoGraphitiEditPart ip = new WithBoGraphitiEditPart(InputPort.class);
    ip.select();

    new MoveShape(gefEditor, ip, 0, 20).execute();

    Thread.sleep(1000);

    assertDiagram(gefEditor, "MoveTest.testMoveOverLabel.xml");
  }

  @Test
  public void testMoveLabel() {
    GEFEditor gefEditor = new GEFEditor(diagramName);
    gefEditor.addToolFromPalette("Input Port", 200, 100);
    WithBoGraphitiEditPart ip = new WithBoGraphitiEditPart(InputPort.class);

    WithTextLabel label = new WithTextLabel("My Input Port");

    InputPortShape shape = Optional.of(ip).map(GraphitiEditPart::getGEFEditPart)
        .filter(ShapeEditPart.class::isInstance).map(ShapeEditPart.class::cast)
        .map(ShapeEditPart::getPictogramElement)
        .filter(InputPortShape.class::isInstance)
        .map(InputPortShape.class::cast).orElseThrow();
    LabelShape labelShape = shape.getLabel();
    int xBefore = labelShape.getX();
    int yBefore = labelShape.getY();

    new MoveShape(gefEditor, label, 10, 20).execute();

    assertEquals("X shouldn't change", 200, shape.getX());
    assertEquals("Y shouldn't change", 100, shape.getY());
    assertEquals("X of label should change", xBefore + 10, labelShape.getX());
    assertEquals("Y of label should change", yBefore + 20, labelShape.getY());
  }

  @Test
  public void testConnectionPoint() {
    GEFEditor gefEditor = new GEFEditor(diagramName);
    gefEditor.addToolFromPalette("Input Port", 200, 100);
    gefEditor.addToolFromPalette("Output Port", 400, 100);

    new CreateSimpleConnection(gefEditor,
        new WithBoGraphitiEditPart(InputPort.class),
        new WithBoGraphitiEditPart(OutputPort.class)).execute();

    gefEditor.click(300, 102);
    gefEditor.getContextMenu().getItem("Create connection point").select();

    WithShapeGraphitiEditPart scp = new WithShapeGraphitiEditPart(
        SimpleConnectionPointShape.class);
    SimpleConnectionPointShape shape = Optional.of(scp)
        .map(GraphitiEditPart::getGEFEditPart)
        .filter(ShapeEditPart.class::isInstance).map(ShapeEditPart.class::cast)
        .map(ShapeEditPart::getPictogramElement)
        .filter(SimpleConnectionPointShape.class::isInstance)
        .map(SimpleConnectionPointShape.class::cast).orElseThrow();

    new MoveShape(gefEditor, scp, 10, 20).execute();

    assertEquals("X should change", 306, shape.getX());
    assertEquals("Y should change", 123, shape.getY());
  }

}
