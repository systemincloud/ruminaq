/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.it.tests;

import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ruminaq.gui.model.diagram.SimpleConnectionPointShape;
import org.ruminaq.tests.common.reddeer.CreateSimpleConnection;
import org.ruminaq.tests.common.reddeer.GuiTest;
import org.ruminaq.tests.common.reddeer.WithLabelAssociated;
import org.ruminaq.tests.common.reddeer.WithShapeGraphitiEditPart;

/**
 * Test adding basic elements to diagram.
 *
 * @author Marek Jagielski
 */
@RunWith(RedDeerSuite.class)
public class DeleteTest extends GuiTest {

  @Before
  public void initLayout() throws InterruptedException {
    addToolFromPalette("Input Port", 100, 100);
    Thread.sleep(500);
    addToolFromPalette("Output Port", 500, 100);
    Thread.sleep(500);
    addToolFromPalette("Output Port", 500, 200);
    Thread.sleep(500);
    addToolFromPalette("Output Port", 500, 300);

    Thread.sleep(500);

    new CreateSimpleConnection(diagramEditor,
        new WithLabelAssociated("My Input Port"),
        new WithLabelAssociated("My Output Port")).execute();

    diagramEditor.click(200, 100);

    diagramEditor.getContextMenu().getItem("Create connection point").select();

    new CreateSimpleConnection(diagramEditor,
        new WithShapeGraphitiEditPart(SimpleConnectionPointShape.class),
        new WithLabelAssociated("My Output Port 1")).execute();

    diagramEditor.click(300, 130);

    diagramEditor.getContextMenu().getItem("Create connection point").select();

    new CreateSimpleConnection(diagramEditor,
        new WithShapeGraphitiEditPart(SimpleConnectionPointShape.class, 1),
        new WithLabelAssociated("My Output Port 2")).execute();

    assertDiagram(diagramEditor, "DeleteTest.initLayout.xml");
  }

  @Test
  public void testDeleteConnectionPoint() throws InterruptedException {
    new WithShapeGraphitiEditPart(SimpleConnectionPointShape.class, 1)
        .getContextButton("Delete").click();
    Thread.sleep(1000);
    assertDiagram(diagramEditor, "DeleteTest.testDeleteConnectionPoint.xml");
  }

  @Test
  public void testDeleteConnectionPointPrecedingOtherPoint()
      throws InterruptedException {
    new WithShapeGraphitiEditPart(SimpleConnectionPointShape.class, 0)
        .getContextButton("Delete").click();
    Thread.sleep(1000);
    assertDiagram(diagramEditor,
        "DeleteTest.testDeleteConnectionPointPrecedingOtherPoint.xml");
  }

  @Test
  public void testDeleteOutputPortWithConnection() throws InterruptedException {
    new WithLabelAssociated("My Output Port").getContextButton("Delete")
        .click();
    Thread.sleep(1000);
    assertDiagram(diagramEditor,
        "DeleteTest.testDeleteOutputPortWithConnection.xml");
  }

  @Test
  public void testDeleteInputPortWithConnection() throws InterruptedException {
    new WithLabelAssociated("My Input Port").getContextButton("Delete").click();
    Thread.sleep(1000);
    assertDiagram(diagramEditor,
        "DeleteTest.testDeleteInputPortWithConnection.xml");
  }

}
