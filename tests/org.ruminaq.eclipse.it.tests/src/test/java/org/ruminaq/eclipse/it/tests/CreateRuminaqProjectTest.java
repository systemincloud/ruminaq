/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.it.tests;

import java.util.Collection;

import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ruminaq.eclipse.it.tests.actions.CreateRuminaqProject;
import org.ruminaq.eclipse.it.tests.api.EclipseTestExtension;
import org.ruminaq.eclipse.wizards.project.CreateProjectWizard;
import org.ruminaq.eclipse.wizards.project.Nature;
import org.ruminaq.util.ServiceUtil;

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
  private static Collection<EclipseTestExtension> extensions;

  /**
   * Initialize SWTBot.
   *
   */
  @Before
  public void initBot() {
    bot = new SWTWorkbenchBot();
    workbench = PlatformUI.getWorkbench();
    workspace = ResourcesPlugin.getWorkspace();
    extensions = ServiceUtil.getServicesAtLatestVersion(
        CreateRuminaqProjectTest.class, EclipseTestExtension.class);
  }

  @After
  public void afterClass() {
    bot.resetWorkbench();
  }

  private static final int PROJECT_SUFFIX_LENGTH = 5;

  private IPerspectiveDescriptor perspective;

  @Test
  public final void testCreateProject() throws CoreException {
    String projectName = "test"
        + RandomStringUtils.randomAlphabetic(PROJECT_SUFFIX_LENGTH);
    new CreateRuminaqProject().execute(bot, projectName);
    new CreateRuminaqProject().acceptPerspectiveChange(bot);

    Display.getDefault().syncExec(new Runnable() {
      @Override
      public void run() {
        perspective = workbench.getActiveWorkbenchWindow().getActivePage()
            .getPerspective();
      }
    });
    Assert.assertEquals("Perspective should be changed",
        "org.ruminaq.eclipse.perspective.RuminaqPerspective",
        perspective.getId());

    IProject project = workspace.getRoot().getProject(projectName);

    Assert.assertTrue("Workspace nature should change to Ruminaq",
        project.hasNature(Nature.NATURE_ID));

    Assert.assertTrue("Property file created",
        project.exists(new Path(CreateProjectWizard.PROPERTIES_FILE)));

    extensions.stream().forEach(e -> e.verifyProject(project));
  }

  @Test
  public final void testCreateProjectFailed() throws CoreException {
    String projectName = "test"
        + RandomStringUtils.randomAlphabetic(PROJECT_SUFFIX_LENGTH);
    System.setProperty(
        JavaProjectAspect.FAIL_CREATE_OUTPUT_LOCATION_PROJECT_NAME,
        projectName);
    new CreateRuminaqProject().execute(bot, projectName);
    new CreateRuminaqProject().acceptPerspectiveChange(bot);

    SWTBotShell failureWindow = bot.shell("Ruminaq failure");
    failureWindow.activate();

    Assert.assertNotNull("Failure window should appear", failureWindow);
  }
}
