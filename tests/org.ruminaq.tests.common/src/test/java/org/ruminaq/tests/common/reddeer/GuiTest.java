/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tests.common.reddeer;

import static org.junit.Assert.assertFalse;

import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.eclipse.condition.ProjectExists;
import org.eclipse.reddeer.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.reddeer.eclipse.utils.DeleteUtils;
import org.eclipse.reddeer.gef.editor.GEFEditor;
import org.eclipse.reddeer.workbench.impl.shell.WorkbenchShell;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.ruminaq.eclipse.wizards.diagram.CreateDiagramWizard;
import org.ruminaq.eclipse.wizards.project.SourceFolders;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.ComparisonResult;
import org.xmlunit.diff.ComparisonType;
import org.xmlunit.diff.Diff;

public class GuiTest {

  private static final int PROJECT_SUFFIX_LENGTH = 5;
  private static final int DIAGRAM_SUFFIX_LENGTH = 5;

  protected String projectName;

  protected String diagramName;

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

  protected void assertDiagram(GEFEditor gefEditor, String resourcePath) {
    Diff diff = DiffBuilder
        .compare(
            Input.fromStream(this.getClass().getResourceAsStream(resourcePath)))
        .withTest(
            Input.fromFile(gefEditor.getAssociatedFile().getAbsolutePath()))
        .withDifferenceEvaluator(((comparison, outcome) -> {
          if (outcome == ComparisonResult.DIFFERENT
              && comparison.getType() == ComparisonType.ATTR_VALUE
              && comparison.getControlDetails().getXPath().endsWith("@x")) {
            if (Math.abs(Integer
                .parseInt((String) comparison.getControlDetails().getValue())
                - Integer.parseInt(
                    (String) comparison.getTestDetails().getValue())) < 2) {
              return ComparisonResult.EQUAL;
            }
          }

          return outcome;
        })).build();
    assertFalse(diff.toString(), diff.hasDifferences());
  }
}
