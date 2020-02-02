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
import org.ruminaq.model.ruminaq.InputPort;
import org.ruminaq.tests.common.reddeer.MoveShape;
import org.ruminaq.tests.common.reddeer.WithBoGraphitiEditPart;

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
        .filter(ShapeEditPart.class::isInstance)
        .map(ShapeEditPart.class::cast)
        .map(ShapeEditPart::getPictogramElement)
        .filter(InputPortShape.class::isInstance).map(InputPortShape.class::cast)
        .get();
    assertEquals("X should change", 190, shape.getX());
    assertEquals("Y should change", 80, shape.getY());
  }

}
