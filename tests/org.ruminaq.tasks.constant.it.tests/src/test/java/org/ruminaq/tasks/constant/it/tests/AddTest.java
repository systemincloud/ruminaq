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
import org.ruminaq.model.ruminaq.InternalOutputPort;
import org.ruminaq.model.ruminaq.OutputPort;
import org.ruminaq.tests.common.reddeer.CreateSimpleConnection;
import org.ruminaq.tests.common.reddeer.GuiTest;
import org.ruminaq.tests.common.reddeer.WithBoGraphitiEditPart;

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

    addToolFromPalette("Output Port", 400, 150);

    new CreateSimpleConnection(diagramEditor,
        new WithBoGraphitiEditPart(InternalOutputPort.class, 0),
        new WithBoGraphitiEditPart(OutputPort.class, 0)).execute();

    bot.canvas().pressShortcut(SWT.CTRL, 'a');
    bot.canvas().pressShortcut(SWT.CTRL, 'c');
    diagramEditor.click(600, 200);
    bot.canvas().pressShortcut(SWT.CTRL, 'v');

    Thread.sleep(1000);

    assertDiagram(diagramEditor, "AddTest.testAddConstant.2.xml");
  }

}
