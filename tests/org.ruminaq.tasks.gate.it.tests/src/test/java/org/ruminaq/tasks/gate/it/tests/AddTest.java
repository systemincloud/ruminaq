/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.gate.it.tests;

import static org.junit.Assert.assertEquals;
import org.eclipse.reddeer.gef.editor.GEFEditor;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ruminaq.gui.model.diagram.InternalOutputPortShape;
import org.ruminaq.model.ruminaq.InternalInputPort;
import org.ruminaq.model.ruminaq.InternalOutputPort;
import org.ruminaq.tasks.gate.model.gate.And;
import org.ruminaq.tasks.gate.model.gate.Not;
import org.ruminaq.tests.common.reddeer.CreateSimpleConnection;
import org.ruminaq.tests.common.reddeer.GuiTest;
import org.ruminaq.tests.common.reddeer.WithBoGraphitiEditPart;
import org.ruminaq.tests.common.reddeer.WithShapeGraphitiEditPart;

/**
 * Test adding basic elements to diagram.
 *
 * @author Marek Jagielski
 */
@RunWith(RedDeerSuite.class)
public class AddTest extends GuiTest {

  @Test
  public void testAddNAnd() {
    GEFEditor gefEditor = new GEFEditor(diagramName);
    gefEditor.addToolFromPalette("And", 200, 100);
  }

  @Test
  public void testAddNot() {
    GEFEditor gefEditor = new GEFEditor(diagramName);
    gefEditor.addToolFromPalette("Not", 200, 100);
  }

  @Test
  public void testAddOr() {
    GEFEditor gefEditor = new GEFEditor(diagramName);
    gefEditor.addToolFromPalette("Or", 200, 100);
  }

  @Test
  public void testAddXor() {
    GEFEditor gefEditor = new GEFEditor(diagramName);
    gefEditor.addToolFromPalette("Or", 200, 100);
  }

  @Test
  public void testAddSimpleConnectionBetweenTasks()
      throws InterruptedException {
    GEFEditor gefEditor = new GEFEditor(diagramName);
    gefEditor.addToolFromPalette("And", 200, 100);

    WithShapeGraphitiEditPart outputPort = new WithShapeGraphitiEditPart(
        InternalOutputPortShape.class);
    assertEquals("Internal Output Port should have 1 context button.", 1,
        outputPort.getContextButtons().size());

    gefEditor.addToolFromPalette("Not", 300, 200);

    new CreateSimpleConnection(gefEditor,
        new WithBoGraphitiEditPart(And.class, InternalOutputPort.class),
        new WithBoGraphitiEditPart(Not.class, InternalInputPort.class))
            .execute();
    assertDiagram(gefEditor, "AddTest.testAddSimpleConnectionBetweenTasks.xml");
  }
}
