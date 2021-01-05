/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.it.tests;

import static org.junit.Assert.assertEquals;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ruminaq.model.ruminaq.InputPort;
import org.ruminaq.tests.common.reddeer.GuiTest;
import org.ruminaq.tests.common.reddeer.WithBoGraphitiEditPart;
import org.ruminaq.tests.common.reddeer.WithTextLabel;

/**
 * Test moving basic elements.
 *
 * @author Marek Jagielski
 */
@RunWith(RedDeerSuite.class)
public class CopyTest extends GuiTest {

  @Test
  public void testCopyPort() throws InterruptedException {
    addToolFromPalette("Input Port", 200, 100);

    WithBoGraphitiEditPart ip = new WithBoGraphitiEditPart(InputPort.class);
    ip.select();

    diagramEditor.getContextMenu().getItem("Copy").select();

    diagramEditor.click(300, 200);
    diagramEditor.getContextMenu().getItem("Paste").select();

    assertEquals("2 elements added", 5, diagramEditor.getNumberOfEditParts());

    new WithTextLabel("(Copy) My Input Port");
    
    diagramEditor.click(400, 300);
    diagramEditor.getContextMenu().getItem("Paste").select();
    
    assertEquals("3 elements added", 7, diagramEditor.getNumberOfEditParts());

    new WithTextLabel("(Copy) My Input Port 1");
    
    diagramEditor.click(500, 400);
    diagramEditor.getContextMenu().getItem("Paste").select();
    
    assertEquals("4 elements added", 9, diagramEditor.getNumberOfEditParts());

    new WithTextLabel("(Copy) My Input Port 2");
  }

}
