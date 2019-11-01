package org.ruminaq.gui.it.tests;

import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.reddeer.common.exception.TestFailureException;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.core.exception.CoreLayerException;
import org.eclipse.reddeer.eclipse.condition.ProjectExists;
import org.eclipse.reddeer.eclipse.selectionwizard.NewMenuWizard;
import org.eclipse.reddeer.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.reddeer.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.eclipse.reddeer.gef.editor.GEFEditor;
import org.eclipse.reddeer.graphiti.impl.graphitieditpart.LabeledGraphitiEditPart;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.swt.impl.button.PushButton;
import org.eclipse.reddeer.swt.impl.combo.LabeledCombo;
import org.eclipse.reddeer.swt.impl.shell.DefaultShell;
import org.eclipse.reddeer.swt.impl.text.LabeledText;
import org.eclipse.reddeer.workbench.impl.shell.WorkbenchShell;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 *
 * @author Marek Jagielski
 */
@RunWith(RedDeerSuite.class)
public class AddInputPortTest {

  private static final int PROJECT_SUFFIX_LENGTH = 5;

  public class RuminaqProjectWizard extends BasicNewProjectResourceWizard {

    public void create(String name) {
      open();
      new LabeledText("Project name:").setText(name);
      finish();
    }
  }

  public class TutorialDiagramWizard extends NewMenuWizard {

    public TutorialDiagramWizard() {
      super("New Diagram", "Other", "Graphiti Example Diagram");
    }

    public void create(String name) {
      open();
      new LabeledCombo("Diagram Type").setSelection("tutorial");
      next();
      new LabeledText("Diagram Name").setText(name);
      finish();
    }
  }

  @BeforeClass
  public static void maximizeWorkbenchShell() {
    new WorkbenchShell().maximize();
  }

  @Before
  public void createProject() {
    String projectName = "test"
        + RandomStringUtils.randomAlphabetic(PROJECT_SUFFIX_LENGTH);
    new RuminaqProjectWizard().create(projectName);
    new ProjectExplorer().open();
    new WaitUntil(new ProjectExists(projectName), TimePeriod.MEDIUM, false);
    new ProjectExplorer().getProject(projectName).select();
    new TutorialDiagramWizard().create("test");
  }

  @After
  public void deleteAllProjects() {
    new GEFEditor().close();
    ProjectExplorer projectExplorer = new ProjectExplorer();
    projectExplorer.open();
//    DeleteUtils.forceProjectDeletion(projectExplorer.getProject(projectName),true);
  }

  @Test(expected=TestFailureException.class)
  public void contextButtonTest() {
    GEFEditor gefEditor = new GEFEditor("test");
    try {
      gefEditor.addToolFromPalette("EClass", 50, 100).setLabel("ClassA");
    } catch (CoreLayerException ex) {
      throw new TestFailureException(ex.getMessage());
    }
    gefEditor.addToolFromPalette("EClass", 200, 100).setLabel("ClassB");

    new LabeledGraphitiEditPart("ClassA").getContextButton("Delete").click();
    new DefaultShell("Confirm Delete").setFocus();
    new PushButton("Yes").click();
    try {
      new LabeledGraphitiEditPart("ClassA").select();
    } catch (CoreLayerException ex) {
      throw new TestFailureException(ex.toString());
    }
  }

  @Test(expected=TestFailureException.class)
  public void doubleClickTest() {
    GEFEditor gefEditor = new GEFEditor("test");
    try {
      gefEditor.addToolFromPalette("EClass", 50, 100).setLabel("ClassA");
    } catch (CoreLayerException ex) {
      throw new TestFailureException(ex.getMessage());
    }
    gefEditor.addToolFromPalette("EClass", 200, 100).setLabel("ClassB");

    new LabeledGraphitiEditPart("ClassA").doubleClick();
    new DefaultShell("Rename EClass").setFocus();
    new PushButton("Cancel").click();
  }
}
