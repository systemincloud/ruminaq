/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.wizards.project;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.IProjectConfigurationManager;
import org.eclipse.m2e.core.project.MavenUpdateRequest;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.osgi.framework.Version;
import org.ruminaq.eclipse.Image;
import org.ruminaq.eclipse.Messages;
import org.ruminaq.eclipse.RuminaqException;
import org.ruminaq.eclipse.api.EclipseExtension;
import org.ruminaq.eclipse.wizards.diagram.CreateDiagramWizard;
import org.ruminaq.eclipse.wizards.diagram.CreateDiagramWizardNamePage;
import org.ruminaq.logs.ModelerLoggerFactory;
import org.ruminaq.prefs.ProjectProps;
import org.ruminaq.util.EclipseUtil;
import org.ruminaq.util.ImageUtil;
import org.ruminaq.util.PlatformUtil;
import org.ruminaq.util.ServiceUtil;
import org.ruminaq.util.Try;
import org.slf4j.Logger;

/**
 * Wizard to create new Ruminaq eclipse project.
 *
 * @author Marek Jagielski
 */
public class CreateProjectWizard extends BasicNewProjectResourceWizard {

  private static final Logger LOGGER = ModelerLoggerFactory
      .getLogger(CreateProjectWizard.class);

  public static final String ID = CreateProjectWizard.class.getCanonicalName();

  private static final String BASIC_NEW_PROJECT_PAGE_NAME = "basicNewProjectPage";

  public static final String PROPERTIES_FILE = CreateSourceFolders.MAIN_RESOURCES
      + "/ruminaq.properties";

  private static final String OUTPUT_CLASSES = "target/classes";

  public static final String MAIN_MODULE = "MAIN_MODULE";

  public static final String BIN_DIRECTORY = "bin";

  private final List<Function<IProject, Try<RuminaqException>>> executors = Arrays
      .asList(SetNature::execute, CreateSourceFolders::execute,
          CreateProjectWizard::createOutputLocation, CreatePomFile::execute,
          AddBuilders::execute, CreateProjectWizard::createPropertiesFile,
          this::setProjectProps, CreateProjectWizard::deleteBinDirectory,
          CreateProjectWizard::extensions, CreateProjectWizard::updateProject);

  /**
   * Sets the window title.
   *
   * @see org.eclipse.jface.wizard.Wizard#setWindowTitle()
   */
  @Override
  public void setWindowTitle(final String newTitle) {
    super.setWindowTitle(Messages.createProjectWizardWindowTitle);
  }

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
      executors.stream().map(r -> r.apply(newProject)).filter(Try::isFailed)
          .findFirst().ifPresent((Try<RuminaqException> r) -> {
            LOGGER.error(Messages.createProjectWizardFailed, r.getError());
            MessageDialog.openError(getShell(), Messages.ruminaqFailed,
                r.getError().getMessage());
          });
    }

    return true;
  }

  /**
   * Newly created project by default has output directory bin. Eclipse will
   * manage to create this directory before we change classpath file.
   *
   * @param projet eclipse project reference
   */
  private static Try<RuminaqException> deleteBinDirectory(IProject projet) {
    return EclipseUtil.deleteProjectDirectoryIfExists(projet, BIN_DIRECTORY)
        .wrapError(
            e -> new RuminaqException(Messages.createProjectWizardFailed, e));
  }

  private static Try<RuminaqException> updateProject(IProject project) {
    IProjectConfigurationManager configurationManager = MavenPlugin
        .getProjectConfigurationManager();
    MavenUpdateRequest request = new MavenUpdateRequest(project, true, true);
    return Try
        .check(() -> configurationManager.updateProjectConfiguration(request,
            new NullProgressMonitor()))
        .wrapError(
            e -> new RuminaqException(Messages.createProjectWizardFailed, e));
  }

  private static Try<RuminaqException> createOutputLocation(IProject project) {
    IJavaProject javaProject = JavaCore.create(project);
    new JavaClasspathFile().setClasspathEntries(javaProject);
    IPath targetPath = javaProject.getPath().append(OUTPUT_CLASSES);
    return Try.check(() -> javaProject.setOutputLocation(targetPath, null))
        .<RuminaqException>wrapError(
            e -> new RuminaqException(Messages.createProjectWizardFailed, e));
  }

  private static Try<RuminaqException> createPropertiesFile(IProject project) {
    Properties prop = new Properties();
    try (OutputStream output = new ByteArrayOutputStream()) {
      prop.setProperty(MAIN_MODULE,
          CreateSourceFolders.TASK_FOLDER + "/"
              + CreateDiagramWizardNamePage.DEFAULT_DIAGRAM_NAME
              + CreateDiagramWizard.DIAGRAM_EXTENSION_DOT);
      prop.store(output, null);
      IFile outputFile = project.getFile(PROPERTIES_FILE);
      outputFile.create(
          new ByteArrayInputStream(
              ((ByteArrayOutputStream) output).toByteArray()),
          true, new NullProgressMonitor());
    } catch (IOException | CoreException e) {
      return Try.crash(new RuminaqException(
          Messages.createProjectWizardFailedPropertiesFile, e));
    }
    return Try.success();
  }

  private Try<RuminaqException> setProjectProps(IProject project) {
    Version version = PlatformUtil.getBundleVersion(this.getClass());
    ProjectProps.getInstance(project).put(ProjectProps.RUMINAQ_VERSION,
        new Version(version.getMajor(), version.getMinor(), version.getMicro())
            .toString());
    return Try.success();
  }

  private static Try<RuminaqException> extensions(IProject project) {
    ServiceUtil
        .getServicesAtLatestVersion(CreateProjectWizard.class,
            EclipseExtension.class)
        .stream().forEach(e -> e.createProjectWizardPerformFinish(project));
    return Try.success();
  }
}
