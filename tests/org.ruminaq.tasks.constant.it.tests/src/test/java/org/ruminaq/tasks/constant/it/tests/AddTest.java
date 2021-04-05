/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.constant.it.tests;

import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.swt.SWT;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ruminaq.gui.model.diagram.SimpleConnectionPointShape;
import org.ruminaq.gui.model.diagram.SimpleConnectionShape;
import org.ruminaq.model.ruminaq.InternalOutputPort;
import org.ruminaq.model.ruminaq.OutputPort;
import org.ruminaq.tests.common.reddeer.CreateSimpleConnection;
import org.ruminaq.tests.common.reddeer.GuiTest;
import org.ruminaq.tests.common.reddeer.WithBoGraphitiEditPart;
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
  public void testAddConstant() throws InterruptedException {
    addToolFromPalette("Constant", 200, 100);

    Thread.sleep(1000);

    assertDiagram(diagramEditor, "AddTest.testAddConstant.1.xml");

    addToolFromPalette("Output Port", 590, 120);
    addToolFromPalette("Output Port", 590, 160);
    addToolFromPalette("Output Port", 590, 200);
    addToolFromPalette("Output Port", 590, 240);

    new CreateSimpleConnection(diagramEditor,
        new WithBoGraphitiEditPart(InternalOutputPort.class, 0),
        new WithBoGraphitiEditPart(OutputPort.class, 0)).execute();

    diagramEditor.click(300, 125);
    diagramEditor.getContextMenu().getItem("Create connection point").select();

    diagramEditor.click(500, 125);
    diagramEditor.getContextMenu().getItem("Create connection point").select();

    new CreateSimpleConnection(diagramEditor,
        new WithShapeGraphitiEditPart(SimpleConnectionPointShape.class, 0),
        new WithBoGraphitiEditPart(OutputPort.class, 2)).execute();

    new CreateSimpleConnection(diagramEditor,
        new WithShapeGraphitiEditPart(SimpleConnectionPointShape.class, 1),
        new WithBoGraphitiEditPart(OutputPort.class, 1)).execute();

    new CreateSimpleConnection(diagramEditor,
        new WithBoGraphitiEditPart(InternalOutputPort.class, 0),
        new WithBoGraphitiEditPart(OutputPort.class, 3)).execute();

    WithShapeGraphitiConnection connection = new WithShapeGraphitiConnection(
        SimpleConnectionShape.class);
    connection.select();
    addBendpoint(connection, 280, 200);

    bot.canvas().pressShortcut(SWT.CTRL, 'a');
    bot.canvas().pressShortcut(SWT.CTRL, 'c');

    waitSeconds(1);

    diagramEditor.click(200, 250);
    bot.canvas().pressShortcut(SWT.CTRL, 'v');

    waitSeconds(1);

    assertDiagram(diagramEditor, "AddTest.testAddConstant.2.xml");
  }

}
