package org.ruminaq.tasks.pythontask.impl.jython;

import java.io.ByteArrayInputStream;
import java.util.LinkedHashSet;

import org.apache.commons.cli.Options;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.python.pydev.core.IInterpreterInfo;
import org.python.pydev.core.IInterpreterManager;
import org.python.pydev.core.IPythonPathNature;
import org.python.pydev.plugin.nature.PythonNature;
import org.ruminaq.tasks.pythontask.Activator;

public enum JythonProgramArguments {
    INSTANCE;

    public void addToPath(StringBuilder sb,
                          LinkedHashSet<String> ret,
                          IProject p,
                          PythonNature nature,
                          IPythonPathNature pythonPathNature,
                          IInterpreterManager im,
                          IInterpreterInfo ii) {
        try {
            for (IResource r : pythonPathNature.getProjectSourcePathFolderSet())
                if (r instanceof IFolder)
                    for(IResource m : ((IFolder) r).members())
                        if(m instanceof IFolder) {
                            String ph = r.getFullPath().toOSString();
                            if(ph.matches("/.:.*")) ph = ph.substring(1);
                            sb.append(ph).append(";");
                        }
            sb.append(Activator.jythonPath + "/Lib").append(";");

            for(IResource r : pythonPathNature.getProjectSourcePathFolderSet())
                if(r instanceof IFolder)
                    addInit((IFolder) r);

            for(String s : pythonPathNature.getProjectExternalSourcePathAsList(true)) sb.append(s).append(";");

        } catch(CoreException e1) { }
    }

    private void addInit(IFolder r) {
        try {
            IFile f = ((IFolder) r).getFile("__init__.py");
            if(!f.exists())
                f.create(new ByteArrayInputStream(new byte[0]), IResource.NONE, null);
            for(IResource m : r.members())
               if(m instanceof IFolder)
                   addInit((IFolder) m);
        } catch(CoreException e1) { }
    }

    public void getProgramArguments(LinkedHashSet<String> ret,
                                    IProject p,
                                    PythonNature nature,
                                    IPythonPathNature pythonPathNature,
                                    IInterpreterManager im,
                                    IInterpreterInfo ii) {
    }

    public void addOptions(Options options) { }
}
