/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.constant.it.tests;

import org.eclipse.reddeer.gef.editor.GEFEditor;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ruminaq.model.ruminaq.InternalOutputPort;
import org.ruminaq.tests.common.reddeer.GuiTest;
import org.ruminaq.tests.common.reddeer.WithBoGraphitiEditPart;

/**
 * Test custom features.
 *
 * @author Marek Jagielski
 */
@RunWith(RedDeerSuite.class)
public class CustomTest extends GuiTest {

  @Test
  public void testInternalPortBreakpoint() throws InterruptedException {
    GEFEditor gefEditor = new GEFEditor(diagramName);
    gefEditor.addToolFromPalette("Constant", 200, 100);

    WithBoGraphitiEditPart ip = new WithBoGraphitiEditPart(
        InternalOutputPort.class);
    ip.select();
    gefEditor.getContextMenu().getItem("Toggle Breakpoint").select();

    Thread.sleep(1000);

    gefEditor.getContextMenu().getItem("Toggle Breakpoint").select();
  }

}
