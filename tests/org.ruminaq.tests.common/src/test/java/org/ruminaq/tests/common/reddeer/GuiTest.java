/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tests.common.reddeer;

import static org.junit.Assert.assertFalse;
import java.util.Arrays;
import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.core.handler.WidgetHandler;
import org.eclipse.reddeer.eclipse.condition.ProjectExists;
import org.eclipse.reddeer.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.reddeer.eclipse.utils.DeleteUtils;
import org.eclipse.reddeer.gef.editor.GEFEditor;
import org.eclipse.reddeer.workbench.impl.shell.WorkbenchShell;
import org.eclipse.swt.SWT;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.ruminaq.eclipse.editor.RuminaqEditor;
import org.ruminaq.eclipse.wizards.diagram.CreateDiagramWizard;
import org.ruminaq.eclipse.wizards.project.CreateSourceFolders;
import org.ruminaq.gui.model.diagram.SimpleConnectionShape;
import org.ruminaq.model.ruminaq.ModelUtil;
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

  protected GEFEditor diagramEditor;

  protected static SWTWorkbenchBot bot;

  @BeforeClass
  public static void initBot() {
    SWTBotPreferences.KEYBOARD_LAYOUT = "EN_US";
    bot = new SWTWorkbenchBot();
  }

  @AfterClass
  public static void after() {
    bot.resetWorkbench();
  }

  private static int levenshteinDistanceate(String x, String y) {
    int[][] dp = new int[x.length() + 1][y.length() + 1];

    for (int i = 0; i <= x.length(); i++) {
      for (int j = 0; j <= y.length(); j++) {
        if (i == 0) {
          dp[i][j] = j;
        } else if (j == 0) {
          dp[i][j] = i;
        } else {
          dp[i][j] = min(
              dp[i - 1][j - 1]
                  + costOfSubstitution(x.charAt(i - 1), y.charAt(j - 1)),
              dp[i - 1][j] + 1, dp[i][j - 1] + 1);
        }
      }
    }
    return dp[x.length()][y.length()];
  }

  private static int costOfSubstitution(char a, char b) {
    return a == b ? 0 : 1;
  }

  private static int min(int... numbers) {
    return Arrays.stream(numbers).min().orElse(Integer.MAX_VALUE);
  }

  @BeforeClass
  public static void maximizeWorkbenchShell() {
    new WorkbenchShell().maximize();
  }

  @Before
  public void createProject() throws InterruptedException {
    projectName = "test"
        + RandomStringUtils.randomAlphabetic(PROJECT_SUFFIX_LENGTH);

    diagramName = "Diagram_"
        + RandomStringUtils.randomAlphabetic(DIAGRAM_SUFFIX_LENGTH);
    new RuminaqProjectWizard().create(projectName);
    new ProjectExplorer().open();
    new WaitUntil(new ProjectExists(projectName), TimePeriod.MEDIUM, false);
    new ProjectExplorer().getProject(projectName).select();
    new RuminaqDiagramWizard().create(projectName,
        CreateSourceFolders.DIAGRAM_FOLDER,
        diagramName + CreateDiagramWizard.DIAGRAM_EXTENSION_DOT);
    diagramEditor = new GEFEditor(diagramName);
  }

  @After
  public void deleteAllProjects() {
    new GEFEditor().close();
    ProjectExplorer projectExplorer = new ProjectExplorer();
    projectExplorer.open();
    DeleteUtils.forceProjectDeletion(projectExplorer.getProject(projectName),
        true);
  }

  protected void addToolFromPalette(String tool, int x, int y)
      throws InterruptedException {
    diagramEditor.getPalette().activateTool(tool, null);
    FigureCanvas figureCanvas = (FigureCanvas) diagramEditor.getControl();
    WidgetHandler.getInstance().notifyItemMouse(SWT.MouseMove, 0, figureCanvas,
        null, x, y, 0);
    WidgetHandler.getInstance().notifyItemMouse(SWT.MouseDown, 0, figureCanvas,
        null, x, y, 1);
    Thread.sleep(1000);
    WidgetHandler.getInstance().notifyItemMouse(SWT.MouseUp, 0, figureCanvas,
        null, x, y, 1);
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
                    (String) comparison.getTestDetails().getValue())) < 8) {
              return ComparisonResult.EQUAL;
            }
          }

          if (outcome == ComparisonResult.DIFFERENT
              && comparison.getType() == ComparisonType.ATTR_VALUE
              && comparison.getControlDetails().getXPath()
                  .endsWith("@implementationPath")) {
            if (levenshteinDistanceate(
                (String) comparison.getControlDetails().getValue(),
                (String) comparison.getTestDetails()
                    .getValue()) <= DIAGRAM_SUFFIX_LENGTH) {
              return ComparisonResult.EQUAL;
            }
          }

          if (outcome == ComparisonResult.DIFFERENT
              && comparison.getType() == ComparisonType.ATTR_VALUE
              && comparison.getControlDetails().getXPath().endsWith("@id")) {
            if (levenshteinDistanceate(
                (String) comparison.getControlDetails().getValue(),
                (String) comparison.getTestDetails()
                    .getValue()) <= DIAGRAM_SUFFIX_LENGTH) {
              return ComparisonResult.EQUAL;
            }
          }

          if (outcome == ComparisonResult.DIFFERENT
              && comparison.getType() == ComparisonType.ATTR_VALUE && comparison
                  .getControlDetails().getXPath().endsWith("@description")) {
            if (levenshteinDistanceate(
                (String) comparison.getControlDetails().getValue(),
                (String) comparison.getTestDetails()
                    .getValue()) <= DIAGRAM_SUFFIX_LENGTH) {
              return ComparisonResult.EQUAL;
            }
          }

          return outcome;
        })).build();
    assertFalse(diff.toString(), diff.hasDifferences());
  }

  protected void addBendpoint(WithShapeGraphitiConnection connection, int x,
      int y) {
    connection.getConnection().filter(SimpleConnectionShape.class::isInstance)
        .map(SimpleConnectionShape.class::cast)
        .map(SimpleConnectionShape::getBendpoints)
        .ifPresent(list -> ModelUtil.runModelChange(
            () -> list.add(Graphiti.getGaService().createPoint(x, y)),
            ((RuminaqEditor) diagramEditor.getEditorPart()).getDiagramBehavior()
                .getEditingDomain(),
            ""));
  }

}
