package org.ruminaq.gui.it.tests;

import static org.junit.Assert.assertFalse;

import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.eclipse.condition.ProjectExists;
import org.eclipse.reddeer.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.reddeer.eclipse.utils.DeleteUtils;
import org.eclipse.reddeer.gef.editor.GEFEditor;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.workbench.impl.shell.WorkbenchShell;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ruminaq.eclipse.wizards.diagram.CreateDiagramWizard;
import org.ruminaq.eclipse.wizards.project.SourceFolders;
import org.ruminaq.tests.common.reddeer.RuminaqDiagramWizard;
import org.ruminaq.tests.common.reddeer.RuminaqProjectWizard;

/**
 *
 *
 * @author Marek Jagielski
 */
@RunWith(RedDeerSuite.class)
public class AddInputPortTest {

  private static String projectName = "test";

  private static String diagramName = "Diagram";

  @BeforeClass
  public static void maximizeWorkbenchShell() {
    new WorkbenchShell().maximize();
  }

  @Before
  public void createProject() {
    new RuminaqProjectWizard().create(projectName);
    new ProjectExplorer().open();
    new WaitUntil(new ProjectExists(projectName), TimePeriod.MEDIUM, false);
    new ProjectExplorer().getProject(projectName).select();
    new RuminaqDiagramWizard().create(projectName, SourceFolders.DIAGRAM_FOLDER,
        diagramName + CreateDiagramWizard.DIAGRAM_EXTENSION_DOT);
  }

  @After
  public void deleteAllProjects() {
    new GEFEditor().close();
    ProjectExplorer projectExplorer = new ProjectExplorer();
    projectExplorer.open();
    DeleteUtils.forceProjectDeletion(projectExplorer.getProject(projectName),
        true);
  }

  @Test // (expected=TestFailureException.class)
  public void addInputPortTest() {
    GEFEditor gefEditor = new GEFEditor(diagramName);
    gefEditor.addToolFromPalette("Input Port", 200, 100);
    assertFalse("Editor is always saved", gefEditor.isDirty());
//    try {
//      gefEditor.addToolFromPalette("EClass", 50, 100).setLabel("ClassA");
//    } catch (CoreLayerException ex) {
//      throw new TestFailureException(ex.getMessage());
//    }
//    new LabeledGraphitiEditPart("ClassA").getContextButton("Delete").click();
//    new DefaultShell("Confirm Delete").setFocus();
//    new PushButton("Yes").click();
//    try {
//      new LabeledGraphitiEditPart("ClassA").select();
//    } catch (CoreLayerException ex) {
//      throw new TestFailureException(ex.toString());
//    }
  }

//  @Test(expected=TestFailureException.class)
  public void doubleClickTest() {
//    GEFEditor gefEditor = new GEFEditor("test");
//    try {
//      gefEditor.addToolFromPalette("EClass", 50, 100).setLabel("ClassA");
//    } catch (CoreLayerException ex) {
//      throw new TestFailureException(ex.getMessage());
//    }
//    gefEditor.addToolFromPalette("EClass", 200, 100).setLabel("ClassB");
//
//    new LabeledGraphitiEditPart("ClassA").doubleClick();
//    new DefaultShell("Rename EClass").setFocus();
//    new PushButton("Cancel").click();
  }
}
