/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.it.tests;

import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ruminaq.eclipse.RuminaqPerspective;
import org.ruminaq.eclipse.wizards.project.CreateProjectWizard;
import org.ruminaq.eclipse.wizards.project.Nature;
import org.ruminaq.eclipse.wizards.project.PomFile;
import org.ruminaq.eclipse.wizards.project.SourceFolders;
import org.ruminaq.tests.common.CreateRuminaqProject;
import org.ruminaq.tests.common.SelectView;

/**
 * Test of creating a new eclipse project.
 *
 * @author Marek Jagielski
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class CreateRuminaqProjectTest {

  private static SWTWorkbenchBot bot;
  private static IWorkbench workbench;
  private static IWorkspace workspace;

  /**
   * Initialize SWTBot.
   *
   */
  @BeforeClass
  public static void initBot() {
    bot = new SWTWorkbenchBot();
    workbench = PlatformUI.getWorkbench();
    workspace = ResourcesPlugin.getWorkspace();
    SelectView.closeWelcomeViewIfExists(bot);
  }

  @AfterClass
  public static void after() {
    bot.resetWorkbench();
  }

  private static final int PROJECT_SUFFIX_LENGTH = 5;

  private IPerspectiveDescriptor perspective;

  @Test
  public final void testCreateProject()
      throws CoreException, IOException, XmlPullParserException {
    String projectName = "test"
        + RandomStringUtils.randomAlphabetic(PROJECT_SUFFIX_LENGTH);
    new CreateRuminaqProject().execute(bot, projectName);
    new CreateRuminaqProject().acceptPerspectiveChangeIfPopUps(bot);

    Display.getDefault().syncExec(new Runnable() {
      @Override
      public void run() {
        perspective = workbench.getActiveWorkbenchWindow().getActivePage()
            .getPerspective();
      }
    });
    Assert.assertEquals("Perspective should be changed",
        RuminaqPerspective.class.getCanonicalName(),
        perspective.getId());

    IProject project = workspace.getRoot().getProject(projectName);

    Arrays
        .asList(SourceFolders.MAIN_RESOURCES, SourceFolders.TEST_RESOURCES,
            SourceFolders.TASK_FOLDER, SourceFolders.DIAGRAM_FOLDER)
        .stream()
        .forEach(f -> Assert.assertTrue(
            "Source directory " + f + "should be created",
            project.getFolder(f).exists()));

    Assert.assertTrue("Workspace nature should change to Ruminaq",
        project.hasNature(Nature.ID));

    Assert.assertTrue("Property file created",
        project.exists(new Path(CreateProjectWizard.PROPERTIES_FILE)));

    var pom = new Path(PomFile.POM_FILE_PATH);
    Assert.assertTrue("Pom file created", project.exists(pom));

    var reader = new MavenXpp3Reader();
    Model model = reader.read(project.getFile(pom).getContents());

    Assert.assertEquals("GroupId is set", "org.examples", model.getGroupId());
  }
}
