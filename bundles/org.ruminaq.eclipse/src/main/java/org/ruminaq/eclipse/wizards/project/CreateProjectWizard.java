/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.wizards.project;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Properties;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.project.IProjectConfigurationManager;
import org.eclipse.m2e.core.project.MavenUpdateRequest;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.ruminaq.consts.Constants;
import org.ruminaq.eclipse.Image;
import org.ruminaq.eclipse.Messages;
import org.ruminaq.eclipse.RuminaqException;
import org.ruminaq.eclipse.api.EclipseExtension;
import org.ruminaq.eclipse.wizards.diagram.CreateDiagramWizardNamePage;
import org.ruminaq.logs.ModelerLoggerFactory;
import org.ruminaq.prefs.ProjectProps;
import org.ruminaq.util.EclipseUtil;
import org.ruminaq.util.ImageUtil;
import org.ruminaq.util.PlatformUtil;
import org.ruminaq.util.ServiceUtil;
import org.slf4j.Logger;

/**
 * Wizard to create new Ruminaq eclipse project.
 *
 * @author Marek Jagielski
 */
public class CreateProjectWizard extends BasicNewProjectResourceWizard {

  private static final Logger LOGGER = ModelerLoggerFactory
      .getLogger(CreateProjectWizard.class);

  private static final String BASIC_NEW_PROJECT_PAGE_NAME = "basicNewProjectPage";

  public static final String PROPERTIES_FILE = SourceFolders.MAIN_RESOURCES
      + "/ruminaq.properties";

  private static final String OUTPUT_CLASSES = "target/classes";

  private static final String EXTERNALTOOLBUILDERS = ".externalToolBuilders";

  private static final String BUILDER_CONFIG_MVN = IMavenConstants.BUILDER_ID
      + ".launch";

  private static final String MAIN_MODULE = "MAIN_MODULE";

  /**
   * New project wizard.
   *
   * @see org.eclipse.jface.wizard.Wizard#createPageControls(org.eclipse.swt.widgets.Composite)
   */
  @Override
  public void createPageControls(Composite pageContainer) {

    super.createPageControls(pageContainer);

    WizardNewProjectCreationPage basicNewProjectPage = (WizardNewProjectCreationPage) getPage(
        BASIC_NEW_PROJECT_PAGE_NAME);
    basicNewProjectPage.setTitle(Messages.createProjectWizardTitle);
    basicNewProjectPage.setImageDescriptor(
        ImageUtil.getImageDescriptor(Image.RUMINAQ_LOGO_64X64));
    basicNewProjectPage.setDescription(Messages.createProjectWizardDescription);
  }

  /**
   * Creating project. If smth wrong it shows modal window.
   *
   * @see org.eclipse.jface.wizard.Wizard#performFinish()
   */
  @Override
  public boolean performFinish() {
    if (super.performFinish()) {
      IProject newProject = getNewProject();

      try {
        Nature.setNatureIds(newProject);
        SourceFolders.createSourceFolders(newProject);

        IJavaProject javaProject = JavaCore.create(newProject);
        new JavaClasspathFile().setClasspathEntries(javaProject);
        createOutputLocation(javaProject);

        new PomFile().createPomFile(newProject);
        configureBuilders(newProject);
        createPropertiesFile(newProject);

        ProjectProps.getInstance(newProject).put(ProjectProps.MODELER_VERSION,
            PlatformUtil.getBundleVersion(this.getClass()).toString());

        deleteBinDirectory(newProject);

        ServiceUtil
        .getServicesAtLatestVersion(CreateProjectWizard.class,
            EclipseExtension.class).stream()
            .forEach(e -> e.createProjectWizardPerformFinish(javaProject));

        updateProject(newProject);

        return true;

      } catch (RuminaqException e) {
        LOGGER.error(Messages.createProjectWizardFailed, e);
        MessageDialog.openError(getShell(), Messages.ruminaqFailed,
            e.getMessage());
      }
    }

    return false;
  }

  /**
   * Newly created project by default has output directory bin. Eclipse will
   * manage to create this directory before we change after classpath file.
   *
   * @param projet eclipse project reference
   */
  private static void deleteBinDirectory(IProject projet) {
    try {
      EclipseUtil.deleteProjectDirectoryIfExists(projet,
          EclipseUtil.BIN_DIRECTORY);
    } catch (CoreException e) {
      throw new RuminaqException(Messages.createProjectWizardFailed, e);
    }
  }

  private static void updateProject(IProject project) {
    IProjectConfigurationManager configurationManager = MavenPlugin
        .getProjectConfigurationManager();
    MavenUpdateRequest request = new MavenUpdateRequest(project, true, true);
    try {
      configurationManager.updateProjectConfiguration(request,
          new NullProgressMonitor());
    } catch (CoreException e) {
      throw new RuminaqException(Messages.createProjectWizardFailed, e);
    }
  }

  /**
   * Sets the window title.
   *
   * @see org.eclipse.jface.wizard.Wizard#setWindowTitle()
   */
  @Override
  public void setWindowTitle(final String newTitle) {
    super.setWindowTitle(Messages.createProjectWizardWindowTitle);
  }

  private static void createOutputLocation(IJavaProject javaProject) {
    IPath targetPath = javaProject.getPath().append(OUTPUT_CLASSES);
    try {
      javaProject.setOutputLocation(targetPath, null);
    } catch (JavaModelException e) {
      throw new RuminaqException(Messages.createProjectWizardFailed, e);
    }
  }

  private static void configureBuilders(IProject project) {
    try {
      EclipseUtil.createFolderWithParents(project, EXTERNALTOOLBUILDERS);
    } catch (CoreException e) {
      throw new RuminaqException(Messages.createProjectWizardFailed, e);
    }

    try (InputStream confFile = CreateProjectWizard.class
        .getResourceAsStream(BUILDER_CONFIG_MVN)) {
      IFile outputFile = project.getFolder(EXTERNALTOOLBUILDERS)
          .getFile(BUILDER_CONFIG_MVN);
      outputFile.create(confFile, true, null);
      ICommand command = Arrays.stream(project.getDescription().getBuildSpec())
          .filter(
              cmd -> IMavenConstants.BUILDER_ID.equals(cmd.getBuilderName()))
          .findFirst().orElseThrow(IOException::new);
      command
          .setBuilderName("org.eclipse.ui.externaltools.ExternalToolBuilder");
      command.getArguments().put("LaunchConfigHandle",
          "<project>/.externalToolBuilders/org.eclipse.m2e.core.maven2Builder.launch");
    } catch (CoreException | IOException e) {
      throw new RuminaqException(Messages.createProjectWizardFailed, e);
    }
  }

  private static void createPropertiesFile(IProject newProject) {
    Properties prop = new Properties();
    try (OutputStream output = new ByteArrayOutputStream()) {
      prop.setProperty(MAIN_MODULE,
          SourceFolders.TASK_FOLDER + "/"
              + CreateDiagramWizardNamePage.DEFAULT_DIAGRAM_NAME
              + Constants.DIAGRAM_EXTENSION_DOT);
      prop.store(output, null);
      IFile outputFile = newProject.getFile(PROPERTIES_FILE);
      outputFile.create(new ByteArrayInputStream(
          ((ByteArrayOutputStream) output).toByteArray()), true, null);
    } catch (IOException | CoreException e) {
      throw new RuminaqException(Messages.createProjectWizardFailedPropertiesFile, e);
    }
  }
}
