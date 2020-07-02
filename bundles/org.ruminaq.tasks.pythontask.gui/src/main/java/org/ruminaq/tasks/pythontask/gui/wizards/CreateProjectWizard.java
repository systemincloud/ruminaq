/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.pythontask.gui.wizards;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.python.pydev.ast.interpreter_managers.InterpreterManagersAPI;
import org.python.pydev.plugin.nature.PythonNature;
import org.ruminaq.tasks.pythontask.gui.EclipseExtensionImpl;
import org.ruminaq.tasks.pythontask.impl.PythonTaskI;
import org.ruminaq.util.EclipseUtil;

public class CreateProjectWizard {

  private static final String PYDEVPROJECT = ".pydevproject";

  public boolean performFinish(IJavaProject newProject) throws CoreException {
    setNatureIds(newProject.getProject());
    addPyBuilder(newProject.getProject());
    createSourceFolders(newProject.getProject());
    // createPyDevProjectFile(newProject.getProject());
    return true;
  }

  public List<IClasspathEntry> createClasspathEntries(
      IJavaProject javaProject) {
    List<IClasspathEntry> entries = new LinkedList<>();
    IPath srcPath1 = javaProject.getPath().append(EclipseExtensionImpl.MAIN_PYTHON);
    IPath srcPath2 = javaProject.getPath().append(EclipseExtensionImpl.TEST_PYTHON);
    entries.add(JavaCore.newSourceEntry(srcPath1,
        new IPath[] { new Path("**/__pycache__/**") }));
    entries.add(JavaCore.newSourceEntry(srcPath2,
        new IPath[] { new Path("**/__pycache__/**") }));
    return entries;
  }

  private void setNatureIds(IProject newProject) throws CoreException {
    IProjectDescription description = newProject.getDescription();
    description.setNatureIds(ArrayUtils.add(description.getNatureIds(),
        PythonNature.PYTHON_NATURE_ID));
    newProject.setDescription(description, null);
  }

  private void addPyBuilder(IProject project) throws CoreException {
    IProjectDescription description = project.getDescription();
    ICommand[] commands = description.getBuildSpec();
    ICommand[] newCommands = new ICommand[commands.length + 1];

    int j = 0;
    for (int i = 0; i < commands.length; i++)
      newCommands[j++] = commands[i];

    ICommand command = description.newCommand();

    command.setBuilderName("org.python.pydev.PyDevBuilder");

    newCommands[newCommands.length - 1] = command;

    description.setBuildSpec(newCommands);

    project.setDescription(description, null);
  }

  private void createSourceFolders(IProject project) throws CoreException {
    EclipseUtil.createFolderWithParents(project, EclipseExtensionImpl.MAIN_PYTHON);
    EclipseUtil.createFileInFolder(project, EclipseExtensionImpl.MAIN_PYTHON,
        "PLACEHOLDER_FOR_PY");
    EclipseUtil.createFolderWithParents(project, EclipseExtensionImpl.TEST_PYTHON);
    EclipseUtil.createFileInFolder(project, EclipseExtensionImpl.TEST_PYTHON,
        "PLACEHOLDER_FOR_PY");
  }

  private void createPyDevProjectFile(IProject project) throws CoreException {
    String pythonType = InterpreterManagersAPI.getPythonInterpreterManager(true)
        .getInterpreterInfos().length > 0 ? "python" : "jython";
    try {
      String content = IOUtils
          .toString(CreateProjectWizard.class.getResourceAsStream(PYDEVPROJECT))
          .replace("${process-version}", PythonTaskI.PROCESS_LIB_VERSION)
          .replace("${python-type}", pythonType);
      // TODO:content =
      // PythonTaskExtensionManager.INSTANCE.editPyDevProjectFile(content);
      IFile outputFile = project.getFile(PYDEVPROJECT);
      outputFile.create(IOUtils.toInputStream(content), true, null);
    } catch (IOException e) {
    }

  }
}
