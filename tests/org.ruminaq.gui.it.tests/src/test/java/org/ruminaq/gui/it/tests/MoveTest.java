package org.ruminaq.gui.it.tests;

import org.eclipse.reddeer.gef.editor.GEFEditor;
import org.eclipse.reddeer.gef.handler.EditPartHandler;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.swt.widgets.Display;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ruminaq.model.ruminaq.InputPort;
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

    new MoveShape(gefEditor, ip, 10, 10).execute();
//    ip.getGEFEditPart().getModel()
  }

}
