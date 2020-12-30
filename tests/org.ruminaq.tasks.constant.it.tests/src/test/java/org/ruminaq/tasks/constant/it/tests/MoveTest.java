/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.constant.it.tests;

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
    addToolFromPalette("Constant", 200, 100);

    WithBoGraphitiEditPart ip = new WithBoGraphitiEditPart(
        InternalOutputPort.class);
    ip.select();
    new MoveShape(diagramEditor, ip, -40, 0).execute();

    Thread.sleep(1000);

    assertDiagram(diagramEditor, "MoveTest.testMoveInternalPort.xml");
  }

  @Test
  public void testMoveInternalPortAlmostOnBoard() throws InterruptedException {
    addToolFromPalette("Constant", 200, 100);

    WithBoGraphitiEditPart ip = new WithBoGraphitiEditPart(
        InternalOutputPort.class);
    ip.select();
    new MoveShape(diagramEditor, ip, -38, 0).execute();

    Thread.sleep(1000);

    assertDiagram(diagramEditor,
        "MoveTest.testMoveInternalPortAlmostOnBoard.1.xml");

    new MoveShape(diagramEditor, ip, 10, -18).execute();

    assertDiagram(diagramEditor,
        "MoveTest.testMoveInternalPortAlmostOnBoard.2.xml");

    new MoveShape(diagramEditor, ip, 0, 32).execute();

    assertDiagram(diagramEditor,
        "MoveTest.testMoveInternalPortAlmostOnBoard.3.xml");

    new MoveShape(diagramEditor, ip, 23, -15).execute();

    assertDiagram(diagramEditor,
        "MoveTest.testMoveInternalPortAlmostOnBoard.4.xml");
  }

  @Test
  public void testMoveInternalPortToIncorrectPlace()
      throws InterruptedException {
    addToolFromPalette("Constant", 200, 100);

    WithBoGraphitiEditPart ip = new WithBoGraphitiEditPart(
        InternalOutputPort.class);
    ip.select();
    new MoveShape(diagramEditor, ip, -10, 0).execute();

    Thread.sleep(1000);

    assertDiagram(diagramEditor,
        "MoveTest.testMoveInternalPortToIncorrectPlace.xml");
  }

}
