/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.it.tests;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.waits.ICondition;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ruminaq.eclipse.RuminaqPerspective;
import org.ruminaq.eclipse.RuminaqProjectNature;
import org.ruminaq.eclipse.wizards.diagram.CreateDiagramWizard;
import org.ruminaq.eclipse.wizards.diagram.CreateDiagramWizardNamePage;
import org.ruminaq.eclipse.wizards.project.CreatePomFile;
import org.ruminaq.eclipse.wizards.project.CreateProjectWizard;
import org.ruminaq.eclipse.wizards.project.CreateSourceFolders;
import org.ruminaq.tests.common.CreateRuminaqProject;
import org.ruminaq.tests.common.SelectView;

/**
 * Test of creating a new eclipse project.
 *
 * @author Marek Jagielski
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class CreateRuminaqProjectTest {

  private static final int PROJECT_SUFFIX_LENGTH = 5;

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

  @Test
  public final void testCreateProject() throws CoreException, IOException,
      XmlPullParserException, InterruptedException {
    String projectName = "test"
        + RandomStringUtils.randomAlphabetic(PROJECT_SUFFIX_LENGTH);
    new CreateRuminaqProject().execute(bot, projectName);
    new CreateRuminaqProject().acceptPerspectiveChangeIfPopUps(bot);

    bot.waitUntil(new ICondition() {

      @Override
      public boolean test() {
        return Optional.of(workbench).map(IWorkbench::getActiveWorkbenchWindow)
            .filter(Objects::nonNull).map(IWorkbenchWindow::getActivePage)
            .map(IWorkbenchPage::getPerspective)
            .map(IPerspectiveDescriptor::getId)
            .filter(RuminaqPerspective.class.getCanonicalName()::equals)
            .isEmpty();
      }

      @Override
      public void init(SWTBot bot) {
        // not needed
      }

      @Override
      public String getFailureMessage() {
        return null;
      }
    });

    IProject project = workspace.getRoot().getProject(projectName);
    project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());

    Arrays.asList(CreateSourceFolders.MAIN_RESOURCES,
        CreateSourceFolders.TEST_RESOURCES, CreateSourceFolders.DIAGRAM_FOLDER)
        .stream()
        .forEach(f -> Assert.assertTrue(
            "Source directory " + f + "should be created",
            project.getFolder(f).exists()));

    Assert.assertTrue("Workspace nature should change to Ruminaq",
        project.hasNature(RuminaqProjectNature.ID));

    Thread.sleep(5000);

    IFile propertyFile = project.getFile(CreateProjectWizard.PROPERTIES_FILE);
    Properties prop = new Properties();
    prop.load(propertyFile.getContents());
    Assert.assertEquals("Main module specified",
        CreateSourceFolders.TASK_FOLDER + "/"
            + CreateDiagramWizardNamePage.DEFAULT_DIAGRAM_NAME
            + CreateDiagramWizard.DIAGRAM_EXTENSION_DOT,
        prop.get(CreateProjectWizard.MAIN_MODULE));

    Assert.assertTrue("Property file created", propertyFile.exists());

    Path pom = new Path(CreatePomFile.POM_FILE_PATH);
    Assert.assertTrue("Pom file created", project.exists(pom));

    var reader = new MavenXpp3Reader();
    Model model = reader.read(project.getFile(pom).getContents());

    Assert.assertEquals("GroupId is set", "org.examples", model.getGroupId());
  }
}
