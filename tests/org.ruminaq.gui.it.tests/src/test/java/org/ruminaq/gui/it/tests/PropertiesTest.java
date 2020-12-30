/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.it.tests;

import org.eclipse.reddeer.eclipse.ui.views.properties.PropertySheet;
import org.eclipse.reddeer.gef.editor.GEFEditor;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ruminaq.tests.common.reddeer.GuiTest;

/**
 * Test properties view.
 *
 * @author Marek Jagielski
 */
@RunWith(RedDeerSuite.class)
public class PropertiesTest extends GuiTest {

  @Test
  public void testMainTaskTab() throws InterruptedException {
    GEFEditor gefEditor = new GEFEditor(diagramName);
    
    PropertySheet propertiesView = new PropertySheet();

    propertiesView.open();
    propertiesView.activate();
    propertiesView.selectTab("Main Task");
    bot.checkBox("Atomic").deselect();
    bot.checkBox("Prevent data loss").deselect();
    
    Thread.sleep(1000);
    
    assertDiagram(gefEditor, "PropertiesTest.testMainTaskTab.xml");
  }

}
