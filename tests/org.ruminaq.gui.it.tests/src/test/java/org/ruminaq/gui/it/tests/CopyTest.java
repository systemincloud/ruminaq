package org.ruminaq.gui.it.tests;

import org.eclipse.reddeer.gef.editor.GEFEditor;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
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
public class CopyTest extends GuiTest {

  @Test
  public void testCopyPort() {
    GEFEditor gefEditor = new GEFEditor(diagramName);
    gefEditor.addToolFromPalette("Input Port", 200, 100);

    WithBoGraphitiEditPart ip = new WithBoGraphitiEditPart(InputPort.class);
    ip.select();
    
    ip.getContextButton("Copy").click();
  }

}
