/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.sipo.it.tests;

import org.eclipse.graphiti.features.context.impl.ResizeShapeContext;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ruminaq.tasks.sipo.model.sipo.Sipo;
import org.ruminaq.tests.common.reddeer.GuiTest;
import org.ruminaq.tests.common.reddeer.ResizeShape;
import org.ruminaq.tests.common.reddeer.WithBoGraphitiEditPart;

/**
 * Test adding basic elements to diagram.
 *
 * @author Marek Jagielski
 */
@RunWith(RedDeerSuite.class)
public class ResizeTest extends GuiTest {

  @Test
  public void testResizeSipo() throws InterruptedException {
    addToolFromPalette("Sipo", 200, 100);

    WithBoGraphitiEditPart constant = new WithBoGraphitiEditPart(Sipo.class);

    new ResizeShape(diagramEditor, constant,
        ResizeShapeContext.DIRECTION_SOUTH_EAST, 40, 40).execute();

    assertDiagram(diagramEditor, "ResizeTest.testResizeSipo.xml");
  }

}
