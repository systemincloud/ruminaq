/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.it.tests;

import org.eclipse.reddeer.eclipse.ui.views.properties.PropertySheet;
import org.eclipse.reddeer.graphiti.impl.graphitieditpart.LabeledGraphitiEditPart;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.swt.SWT;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ruminaq.model.ruminaq.InputPort;
import org.ruminaq.tests.common.reddeer.GuiTest;
import org.ruminaq.tests.common.reddeer.WithBoGraphitiEditPart;

/**
 * Test adding basic elements to diagram.
 *
 * @author Marek Jagielski
 */
@RunWith(RedDeerSuite.class)
public class DirectEditingTest extends GuiTest {

  @Test
  public void testEditLabel() throws InterruptedException {
    addToolFromPalette("Input Port", 200, 100);
    LabeledGraphitiEditPart ipLabel = new LabeledGraphitiEditPart(
        "My Input Port");
    ipLabel.click();
    ipLabel.setLabel("Label");

    Thread.sleep(1000);

    assertDiagram(diagramEditor, "DirectEditingTest.testEditLabel.xml");
    
    addToolFromPalette("Output Port", 300, 300);

    new WithBoGraphitiEditPart(InputPort.class).select();
    
    PropertySheet propertiesView = new PropertySheet();

    propertiesView.open();
    propertiesView.activate();
    propertiesView.selectTab("General");
    
    bot.text().setText("");
    
    bot.text().pressShortcut(SWT.CR, SWT.LF);

    bot.button("OK").click();

    propertiesView.activate();
    bot.text().setText("My Output Port");

    bot.text().pressShortcut(SWT.CR, SWT.LF);

    bot.button("OK").click();
  }

}
