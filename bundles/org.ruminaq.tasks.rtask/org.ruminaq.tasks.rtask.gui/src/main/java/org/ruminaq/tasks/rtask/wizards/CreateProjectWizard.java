/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.rtask.wizards;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.ruminaq.tasks.rtask.EclipseExtensionImpl;
import org.ruminaq.util.EclipseUtil;

public class CreateProjectWizard {

  public boolean performFinish(IProject newProject) throws CoreException {
    setNatureIds(newProject);
    addRBuilder(newProject);
    createSourceFolders(newProject);
    return true;
  }

  public List<IClasspathEntry> createClasspathEntries(
      IJavaProject javaProject) {
    List<IClasspathEntry> entries = new LinkedList<>();
    IPath srcPath1 = javaProject.getPath().append(EclipseExtensionImpl.MAIN_R);
    IPath srcPath2 = javaProject.getPath().append(EclipseExtensionImpl.TEST_R);
    entries.add(JavaCore.newSourceEntry(srcPath1, new IPath[0]));
    entries.add(JavaCore.newSourceEntry(srcPath2, new IPath[0]));
    return entries;
  }

  private void setNatureIds(IProject newProject) throws CoreException {
    IProjectDescription description = newProject.getDescription();
//    String[] ns = ArrayUtils.add(description.getNatureIds(),
//        StatetProject.NATURE_ID);
//    ns = ArrayUtils.add(ns, RProjects.R_NATURE_ID);
//    description.setNatureIds(ns);
    newProject.setDescription(description, null);
  }

  private void addRBuilder(IProject project) throws CoreException {
    IProjectDescription description = project.getDescription();
    ICommand[] commands = description.getBuildSpec();
    ICommand[] newCommands = new ICommand[commands.length + 1];

    int j = 0;
    for (int i = 0; i < commands.length; i++)
      newCommands[j++] = commands[i];

    ICommand command = description.newCommand();

    command.setBuilderName("de.walware.statet.r.builders.RSupport");

    newCommands[newCommands.length - 1] = command;

    description.setBuildSpec(newCommands);

    project.setDescription(description, null);
  }

  private void createSourceFolders(IProject project) throws CoreException {
    EclipseUtil.createFolderWithParents(project, EclipseExtensionImpl.MAIN_R);
    EclipseUtil.createFileInFolder(project, EclipseExtensionImpl.MAIN_R,
        "PLACEHOLDER_FOR_R");
    EclipseUtil.createFolderWithParents(project, EclipseExtensionImpl.TEST_R);
    EclipseUtil.createFileInFolder(project, EclipseExtensionImpl.TEST_R,
        "PLACEHOLDER_FOR_R");
  }
}
