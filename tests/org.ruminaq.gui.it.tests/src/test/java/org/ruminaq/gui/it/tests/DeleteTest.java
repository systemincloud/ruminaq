/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.it.tests;

import org.eclipse.reddeer.gef.editor.GEFEditor;
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

  GEFEditor gefEditor;

  @Before
  public void initLayout() throws InterruptedException {
    gefEditor = new GEFEditor(diagramName);

    gefEditor.addToolFromPalette("Input Port", 100, 100);
    Thread.sleep(500);
    gefEditor.addToolFromPalette("Output Port", 500, 100);
    Thread.sleep(500);
    gefEditor.addToolFromPalette("Output Port", 500, 200);
    Thread.sleep(500);
    gefEditor.addToolFromPalette("Output Port", 500, 300);

    Thread.sleep(500);

    new CreateSimpleConnection(gefEditor,
        new WithLabelAssociated("My Input Port"),
        new WithLabelAssociated("My Output Port")).execute();

    gefEditor.click(200, 100);

    gefEditor.getContextMenu().getItem("Create connection point").select();

    new CreateSimpleConnection(gefEditor,
        new WithShapeGraphitiEditPart(SimpleConnectionPointShape.class),
        new WithLabelAssociated("My Output Port 1")).execute();

    gefEditor.click(300, 130);

    gefEditor.getContextMenu().getItem("Create connection point").select();

    new CreateSimpleConnection(gefEditor,
        new WithShapeGraphitiEditPart(SimpleConnectionPointShape.class, 1),
        new WithLabelAssociated("My Output Port 2")).execute();

    assertDiagram(gefEditor, "DeleteTest.initLayout.xml");
  }

  @Test
  public void testDeleteConnectionPoint() throws InterruptedException {
    new WithShapeGraphitiEditPart(SimpleConnectionPointShape.class, 1)
        .getContextButton("Delete").click();
    Thread.sleep(1000);
    assertDiagram(gefEditor, "DeleteTest.testDeleteConnectionPoint.xml");
  }

  @Test
  public void testDeleteConnectionPointPrecedingOtherPoint()
      throws InterruptedException {
    new WithShapeGraphitiEditPart(SimpleConnectionPointShape.class, 0)
        .getContextButton("Delete").click();
    Thread.sleep(1000);
    assertDiagram(gefEditor,
        "DeleteTest.testDeleteConnectionPointPrecedingOtherPoint.xml");
  }

  @Test
  public void testDeleteOutputPortWithConnection() throws InterruptedException {
    new WithLabelAssociated("My Output Port").getContextButton("Delete")
        .click();
    Thread.sleep(1000);
    assertDiagram(gefEditor,
        "DeleteTest.testDeleteOutputPortWithConnection.xml");
  }

  @Test
  public void testDeleteInputPortWithConnection() throws InterruptedException {
    new WithLabelAssociated("My Input Port").getContextButton("Delete").click();
    Thread.sleep(1000);
    assertDiagram(gefEditor,
        "DeleteTest.testDeleteInputPortWithConnection.xml");
  }

}
