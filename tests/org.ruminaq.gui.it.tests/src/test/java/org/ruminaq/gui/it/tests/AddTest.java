package org.ruminaq.gui.it.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.eclipse.condition.ProjectExists;
import org.eclipse.reddeer.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.reddeer.eclipse.utils.DeleteUtils;
import org.eclipse.reddeer.gef.api.Palette;
import org.eclipse.reddeer.gef.editor.GEFEditor;
import org.eclipse.reddeer.graphiti.api.ContextButton;
import org.eclipse.reddeer.graphiti.impl.graphitieditpart.LabeledGraphitiEditPart;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.workbench.impl.shell.WorkbenchShell;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ruminaq.eclipse.wizards.diagram.CreateDiagramWizard;
import org.ruminaq.eclipse.wizards.project.SourceFolders;
import org.ruminaq.model.ruminaq.InputPort;
import org.ruminaq.tests.common.reddeer.RuminaqDiagramWizard;
import org.ruminaq.tests.common.reddeer.RuminaqProjectWizard;
import org.ruminaq.tests.common.reddeer.WithBoGraphitiEditPart;

/**
 *
 *
 * @author Marek Jagielski
 */
@RunWith(RedDeerSuite.class)
public class AddTest {

  private static final int PROJECT_SUFFIX_LENGTH = 5;
  private static final int DIAGRAM_SUFFIX_LENGTH = 5;

  private String projectName;

  private String diagramName;

  @BeforeClass
  public static void maximizeWorkbenchShell() {
    new WorkbenchShell().maximize();
  }

  @Before
  public void createProject() {
    projectName = "test"
        + RandomStringUtils.randomAlphabetic(PROJECT_SUFFIX_LENGTH);

    diagramName = "Diagram_"
        + RandomStringUtils.randomAlphabetic(DIAGRAM_SUFFIX_LENGTH);
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

  @Test
  public void paletteTest() {
    GEFEditor gefEditor = new GEFEditor(diagramName);
    Palette palette = gefEditor.getPalette();
    List<String> tools = palette.getTools();
    assertTrue("Input Port in Palette", tools.contains("Input Port"));
    assertTrue("Output Port in Palette", tools.contains("Output Port"));
  }

  @Test
  public void addInputPortTest() {
    GEFEditor gefEditor = new GEFEditor(diagramName);
    gefEditor.addToolFromPalette("Input Port", 200, 100);
    assertFalse("Editor is always saved", gefEditor.isDirty());
    assertEquals("2 elements added", 3, gefEditor.getNumberOfEditParts());
    LabeledGraphitiEditPart ipLabel = new LabeledGraphitiEditPart(
        "My Input Port");
    assertEquals("Label shouldn't have any pad buttons", 0,
        ipLabel.getContextButtons().size());
    WithBoGraphitiEditPart ip = new WithBoGraphitiEditPart(InputPort.class);
    ip.select();

    List<ContextButton> buttons = ip.getContextButtons();
    assertEquals("InputPort should have 2 pad buttons", 2, buttons.size());

    ip.getContextButton("Delete").click();
    assertEquals("0 elements left", 1, gefEditor.getNumberOfEditParts());
    
    gefEditor.addToolFromPalette("Input Port", 200, 100);
    new LabeledGraphitiEditPart(
        "My Input Port").select();
    gefEditor.addToolFromPalette("Input Port", 200, 200);
    new LabeledGraphitiEditPart(
        "My Input Port 1").select();
    gefEditor.addToolFromPalette("Input Port", 200, 300);
    new LabeledGraphitiEditPart(
        "My Input Port 2").select();
  }

  @Test
  public void addOutputPortTest() {
    GEFEditor gefEditor = new GEFEditor(diagramName);
    gefEditor.addToolFromPalette("Output Port", 200, 100);
    assertFalse("Editor is always saved", gefEditor.isDirty());
    assertEquals("2 elements added", 3, gefEditor.getNumberOfEditParts());
    LabeledGraphitiEditPart opLabel = new LabeledGraphitiEditPart(
        "My Output Port");
    assertEquals("Label shouldn't have any pad buttons", 0,
        opLabel.getContextButtons().size());
  }
}
