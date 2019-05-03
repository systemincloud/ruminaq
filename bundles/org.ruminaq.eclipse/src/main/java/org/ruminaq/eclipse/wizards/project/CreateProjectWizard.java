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
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.IProjectConfigurationManager;
import org.eclipse.m2e.core.project.MavenUpdateRequest;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.jdom2.JDOMException;
import org.osgi.service.component.annotations.Reference;

import org.ruminaq.consts.Constants;
import org.ruminaq.eclipse.Activator;
import org.ruminaq.eclipse.PluginImage;
import org.ruminaq.eclipse.api.EclipseExtensionHandler;
import org.ruminaq.eclipse.wizards.diagram.CreateDiagramWizardNamePage;
import org.ruminaq.logs.ModelerLoggerFactory;
import org.ruminaq.prefs.ProjectProps;

import org.slf4j.Logger;

/**
 * Wizard to create new Reminaq eclipse project.
 *
 * @author Marek Jagielski
 */
public class CreateProjectWizard extends BasicNewProjectResourceWizard {

  private static final Logger LOGGER = ModelerLoggerFactory
      .getLogger(CreateProjectWizard.class);

  @Reference
  private EclipseExtensionHandler extensions;

  private static final String BASIC_NEW_PROJECT_PAGE_NAME = "basicNewProjectPage";

  public static final String PROPERTIES_FILE = "src/main/resources/ruminaq.properties";

  /**
   * New project wizzard.
   *
   * @see org.eclipse.jface.wizard.Wizard#createPageControls(org.eclipse.swt.widgets.Composite)
   */
  @Override
  public void createPageControls(Composite pageContainer) {

    super.createPageControls(pageContainer);

    final WizardNewProjectCreationPage basicNewProjectPage = getBasicNewProjectPage();
    if (basicNewProjectPage != null) {
      basicNewProjectPage.setTitle("Create Ruminaq Project");
      basicNewProjectPage.setImageDescriptor(
          Activator.getImageDescriptor(PluginImage.RUMINAQ_LOGO_64x64));
      basicNewProjectPage
          .setDescription("Create Ruminaq Project in the workspace.");
    }
  }

  /**
   * Creating project.
   *
   * @see org.eclipse.jface.wizard.Wizard#performFinish()
   */
  @Override
  public boolean performFinish() {
    if (!super.performFinish()) {
      return false;
    }

    IProject newProject = getNewProject();

    try {
      setNatureIds(newProject);
      SourceFolders.createSourceFolders(newProject);

      IJavaProject javaProject = JavaCore.create(newProject);
      new JavaClasspathFile().setClasspathEntries(javaProject);
      createOutputLocation(javaProject);

      new PomFile().createPomFile(newProject);
      configureBuilders(newProject);
      createPropertiesFile(newProject);

      ProjectProps.getInstance(newProject).put(ProjectProps.MODELER_VERSION,
          Activator.getBaseVersionString());

      // delete bin directory if created
      IFolder bin = newProject.getFolder("bin");
      if (bin.exists()) {
        bin.delete(true, new NullProgressMonitor());
      }

      extensions.createProjectWizardPerformFinish(javaProject);

      IProjectConfigurationManager configurationManager = MavenPlugin
          .getProjectConfigurationManager();
      MavenUpdateRequest request = new MavenUpdateRequest(newProject, true,
          true);
      configurationManager.updateProjectConfiguration(request,
          new NullProgressMonitor());
    } catch (CoreException | JDOMException | IOException e) {
      LOGGER.error("Something went wrong when creating project", e);
      return false;
    }

    return true;
  }

  @Override
  public void setWindowTitle(final String newTitle) {
    super.setWindowTitle("New System in Cloud Project");
  }

  /**
   * Gets the WizardNewProjectCreationPage from the Wizard, which is the first
   * page allowing the user to specify the project name and location.
   */
  private WizardNewProjectCreationPage getBasicNewProjectPage() {

    WizardNewProjectCreationPage result = null;

    final IWizardPage page = getPage(BASIC_NEW_PROJECT_PAGE_NAME);
    if (page instanceof WizardNewProjectCreationPage) {
      result = (WizardNewProjectCreationPage) page;
    }
    return result;
  }

  private static void setNatureIds(IProject newProject) throws CoreException {
    IProjectDescription description = newProject.getDescription();
    description.setNatureIds(new String[] { JavaCore.NATURE_ID,
        Constants.NATURE_ID, Constants.MAVEN_NATURE_ID });
    newProject.setDescription(description, null);
  }

  private static void createOutputLocation(IJavaProject javaProject)
      throws JavaModelException {
    IPath targetPath = javaProject.getPath().append(Constants.OUTPUT_CLASSES);
    javaProject.setOutputLocation(targetPath, null);
  }

  private void configureBuilders(IProject newProject) throws CoreException {
//        EclipseUtil.createFolderWithParents(newProject, SicConstants.EXTERNALTOOLBUILDERS);
//
//        OutputStream output = null;
//        try {
//            output = new ByteArrayOutputStream();
//            IFile outputFile = newProject.getFolder(SicConstants.EXTERNALTOOLBUILDERS).getFile(BUILDER_CONFIG_MVN);
//            outputFile.create(CreateProjectWizard.class.getResourceAsStream(BUILDER_CONFIG_MVN), true, null);
//        } catch (CoreException e) { e.printStackTrace();
//        } finally {
//            if (output != null)
//                try { output.close();
//                } catch (IOException e) {e.printStackTrace(); }
//        }
//
//        IProjectDescription description = newProject .getDescription();
//        ICommand[] commands             = description.getBuildSpec();
//        ICommand[] newCommands          = new ICommand[commands.length];
//
//        int j = 0;
//        for(int i = 0; i < commands.length; i++)
//            if(!commands[i].getBuilderName().equals("org.eclipse.m2e.core.maven2Builder")) newCommands[j++] = commands[i];
//
//        ICommand command = description.newCommand();
//
//        command.setBuilderName("org.eclipse.ui.externaltools.ExternalToolBuilder");
//        command.setArguments(new HashMap<String, String>() { private static final long serialVersionUID = 1L;
//            { put("LaunchConfigHandle", "<project>/.externalToolBuilders/org.eclipse.m2e.core.maven2Builder.launch");    }});
//
//        newCommands[newCommands.length - 1] = command;
//
//        description.setBuildSpec(newCommands);
//
//        newProject.setDescription(description, null);
  }

  private static void createPropertiesFile(IProject newProject) {
    Properties prop = new Properties();
    try (OutputStream output = new ByteArrayOutputStream()) {
      prop.setProperty("MAIN_MODULE",
          SourceFolders.TASK_FOLDER + "/"
              + CreateDiagramWizardNamePage.DEFAULT_DIAGRAM_NAME
              + Constants.DIAGRAM_EXTENSION_DOT);
      prop.store(output, null);
      IFile outputFile = newProject.getFile(PROPERTIES_FILE);
      outputFile.create(new ByteArrayInputStream(
          ((ByteArrayOutputStream) output).toByteArray()), true, null);
    } catch (IOException | CoreException e) {
      LOGGER.error("Could not create property file", e);
      MessageDialog.openError(
          PlatformUI.getWorkbench().getModalDialogShellProvider().getShell(),
          "Error", "Error occured");
    }
  }
}
