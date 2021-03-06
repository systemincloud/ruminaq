/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.pythontask.gui;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.apache.maven.model.Dependency;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.osgi.framework.Version;
import org.osgi.service.component.annotations.Activate;
import org.python.pydev.ast.interpreter_managers.InterpreterManagersAPI;
import org.python.pydev.core.IInterpreterInfo;
import org.python.pydev.core.IInterpreterManager;
import org.python.pydev.core.IPythonNature;
import org.python.pydev.core.IPythonPathNature;
import org.python.pydev.plugin.nature.PythonNature;
import org.ruminaq.eclipse.api.EclipseExtension;
import org.ruminaq.logs.ModelerLoggerFactory;
import org.ruminaq.tasks.pythontask.gui.wizards.CreateProjectWizard;
import org.ruminaq.tasks.pythontask.impl.PythonTaskI;
import org.ruminaq.tasks.pythontask.impl.cpython.CpythonProgramArguments;
import org.ruminaq.tasks.pythontask.impl.jython.JythonProgramArguments;

import ch.qos.logback.classic.Logger;

//@Component
public class TaskApi implements EclipseExtension {

  private final Logger logger = ModelerLoggerFactory.getLogger(TaskApi.class);

  private String symbolicName;
  private Version version;
  private String jythonPath;

  public TaskApi() {
  }

  @Activate
  void activate(Map<String, Object> properties) {
//        Executors.newSingleThreadExecutor().submit(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    // jython
//                    Artifact jythonJar = new DefaultArtifact("org.python", "jython-standalone", "2.7.0", null, "jar", "", new DefaultArtifactHandler("jar"));
//                    jythonJar = MavenPlugin.getMaven().getLocalRepository().find(jythonJar);
//                    logger.trace("jython should be here: {}", jythonJar.getFile().getAbsolutePath());
//                    if(!jythonJar.getFile().exists()) {
//                        logger.trace("jython jar not present in maven repo");
//                        MavenPlugin.getMaven().resolve(jythonJar.getGroupId(),
//                                                       jythonJar.getArtifactId(),
//                                                       jythonJar.getVersion(),
//                                                       jythonJar.getType(), "",
//                                                       MavenPlugin.getMaven().getArtifactRepositories(), new NullProgressMonitor());
//                    }
//
//                    String jythonLibDirPath = jythonJar.getFile().getParentFile().getAbsolutePath() + File.separator + "Lib";
//                    File jythonLibDir = new File(jythonLibDirPath);
//                    if(!jythonLibDir.exists()) {
//                        try {
//                            JarFile jar = new JarFile(jythonJar.getFile());
//                            Enumeration<JarEntry> enumEntries = jar.entries();
//                            while (enumEntries.hasMoreElements()) {
//                                JarEntry file = enumEntries.nextElement();
//                                if(!file.getName().startsWith("Lib")) continue;
//                                File f = new File(jythonJar.getFile().getParentFile().getAbsolutePath() + File.separator + file.getName());
//                                if(file.isDirectory()) {
//                                    f.mkdir();
//                                    continue;
//                                }
//                                InputStream is = jar.getInputStream(file);
//                                FileOutputStream fos = new java.io.FileOutputStream(f);
//                                while (is.available() > 0)
//                                    fos.write(is.read());
//                                fos.close();
//                                is.close();
//                            }
//                            jar.close();
//                        } catch (IOException e) { e.printStackTrace(); }
//                    }
//
//                    jythonPath = jythonJar.getFile().getAbsolutePath();
//
//                    boolean jythonJarIncluded = false;
//                    for(IInterpreterInfo ii : InterpreterManagersAPI.getJythonInterpreterManager(true).getInterpreterInfos())
//                        if(ii.getExecutableOrJar().equals(jythonJar.getFile().getPath())) {
//                            jythonJarIncluded = true;
//                            if(!ii.getPythonPath().contains(jythonLibDirPath))
//                                ii.getPythonPath().add(jythonLibDirPath);
//                        }
//
//                    logger.trace("jython jar included in interprepers : {}", jythonJarIncluded);
//
//                    if(!jythonJarIncluded) {
//                        IInterpreterInfo ii = InterpreterManagersAPI.getJythonInterpreterManager(true).createInterpreterInfo(jythonJar.getFile().getPath(), new NullProgressMonitor(), false);
//                        ii.setName("jython-2.7.0");
//                        InterpreterManagersAPI.getJythonInterpreterManager(true)
//                                   .setInfos(new IInterpreterInfo[] {ii},
//                                             new HashSet<String>(Arrays.asList(new String[]{"jython-2.7.0"})),
//                                             new NullProgressMonitor());
//                        ii.getPythonPath().add(jythonLibDirPath);
//                   }
//
//                    // pythontask.process
//                    Artifact processJar = new DefaultArtifact(PythonTaskI.PROCESS_LIB_GROUPID,
//                                                              PythonTaskI.PROCESS_LIB_ARTIFACT,
//                                                              PythonTaskI.PROCESS_LIB_VERSION, null, "jar", "", new DefaultArtifactHandler("jar"));
//                    processJar = MavenPlugin.getMaven().getLocalRepository().find(processJar);
//                    if(!processJar.getFile().exists())
//                        MavenPlugin.getMaven().resolve(processJar.getGroupId(), processJar.getArtifactId(), processJar.getVersion(), processJar.getType(), "",
//                                                       MavenPlugin.getMaven().getArtifactRepositories(), new NullProgressMonitor());
//
//                } catch(CoreException e) { }
//            }
//        });
  }

  public TaskApi(String symbolicName, Version version) {
    this.symbolicName = symbolicName;
    this.version = version;
  }

  @Override
  public boolean createProjectWizardPerformFinish(IProject newProject) {
    try {
      return new CreateProjectWizard().performFinish(newProject);
    } catch (CoreException e) {
      return false;
    }
  }

  @Override
  public List<IClasspathEntry> getClasspathEntries(IJavaProject javaProject) {
    return new CreateProjectWizard().createClasspathEntries(javaProject);
  }

  @Override
  public List<Dependency> getMavenDependencies() {
    var pythonApi = new Dependency();
    pythonApi.setGroupId(PythonTaskI.PROCESS_LIB_GROUPID);
    pythonApi.setArtifactId(PythonTaskI.PROCESS_LIB_ARTIFACT);
    pythonApi.setVersion(PythonTaskI.PROCESS_LIB_VERSION);
    return Arrays.asList(pythonApi);
  }

  public LinkedHashSet<String> getProgramArguments(IProject p) {
    LinkedHashSet<String> ret = new LinkedHashSet<>();
    PythonNature nature = PythonNature.getPythonNature(p);
    if (nature == null)
      return ret;
    IPythonPathNature pythonPathNature = nature.getPythonPathNature();
    IInterpreterManager im = InterpreterManagersAPI
        .getInterpreterManager(nature);
    IInterpreterInfo ii = null;
    String iname = "Default";
    try {
      iname = nature.getProjectInterpreterName();
    } catch (CoreException e2) {
    }
    if ("Default".equals(iname) && im.getInterpreterInfos().length > 0)
      ii = im.getInterpreterInfos()[0];
    else
      for (IInterpreterInfo i : im.getInterpreterInfos())
        if (i.getName().equals(iname))
          ii = i;
    if (ii == null)
      return ret;

    int type = ii.getInterpreterType();

    ret.add("-" + PythonTaskI.ATTR_PY_TYPE);
    ret.add(Integer.toString(type));

    String bin = ii.getExecutableOrJar();
    ret.add("-" + PythonTaskI.ATTR_PY_BIN);
    ret.add(bin);

    StringBuilder sb;
    String[] envars = ii.getEnvVariables();
    if (envars != null) {
      sb = new StringBuilder();
      ret.add("-" + PythonTaskI.ATTR_PY_ENV);
      for (String e : envars)
        sb.append(e).append(";");
      ret.add(sb.toString());
    }

    List<String> path = ii.getPythonPath();
    ret.add("-" + PythonTaskI.ATTR_PY_PATH);
    sb = new StringBuilder();
    if (!path.isEmpty())
      for (String pt : path)
        sb.append(pt).append(";");

    if (type == IPythonNature.INTERPRETER_TYPE_PYTHON)
      CpythonProgramArguments.INSTANCE.addToPath(sb, ret, p, nature,
          pythonPathNature, im, ii);
    else
      JythonProgramArguments.INSTANCE.addToPath(sb, ret, p, nature,
          pythonPathNature, im, ii);

    ret.add(sb.toString());

    try {
      List<String> projectExternalSourcePath = pythonPathNature
          .getProjectExternalSourcePathAsList(true);
      if (!projectExternalSourcePath.isEmpty()) {
        sb = new StringBuilder();
        ret.add("-" + PythonTaskI.ATTR_PY_EXT_LIBS);
        for (String el : projectExternalSourcePath)
          sb.append(el).append(";");
        ret.add(sb.toString());
      }
    } catch (CoreException e) {
    }

    if (type != IPythonNature.INTERPRETER_TYPE_PYTHON)
      JythonProgramArguments.INSTANCE.getProgramArguments(ret, p, nature,
          pythonPathNature, im, ii);

    return ret;
  }
}
