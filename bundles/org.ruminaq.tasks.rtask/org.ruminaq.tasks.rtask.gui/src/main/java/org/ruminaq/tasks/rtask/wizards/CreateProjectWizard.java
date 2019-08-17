package org.ruminaq.tasks.rtask.wizards;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.ruminaq.consts.Constants;
import org.ruminaq.util.EclipseUtil;

import de.walware.statet.base.core.StatetProject;
import de.walware.statet.r.core.RProjects;

public class CreateProjectWizard {

  public boolean performFinish(IJavaProject newProject) throws CoreException {
    setNatureIds(newProject.getProject());
    addRBuilder(newProject.getProject());
    createSourceFolders(newProject.getProject());
    return true;
  }

  public List<IClasspathEntry> createClasspathEntries(
      IJavaProject javaProject) {
    List<IClasspathEntry> entries = new LinkedList<>();
    IPath srcPath1 = javaProject.getPath().append(Constants.MAIN_R);
    IPath srcPath2 = javaProject.getPath().append(Constants.TEST_R);
    entries.add(JavaCore.newSourceEntry(srcPath1, new IPath[0]));
    entries.add(JavaCore.newSourceEntry(srcPath2, new IPath[0]));
    return entries;
  }

  private void setNatureIds(IProject newProject) throws CoreException {
    IProjectDescription description = newProject.getDescription();
    String[] ns = ArrayUtils.add(description.getNatureIds(),
        StatetProject.NATURE_ID);
    ns = ArrayUtils.add(ns, RProjects.R_NATURE_ID);
    description.setNatureIds(ns);
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
    EclipseUtil.createFolderWithParents(project, Constants.MAIN_R);
    EclipseUtil.createFileInFolder(project, Constants.MAIN_R,
        "PLACEHOLDER_FOR_R");
    EclipseUtil.createFolderWithParents(project, Constants.TEST_R);
    EclipseUtil.createFileInFolder(project, Constants.TEST_R,
        "PLACEHOLDER_FOR_R");
  }
}
