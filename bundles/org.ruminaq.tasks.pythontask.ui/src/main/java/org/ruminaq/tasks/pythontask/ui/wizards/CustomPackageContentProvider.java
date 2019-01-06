package org.ruminaq.tasks.pythontask.ui.wizards;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.python.pydev.core.IPythonPathNature;
import org.python.pydev.plugin.nature.PythonNature;
import org.python.pydev.ui.dialogs.Package;
import org.python.pydev.ui.dialogs.SourceFolder;

public class CustomPackageContentProvider implements ITreeContentProvider {

    private IProject project;
    private boolean  selectOnlySourceFolders;

    public CustomPackageContentProvider(IProject project, boolean selectOnlySourceFolders) {
        this.project = project;
        this.selectOnlySourceFolders = selectOnlySourceFolders;
    }

    public Object[] getChildren(Object parentElement) {

        if(parentElement instanceof IWorkspaceRoot) {
            IWorkspaceRoot workspaceRoot = (IWorkspaceRoot) parentElement;
            List<IProject> ret = new ArrayList<IProject>();

            IProject[] projects = workspaceRoot.getProjects();
            for (IProject project : projects)
                if(project == this.project) ret.add(project);
            return ret.toArray(new IProject[0]);
        }

        if (parentElement instanceof IProject) {
            List<Object> ret = new ArrayList<Object>();
            IProject project = (IProject) parentElement;
            if(project == this.project) {
                try {
                    IPythonPathNature nature = PythonNature.getPythonPathNature(project);
                    String[] srcPaths = PythonNature.getStrAsStrItems(nature.getProjectSourcePath(true));
                    for(String str : srcPaths) {
                        IResource resource = project.getWorkspace().getRoot().findMember(new Path(str));
                        if(resource instanceof IFolder) {
                            IFolder folder = (IFolder) resource;
                            if(folder.exists())
                                if(folder != null) ret.add(new SourceFolder(folder));
                        }
                        if (resource instanceof IProject) {
                            IProject folder = (IProject) resource;
                            if (folder.exists())
                                if (folder != null) ret.add(new SourceFolder(folder));
                        }
                    }
                    return ret.toArray();
                } catch (CoreException e) {
                }
            }
        }

        SourceFolder sourceFolder = null;
        if(parentElement instanceof SourceFolder) {
            sourceFolder = (SourceFolder) parentElement;
            parentElement = ((SourceFolder) parentElement).folder;
        }

        if(parentElement instanceof Package) {
            sourceFolder = ((Package) parentElement).sourceFolder;
            parentElement = ((Package) parentElement).folder;
        }

        if(parentElement instanceof IFolder) {
            IFolder f = (IFolder) parentElement;
            ArrayList<Package> ret = new ArrayList<Package>();
            try {
                IResource[] resources = f.members();
                for (IResource resource : resources)
                    if (resource instanceof IFolder) ret.add(new Package((IFolder) resource, sourceFolder));
            } catch (CoreException e) {
            }
            return ret.toArray();
        }

        return new Object[0];
    }

    public Object getParent(Object element) {
        if (element instanceof Package)   return ((Package) element).sourceFolder;
        if (element instanceof IResource) return ((IResource) element).getParent();
        return null;
    }

    public boolean hasChildren(Object element) {
        if(selectOnlySourceFolders && element instanceof SourceFolder) return false;
        else                                                           return getChildren(element).length > 0;
    }

    public Object[] getElements(Object inputElement) {
        return getChildren(inputElement);
    }

    public void dispose() {
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }

}
