/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.constant.it.tests;

import org.eclipse.reddeer.gef.editor.GEFEditor;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ruminaq.model.ruminaq.InternalOutputPort;
import org.ruminaq.tests.common.reddeer.GuiTest;
import org.ruminaq.tests.common.reddeer.MoveShape;
import org.ruminaq.tests.common.reddeer.WithBoGraphitiEditPart;

/**
 * Test adding basic elements to diagram.
 *
 * @author Marek Jagielski
 */
@RunWith(RedDeerSuite.class)
public class MoveTest extends GuiTest {

  @Test
  public void testMoveInternalPort() throws InterruptedException {
    GEFEditor gefEditor = new GEFEditor(diagramName);
    gefEditor.addToolFromPalette("Constant", 200, 100);

    WithBoGraphitiEditPart ip = new WithBoGraphitiEditPart(InternalOutputPort.class);
    ip.select();
    new MoveShape(gefEditor, ip, -40, 0).execute();

    Thread.sleep(1000);

    assertDiagram(gefEditor, "MoveTest.testMoveInternalPort.xml");
  }

  @Test
  public void testMoveInternalPortAlmostOnBoard() throws InterruptedException {
    GEFEditor gefEditor = new GEFEditor(diagramName);
    gefEditor.addToolFromPalette("Constant", 200, 100);

    WithBoGraphitiEditPart ip = new WithBoGraphitiEditPart(InternalOutputPort.class);
    ip.select();
    new MoveShape(gefEditor, ip, -38, 0).execute();

    Thread.sleep(1000);

    assertDiagram(gefEditor, "MoveTest.testMoveInternalPortAlmostOnBoard.1.xml");
    
    new MoveShape(gefEditor, ip, 10, -18).execute();

    assertDiagram(gefEditor, "MoveTest.testMoveInternalPortAlmostOnBoard.2.xml");
    
    new MoveShape(gefEditor, ip, 0, 32).execute();

    assertDiagram(gefEditor, "MoveTest.testMoveInternalPortAlmostOnBoard.3.xml");
    
    new MoveShape(gefEditor, ip, 23, -15).execute();

    assertDiagram(gefEditor, "MoveTest.testMoveInternalPortAlmostOnBoard.4.xml");
  }
  
  @Test
  public void testMoveInternalPortToIncorrectPlace() throws InterruptedException {
    GEFEditor gefEditor = new GEFEditor(diagramName);
    gefEditor.addToolFromPalette("Constant", 200, 100);

    WithBoGraphitiEditPart ip = new WithBoGraphitiEditPart(InternalOutputPort.class);
    ip.select();
    new MoveShape(gefEditor, ip, -10, 0).execute();

    Thread.sleep(1000);

    assertDiagram(gefEditor, "MoveTest.testMoveInternalPortToIncorrectPlace.xml");
  }

}
