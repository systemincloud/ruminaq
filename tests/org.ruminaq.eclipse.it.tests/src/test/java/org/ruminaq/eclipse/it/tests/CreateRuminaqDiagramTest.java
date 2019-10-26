/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.it.tests;

import static java.text.MessageFormat.format;
import static org.junit.Assert.assertEquals;

import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.navigator.resources.ProjectExplorer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ruminaq.eclipse.editor.EditorLinkHelper;
import org.ruminaq.eclipse.wizards.diagram.CreateDiagramWizard;
import org.ruminaq.eclipse.wizards.project.SourceFolders;
import org.ruminaq.tests.common.CreateRuminaqDiagram;
import org.ruminaq.tests.common.CreateRuminaqProject;
import org.ruminaq.tests.common.ProjectExplorerHandler;
import org.ruminaq.tests.common.SelectView;

/**
 * Test of creating a new eclipse project.
 *
 * @author Marek Jagielski
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class CreateRuminaqDiagramTest {

  private static SWTWorkbenchBot bot;

  /**
   * Initialize SWTBot.
   *
   */
  @BeforeClass
  public static void initBot() {
    bot = new SWTWorkbenchBot();
    SelectView.closeWelcomeViewIfExists(bot);
  }

  /**
   * Open new project wizard File => New.
   *
   * @param bot  SWTWorkbenchBot
   * @param name name of project
   */
  @AfterClass
  public static void after() {
    bot.resetWorkbench();
  }

  private static final int PROJECT_SUFFIX_LENGTH = 5;
  private static final int DIAGRAM_SUFFIX_LENGTH = 5;

  @Test
  public final void testCreateDiagram() throws InterruptedException {
    String projectName = "test"
        + RandomStringUtils.randomAlphabetic(PROJECT_SUFFIX_LENGTH);
    new CreateRuminaqProject().execute(bot, projectName);
    new CreateRuminaqProject().acceptPerspectiveChangeIfPopUps(bot);

    Thread.sleep(5000);

    String path = SourceFolders.DIAGRAM_FOLDER;

    String diagramName1 = "Diagram_"
        + RandomStringUtils.randomAlphabetic(DIAGRAM_SUFFIX_LENGTH);
    new CreateRuminaqDiagram().execute(bot, projectName, path, diagramName1);

    Thread.sleep(5000);

    new CreateRuminaqDiagram().waitUntilDiagramOpens(bot, diagramName1);

    String diagramName2 = "Diagram_"
        + RandomStringUtils.randomAlphabetic(DIAGRAM_SUFFIX_LENGTH);
    new CreateRuminaqDiagram().execute(bot, projectName, path, diagramName2);

    Thread.sleep(5000);

    new CreateRuminaqDiagram().waitUntilDiagramOpens(bot, diagramName2);

    bot.editorByTitle(diagramName2).setFocus();

    new ProjectExplorerHandler().select(bot, projectName, new String[0]);

    new ProjectExplorerHandler().show(bot);

    Thread.sleep(5000);

    ProjectExplorer explorerView = (ProjectExplorer) bot
        .viewById(IPageLayout.ID_PROJECT_EXPLORER).getViewReference()
        .getView(false);
    IStructuredSelection selection = (IStructuredSelection) explorerView
        .getCommonViewer().getSelection();
    IFile s = (IFile) selection.getFirstElement();
    assertEquals("File should be selected",
        format("/{0}/src/main/resources/tasks/{1}.rumi", projectName,
            diagramName2),
        s.getFullPath().toString());

    new ProjectExplorerHandler().select(bot, projectName,
        new String[] { SourceFolders.MAIN_RESOURCES, SourceFolders.TASK_FOLDER,
            diagramName1 + CreateDiagramWizard.DIAGRAM_EXTENSION_DOT });

    Thread.sleep(5000);

    assertEquals("First diagram should be selected", diagramName1, bot.editors()
        .stream().filter(e -> e.isActive()).findFirst().get().getTitle());

    new ProjectExplorerHandler().show(bot);

    new EditorLinkHelper().activateEditor(bot.activeView().getViewReference().getPage(),
        selection);

    Thread.sleep(5000);

    assertEquals("File should be selected",
        format("/{0}/src/main/resources/tasks/{1}.rumi", projectName,
            diagramName2),
        s.getFullPath().toString());

    bot.editorByTitle(diagramName2).close();

    Thread.sleep(5000);
  }
}
