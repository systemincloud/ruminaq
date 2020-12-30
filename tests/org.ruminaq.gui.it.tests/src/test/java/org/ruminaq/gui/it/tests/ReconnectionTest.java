/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.it.tests;

import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ruminaq.model.ruminaq.InputPort;
import org.ruminaq.model.ruminaq.OutputPort;
import org.ruminaq.tests.common.reddeer.CreateSimpleConnection;
import org.ruminaq.tests.common.reddeer.GuiTest;
import org.ruminaq.tests.common.reddeer.ReconnectConnection;
import org.ruminaq.tests.common.reddeer.WithBoGraphitiEditPart;

/**
 * Test adding basic elements to diagram.
 *
 * @author Marek Jagielski
 */
@RunWith(RedDeerSuite.class)
public class ReconnectionTest extends GuiTest {

  @Test
  public void testReconnectionSimpleConnection() throws InterruptedException {
    addToolFromPalette("Input Port", 200, 100);
    addToolFromPalette("Input Port", 200, 200);
    addToolFromPalette("Output Port", 400, 100);
    addToolFromPalette("Output Port", 400, 200);

    new CreateSimpleConnection(diagramEditor,
        new WithBoGraphitiEditPart(InputPort.class, 0),
        new WithBoGraphitiEditPart(OutputPort.class, 0)).execute();

    new ReconnectConnection(diagramEditor,
        new WithBoGraphitiEditPart(OutputPort.class, 0),
        new WithBoGraphitiEditPart(OutputPort.class, 1)).execute();

    assertDiagram(diagramEditor,
        "ReconnectionTest.testReconnectionSimpleConnection.1.xml");

    new ReconnectConnection(diagramEditor,
        new WithBoGraphitiEditPart(InputPort.class, 0),
        new WithBoGraphitiEditPart(InputPort.class, 1)).execute();

    assertDiagram(diagramEditor,
        "ReconnectionTest.testReconnectionSimpleConnection.2.xml");
  }

}
