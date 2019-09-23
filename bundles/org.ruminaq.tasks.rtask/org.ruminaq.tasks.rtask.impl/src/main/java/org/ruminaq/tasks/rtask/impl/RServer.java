package org.ruminaq.tasks.rtask.impl;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.ruminaq.runner.RunnerLoggerFactory;
import org.slf4j.Logger;

import de.walware.ecommons.ECommons;
import de.walware.ecommons.IDisposable;
import de.walware.ecommons.net.RMIRegistry;
import de.walware.ecommons.net.RMIUtil;
import de.walware.rj.data.RJIOExternalizable;
import de.walware.rj.server.srvImpl.AbstractServerControl;
import de.walware.rj.server.srvext.ERJContext;
import de.walware.rj.servi.RServi;
import de.walware.rj.servi.RServiUtil;
import de.walware.rj.servi.internal.NodeController;
import de.walware.rj.servi.pool.EmbeddedRServiManager;
import de.walware.rj.servi.pool.RServiImplE;
import de.walware.rj.servi.pool.RServiNodeConfig;
import de.walware.rj.servi.pool.RServiNodeFactory;

@SuppressWarnings("restriction")
public enum RServer {
  INSTANCE;

  private final Logger logger = RunnerLoggerFactory.getLogger(RServer.class);

  private EmbeddedRServiManager newEmbeddedR = null;

  private Set<RServi> servis = new HashSet<>();

  public synchronized void init(String rHome, String workdir, String rPolicy,
      String rLibsUsr, String rjPath) {
    if (newEmbeddedR == null) {
      try {
        final RServiNodeConfig rConfig = new RServiNodeConfig();
        rConfig.setRHome(rHome);

        rConfig.setJavaArgs(rConfig.getJavaArgs() + " "
            + "-Djava.security.policy=file://" + rPolicy);
        rConfig.setJavaArgs(
            rConfig.getJavaArgs() + " " + "-Djava.security.manager");
        rConfig.setJavaArgs(rConfig.getJavaArgs() + " "
            + "-Dde.walware.rj.rpkg.path=" + rjPath);
        rConfig
            .setJavaArgs(rConfig.getJavaArgs() + " " + "-Dde.walware.rj.debug");

        rConfig.addToClasspath(NodeController.class.getProtectionDomain()
            .getCodeSource().getLocation().toURI().getPath());
        rConfig.addToClasspath(AbstractServerControl.class.getProtectionDomain()
            .getCodeSource().getLocation().toURI().getPath());
        rConfig.addToClasspath(RJIOExternalizable.class.getProtectionDomain()
            .getCodeSource().getLocation().toURI().getPath());

        rConfig.setNodeArgs(rConfig.getNodeArgs() + " -embedded");

        rConfig.setBaseWorkingDirectory(workdir);
        rConfig.setEnableVerbose(true);

        ERJContext context = new ERJContext();
        ECommons.init("dummy_id", new ECommons.IAppEnvironment() {
          @Override
          public void removeStoppingListener(IDisposable arg0) {
          } // System.out.println("removeStoppingListener"); }

          @Override
          public void log(IStatus arg0) {
          } // System.out.println("log"); }

          @Override
          public void addStoppingListener(IDisposable arg0) {
          } // System.out.println("addStoppingListener"); }
        });
        RMIUtil.INSTANCE.setEmbeddedPrivateMode(false);
        RMIRegistry registry = RMIUtil.INSTANCE
            .getEmbeddedPrivateRegistry(new NullProgressMonitor());
        RServiNodeFactory nodeFactory = RServiImplE
            .createLocalNodeFactory("pool", context);
        nodeFactory.setRegistry(registry);
        nodeFactory.setConfig(rConfig);

        this.newEmbeddedR = RServiImplE.createEmbeddedRServi("pool", registry,
            nodeFactory);
      } catch (Exception e) {
        logger.error(e.getMessage());
        e.printStackTrace();
      }
    }
  }

  public RServi getRServi(String name) {
    try {
      RServi rs = RServiUtil.getRServi(newEmbeddedR, name);
      servis.add(rs);
      return rs;
    } catch (NoSuchElementException | CoreException e) {
      e.printStackTrace();
    }
    return null;
  }

  public synchronized void stop(RServi fRservi) {
    servis.remove(fRservi);
    if (servis.isEmpty())
      newEmbeddedR.stop();
  }
}
