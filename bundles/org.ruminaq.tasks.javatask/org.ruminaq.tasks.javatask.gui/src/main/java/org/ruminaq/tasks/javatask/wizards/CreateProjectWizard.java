package org.ruminaq.tasks.javatask.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.ruminaq.tasks.javatask.TaskApi;
import org.ruminaq.util.EclipseUtil;

public class CreateProjectWizard {

    public boolean performFinish(IJavaProject newProject) throws CoreException {
        createSourceFolders(newProject.getProject());
        return true;
    }

    private void createSourceFolders(IProject project) throws CoreException {
        EclipseUtil.createFolderWithParents  (project, TaskApi.MAIN_JAVA);
        EclipseUtil.createPlaceholderInFolder(project, TaskApi.MAIN_JAVA, "_FOR_JAVA");
        EclipseUtil.createFolderWithParents  (project, TaskApi.TEST_JAVA);
        EclipseUtil.createPlaceholderInFolder(project, TaskApi.TEST_JAVA, "_FOR_JAVA");
    }
}
