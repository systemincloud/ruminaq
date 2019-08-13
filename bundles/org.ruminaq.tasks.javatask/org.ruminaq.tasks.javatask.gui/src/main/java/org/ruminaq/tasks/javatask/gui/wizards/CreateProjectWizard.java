package org.ruminaq.tasks.javatask.gui.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.ruminaq.tasks.javatask.gui.EclipseExtensionImpl;
import org.ruminaq.util.EclipseUtil;

public class CreateProjectWizard {

    public boolean performFinish(IJavaProject newProject) throws CoreException {
        createSourceFolders(newProject.getProject());
        return true;
    }

    private void createSourceFolders(IProject project) throws CoreException {
        EclipseUtil.createFolderWithParents  (project, EclipseExtensionImpl.MAIN_JAVA);
        EclipseUtil.createPlaceholderInFolder(project, EclipseExtensionImpl.MAIN_JAVA, "_FOR_JAVA");
        EclipseUtil.createFolderWithParents  (project, EclipseExtensionImpl.TEST_JAVA);
        EclipseUtil.createPlaceholderInFolder(project, EclipseExtensionImpl.TEST_JAVA, "_FOR_JAVA");
    }
}
