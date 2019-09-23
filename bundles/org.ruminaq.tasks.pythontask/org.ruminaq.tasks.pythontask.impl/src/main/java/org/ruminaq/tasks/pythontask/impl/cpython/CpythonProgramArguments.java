package org.ruminaq.tasks.pythontask.impl.cpython;

import java.util.LinkedHashSet;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.python.pydev.core.IInterpreterInfo;
import org.python.pydev.core.IInterpreterManager;
import org.python.pydev.core.IPythonPathNature;
import org.python.pydev.plugin.nature.PythonNature;

public enum CpythonProgramArguments {
  INSTANCE;

  public void addToPath(StringBuilder sb, LinkedHashSet<String> ret, IProject p,
      PythonNature nature, IPythonPathNature pythonPathNature,
      IInterpreterManager im, IInterpreterInfo ii) {
    String tp = Thrift.INSTANCE.getThrifpy(p, ii.getExecutableOrJar());
    if (tp != null)
      sb.append(tp).append(";");

    try {
      for (IResource r : pythonPathNature.getProjectSourcePathFolderSet()) {
        String ph = r.getLocationURI().getRawPath();
        if (ph.matches("/.:.*"))
          ph = ph.substring(1);
        sb.append(ph).append(";");
      }
      for (String s : pythonPathNature.getProjectExternalSourcePathAsList(true))
        sb.append(s).append(";");
    } catch (CoreException e1) {
    }
  }
}
