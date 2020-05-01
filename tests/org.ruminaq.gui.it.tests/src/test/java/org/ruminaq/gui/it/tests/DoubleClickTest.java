package org.ruminaq.gui.it.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.reddeer.eclipse.ui.views.properties.PropertySheet;
import org.eclipse.reddeer.gef.editor.GEFEditor;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
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
public class DoubleClickTest extends GuiTest {

  @Test
  public void testDoubleClickOnInputPort() {
    PropertySheet propertiesView = new PropertySheet();
    GEFEditor gefEditor = new GEFEditor(diagramName);
    gefEditor.addToolFromPalette("Input Port", 200, 100);
    new WithBoGraphitiEditPart(InputPort.class).select();
    new WithBoGraphitiEditPart(InputPort.class).singleClick();
    assertFalse("Properties doesn't open on single click",
        propertiesView.isOpen());
    new WithBoGraphitiEditPart(InputPort.class).doubleClick();
    assertTrue("Properties open on double click", propertiesView.isOpen());

    propertiesView.activate();
    propertiesView.selectTab("Input Port");
  }

}
