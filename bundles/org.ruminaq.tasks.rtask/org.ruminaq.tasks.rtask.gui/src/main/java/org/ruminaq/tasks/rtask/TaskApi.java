package org.ruminaq.tasks.rtask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.LinkedHashSet;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.osgi.framework.Version;
import org.ruminaq.eclipse.api.EclipseExtension;
import org.ruminaq.tasks.api.ITaskApi;
import org.ruminaq.tasks.rtask.model.rtask.RtaskPackage;
import org.ruminaq.tasks.rtask.wizards.CreateProjectWizard;

public class TaskApi implements ITaskApi, EclipseExtension {

  private String symbolicName;
  private Version version;

  @Override
  public void initEditor() {
    RtaskPackage.eINSTANCE.getClass();
  }

  public TaskApi(String symbolicName, Version version) {
    this.symbolicName = symbolicName;
    this.version = version;
  }

  @Override
  public boolean createProjectWizardPerformFinish(IJavaProject newProject) {
    try {
      return new CreateProjectWizard().performFinish(newProject);
    } catch (CoreException e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public List<IClasspathEntry> getClasspathEntries(IJavaProject javaProject) {
    return new CreateProjectWizard().createClasspathEntries(javaProject);
  }

  @Override
  public LinkedHashSet<String> getProgramArguments(IProject p) {
    LinkedHashSet<String> ret = new LinkedHashSet<>();
//        IREnvManager rm = RCore.getREnvManager();
//        REnv dr = rm.getDefault();
//        IREnvConfiguration conf = dr.getConfig();
//
//        if(conf == null) return ret;
//
//        String rHome = conf.getRHome();
//        List<? extends IRLibraryLocation> usrLibs = conf.getRLibraryGroup("r.user").getLibraries();
//
//        if(!"".equals(rHome)) {
//          ret.add("-" + RTaskI.ATTR_R_HOME);
//          ret.add(rHome);
//
//          String rPolicyPath = p.getLocation().toString() + "/target/test-classes/r.policy";
//
//          if(!new File(rPolicyPath).exists()) {
//              try { IOUtils.copy(TaskApi.class.getResourceAsStream("r.policy"), new FileOutputStream(rPolicyPath));
//              } catch (IOException e) { }
//          }
//
//          ret.add("-" + RTaskI.ATTR_R_POLICY);
//          ret.add(rPolicyPath);
//
//          String rLibsUsr = usrLibs.size() > 0 ? usrLibs.get(0).getDirectoryPath() : p.getLocation().toString() + "/target/R";
//          rLibsUsr = rLibsUsr.replace("${user_home}/", getUserHome());
//          if(!new File(rLibsUsr).exists()) new File(rLibsUsr).mkdir();
//
//          installPackage(rHome, rLibsUsr, "rj", "http://download.walware.de/rj-2.1/src/contrib/rj_2.1.0-13.tar.gz");
//          installPackage(rHome, rLibsUsr, "rJava", "https://cran.r-project.org/src/contrib/rJava_0.9-10.tar.gz");
//          installPackage(rHome, rLibsUsr, "R6", "https://cran.r-project.org/src/contrib/R6_2.3.0.tar.gz");
//          installPackage(rHome, rLibsUsr, "ruminaq", "https://cran.r-project.org/src/contrib/ruminaq_1.0.0.tar.gz");
//          installPackage(rHome, rLibsUsr, "thriftr", "https://cran.r-project.org/src/contrib/thriftr_1.0.3.tar.gz");
//
//          ret.add("-" + RTaskI.ATTR_R_LIBS_USR);
//          ret.add(rLibsUsr);
//
//          String rjPath = rLibsUsr + "/rj";
//
//          ret.add("-" + RTaskI.ATTR_R_RJPATH);
//          ret.add(rjPath);
//        }

    return ret;
  }

  private String getUserHome() {
    IPath path = new Path(System.getProperty("user.home")); //$NON-NLS-1$
    path = path.addTrailingSeparator();
    return path.toOSString();
  }

  public void installPackage(String rHome, String rLibsUsr, String pkgName,
      String url) {
    File pkg = new File(rLibsUsr + "/" + pkgName);
    if (!pkg.exists()) {
      try {
        String tar = rLibsUsr + "/" + pkgName + ".tar.gz";
        if (!new File(tar).exists()) {
          URL website = new URL(url);
          ReadableByteChannel rbc = Channels.newChannel(website.openStream());
          FileOutputStream fos = new FileOutputStream(tar);
          fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
          fos.close();
        }
        Process ps = new ProcessBuilder(rHome + "/bin/R", "CMD", "INSTALL",
            "-l", rLibsUsr, tar).start();
        ps.waitFor();
      } catch (IOException | InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

}
