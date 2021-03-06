/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.gate.it.tests;

import static org.junit.Assert.assertEquals;
import org.eclipse.reddeer.eclipse.ui.views.properties.PropertySheet;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.swt.SWT;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ruminaq.gui.model.diagram.InternalInputPortShape;
import org.ruminaq.gui.model.diagram.InternalOutputPortShape;
import org.ruminaq.model.ruminaq.InternalInputPort;
import org.ruminaq.model.ruminaq.InternalOutputPort;
import org.ruminaq.tasks.gate.model.gate.And;
import org.ruminaq.tasks.gate.model.gate.Not;
import org.ruminaq.tasks.gate.model.gate.Or;
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
  public void testAddXor() throws InterruptedException {
    addToolFromPalette("Xor", 200, 100);

    new WithShapeGraphitiEditPart(InternalInputPortShape.class).select();
    PropertySheet propertiesView = new PropertySheet();
    propertiesView.open();
    propertiesView.activate();
    propertiesView.selectTab("Internal Input Port");

    bot.checkBox(0).click();
    bot.checkBox(1).click();
    bot.checkBox(2).click();
    bot.text().setText("4");
    bot.text().pressShortcut(SWT.CR, SWT.LF);

    Thread.sleep(1000);

    assertDiagram(diagramEditor, "AddTest.testAddXor.1.xml");

    bot.button(0).click();
    bot.button(1).click();
    bot.button(2).click();

    Thread.sleep(1000);

    assertDiagram(diagramEditor, "AddTest.testAddXor.2.xml");
  }

  @Test
  public void testAddSimpleConnectionBetweenTasks()
      throws InterruptedException {
    addToolFromPalette("And", 200, 100);

    WithShapeGraphitiEditPart outputPort = new WithShapeGraphitiEditPart(
        InternalOutputPortShape.class);
    assertEquals("Internal Output Port should have 1 context button.", 1,
        outputPort.getContextButtons().size());

    addToolFromPalette("Not", 300, 200);

    new CreateSimpleConnection(diagramEditor,
        new WithBoGraphitiEditPart(And.class, InternalOutputPort.class),
        new WithBoGraphitiEditPart(Not.class, InternalInputPort.class))
            .execute();
    assertDiagram(diagramEditor,
        "AddTest.testAddSimpleConnectionBetweenTasks.xml");
  }

  @Test
  public void testSynchronization() throws InterruptedException {
    addToolFromPalette("Or", 200, 100);
    addToolFromPalette("And", 300, 200);
    addToolFromPalette("Not", 400, 300);

    new WithBoGraphitiEditPart(Or.class).select();
    PropertySheet propertiesView = new PropertySheet();
    propertiesView.open();
    propertiesView.activate();
    propertiesView.selectTab("Synchronization");

    waitSeconds(1);
    
    bot.button(0).click();

    waitSeconds(1);
    
    assertDiagram(diagramEditor,
        "AddTest.testSynchronization.1.xml");
 }
}
