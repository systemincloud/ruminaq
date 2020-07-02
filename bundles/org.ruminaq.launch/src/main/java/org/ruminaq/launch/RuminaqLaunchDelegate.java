/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.launch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.aspectj.weaver.loadtime.Aj;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.JavaLaunchDelegate;
import org.eclipse.m2e.core.MavenPlugin;
import org.osgi.framework.Bundle;
import org.osgi.service.component.annotations.Reference;
import org.ruminaq.builder.ProjectBuilder;
import org.ruminaq.consts.Constants.SicPlugin;
import org.ruminaq.debug.DebuggerService;
import org.ruminaq.debug.api.DebugExtensionHandler;
import org.ruminaq.debug.api.dispatcher.EventDispatchJob;
import org.ruminaq.debug.model.RuminaqDebugTarget;
import org.ruminaq.eclipse.wizards.project.SourceFolders;
import org.ruminaq.launch.api.LaunchExtension;
import org.ruminaq.launch.api.LaunchExtensionHandler;
import org.ruminaq.logs.ModelerLoggerFactory;
import org.ruminaq.prefs.Prefs;
import org.ruminaq.runner.Runner;
import org.ruminaq.runner.dirmi.DirmiServer;
import org.ruminaq.runner.dirmi.RegistrationDoneListener;
import org.ruminaq.runner.impl.debug.IDebugIService;
import org.ruminaq.util.PlatformUtil;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

@SuppressWarnings("restriction")
public class RuminaqLaunchDelegate extends JavaLaunchDelegate
    implements RegistrationDoneListener {

  private final Logger logger = ModelerLoggerFactory
      .getLogger(RuminaqLaunchDelegate.class);

  public static final String ECLIPSE_CORE_RUNTIME_PLUGIN_ID = "org.eclipse.core.runtime";
  public static final String ECLIPSE_CORE_RESOURCES_PLUGIN_ID = "org.eclipse.core.resources";
  public static final String ECLIPSE_CORE_JOBS_PLUGIN_ID = "org.eclipse.core.jobs";
  public static final String ECLIPSE_EQUINOX_COMMON_PLUGIN_ID = "org.eclipse.equinox.common";
  public static final String ECLIPSE_OSGI_PLUGIN_ID = "org.eclipse.osgi";

  public static final String EMF_ECORE_PLUGIN_ID = "org.eclipse.emf.ecore";
  public static final String EMF_COMMON_PLUGIN_ID = "org.eclipse.emf.common";
  public static final String EMF_TRANSACTION_PLUGIN_ID = "org.eclipse.emf.transaction";
  public static final String EMF_XMI_PLUGIN_ID = "org.eclipse.emf.ecore.xmi";
  public static final String GRAPHITI_MM_PLUGIN_ID = "org.eclipse.graphiti.mm";

  @Reference
  private DebugExtensionHandler debugExtensions;

  @Reference
  private LaunchExtensionHandler launchExtensions;

  private static List<LaunchListener> launchListeners = new ArrayList<>();

  public static void addLaunchListener(LaunchListener ll) {
    if (!launchListeners.contains(ll))
      launchListeners.add(ll);
  }

  public static void removeLaunchListener(LaunchListener ll) {
    launchListeners.remove(ll);
  }

  private static boolean running = false;

  public static boolean isRunning() {
    return running;
  }

  private int mainPort = -1;
  private boolean debug = false;

  private EventDispatchJob dispatcher;

  private String logbackPath = null;

  @Override
  public void launch(ILaunchConfiguration configuration, String mode,
      ILaunch launch, IProgressMonitor monitor) throws CoreException {
    IProject project = ResourcesPlugin.getWorkspace().getRoot()
        .getProject(configuration.getAttribute(
            RuminaqLaunchConfigurationConstants.ATTR_PROJECT_NAME, ""));

    logger.trace("********************************************************");
    logger.trace("* launch                                               *");
    logger.trace("********************************************************");
    if (monitor == null)
      monitor = new NullProgressMonitor();

    ProjectBuilder.INSTANCE.build(project);
    project.getFolder("target").refreshLocal(IResource.DEPTH_INFINITE, null);

    ILaunchConfigurationWorkingCopy wc = configuration.getWorkingCopy();
    wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME,
        project.getName());
    wc.doSave();

    SicMavenRuntimeClasspathProvider.enable(project);

    logbackPath = project.getLocation().toString()
        + "/target/test-classes/logback.xml";
    addLogbackXml(logbackPath);

    this.mainPort = DirmiServer.INSTANCE.start(this);
    DirmiServer.INSTANCE.createSessionAcceptor("DEBUG",
        this.getClass().getClassLoader());

    this.debug = ILaunchManager.DEBUG_MODE.equals(mode) ? true : false;
    if (debug) {
      dispatcher = new EventDispatchJob();
      dispatcher.schedule();
      for (IDebugTarget dt : debugExtensions.getDebugTargets(launch, project,
          dispatcher)) {
        launch.addDebugTarget(dt);
      }

      IFile file = project
          .getFile(File.pathSeparator + SourceFolders.TEST_RESOURCES
              + File.pathSeparator + configuration.getAttribute(
                  RuminaqLaunchConfigurationConstants.ATTR_TEST_TASK, ""));
      logger.trace("path: {}", file.getFullPath().toString());
      launch.addDebugTarget(new RuminaqDebugTarget(launch, file, dispatcher));
    }

    for (LaunchListener ll : launchListeners)
      ll.dirmiStarted();

    super.launch(configuration, mode, launch, monitor);
    RuminaqLaunchDelegate.running = true;

    IProcess p = launch.getProcesses()[0];
    launch.addProcess(new JavaProcessDecorator(p));
    launch.removeProcess(p);
    for (IDebugTarget dt : launch.getDebugTargets())
      if (dt instanceof JDIDebugTarget)
        launch.removeDebugTarget(dt);

    do {
      IFolder f1 = project.getFolder("target");
      if (f1 != null) {
        f1.refreshLocal(IResource.DEPTH_ZERO, null);
        IFolder f2 = f1.getFolder("logs");
        if (f2 != null)
          f2.refreshLocal(IResource.DEPTH_ONE, null);
      }
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
      }
    } while (!launch.isTerminated());

    RuminaqLaunchDelegate.running = false;
    for (LaunchListener ll : launchListeners)
      ll.stopped();

    DirmiServer.INSTANCE.stop();

    DebugPlugin manager = DebugPlugin.getDefault();
    if (manager != null)
      for (IProcess ps : launch.getProcesses())
        manager.fireDebugEventSet(
            new DebugEvent[] { new DebugEvent(ps, DebugEvent.TERMINATE) });

    logger.trace("********************************************************");
    logger.trace("* launch finished                                      *");
    logger.trace("********************************************************");
  }

  private void addLogbackXml(String logbackPath) {
    if (!new File(logbackPath).exists()) {
      Bundle bundle = PlatformUtil.getBundle(ModelerLoggerFactory.class);
      Enumeration<?> logbackConf = bundle.findEntries("/", "logback.xml",
          false);
      if (logbackConf == null)
        logbackConf = bundle.findEntries("/target/classes/", "logback.xml",
            false);
      URL element = (URL) logbackConf.nextElement();
      try (FileInputStream fis = new FileInputStream(
          FileLocator.toFileURL(element).getFile())) {
        IOUtils.copy(fis, new FileOutputStream(logbackPath));
      } catch (IOException e) {
      }
    }
  }

  @Override
  public String getVMArguments(ILaunchConfiguration configuration)
      throws CoreException {
    String weaverPath = null;
    try {
      weaverPath = Aj.class.getProtectionDomain().getCodeSource().getLocation()
          .toURI().getPath();
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }

    return super.getVMArguments(configuration)
        + " -Dlogback.configurationFile=\"" + logbackPath + "\" "
        + " -javaagent:" + weaverPath + launchExtensions.getVMArguments();
  }

  @Override
  public void debugInitialized() {
    if (debug) {
      try {
        IDebugIService cs = DirmiServer.INSTANCE.getRemote("main",
            IDebugIService.class);
        if (cs != null) {
          DebuggerService ds = new DebuggerService(cs, dispatcher);
          dispatcher.setDebugger(ds);
          cs.setDebugService(ds);
        }
      } catch (RemoteException e) {
      }
    }
  }

  @Override
  public void registrationDone() {
    for (LaunchListener ll : launchListeners)
      ll.launched();
  }

  @Override
  public String getMainTypeName(ILaunchConfiguration configuration)
      throws CoreException {
    return Runner.class.getName();
  }

  @Override
  public String getProgramArguments(ILaunchConfiguration configuration)
      throws CoreException {
    StringBuilder cmdline = new StringBuilder();

    IProject project = ResourcesPlugin.getWorkspace().getRoot()
        .getProject(configuration.getAttribute(
            RuminaqLaunchConfigurationConstants.ATTR_PROJECT_NAME, ""));

    String logLevel = InstanceScope.INSTANCE.getNode(Prefs.QUALIFIER)
        .get(LaunchExtension.RUNNER_LOG_LEVEL_PREF, Level.ERROR.levelStr);
    boolean onlyLocal = configuration.getAttribute(
        RuminaqLaunchConfigurationConstants.ATTR_ONLY_LOCAL_TASKS, true);

    cmdline.append("-" + Runner.ATTR_DIAGRAM + " ")
        .append((project.getLocation().toOSString() + "/"
            + SourceFolders.TEST_RESOURCES + "/"
            + configuration.getAttribute(
                RuminaqLaunchConfigurationConstants.ATTR_TEST_TASK, ""))
                    .replace(" ", "+")
            + " ");
    cmdline.append("-" + Runner.ATTR_LOG_LEVEL + " ").append(logLevel + " ");
    cmdline.append("-" + Runner.ATTR_ROOT + " ")
        .append(project.getLocation().toString().replace(" ", "+") + " ");
    cmdline.append("-" + Runner.ATTR_PORT + " ").append(this.mainPort + " ");

    if (onlyLocal)
      cmdline.append("-" + Runner.ATTR_ONLY_LOCAL + " ");
    if (debug)
      cmdline.append("-" + Runner.ATTR_DEBUG + " ");

    cmdline.append("-" + Runner.ATTR_MVN_REPO + " ")
        .append(MavenPlugin.getMaven().getLocalRepository().getBasedir() + " ");

    cmdline.append("-" + Runner.ATTR_THRIFT_CLIENT + " ")
        .append(copyThriftResource(
            this.getClass().getResource("RunnerSideServer.thrift"), project)
            + " ");
    cmdline.append("-" + Runner.ATTR_THRIFT_SERVER + " ")
        .append(copyThriftResource(
            this.getClass().getResource("ProcessSideServer.thrift"), project)
            + " ");
    cmdline.append("-" + Runner.ATTR_THRIFT_DATA + " ").append(
        copyThriftResource(this.getClass().getResource("RemoteData.thrift"),
            project) + " ");

    for (String s : launchExtensions.getProgramArguments(project)) {
      cmdline.append(s).append(" ");
    }

    return cmdline.toString();
  }

  private String copyThriftResource(URL url, IProject p) {
    File dstFile = new File(
        p.getLocation().toString() + "/target/thrift" + url.getPath());
    if (!dstFile.exists()) {
      dstFile.getParentFile().mkdirs();
      try (FileInputStream fis = new FileInputStream(
          FileLocator.toFileURL(url).getFile())) {
        IOUtils.copy(fis, new FileOutputStream(dstFile));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return dstFile.getAbsolutePath() + "," + dstFile.getParent();
  }

  @Override
  public String[] getClasspath(ILaunchConfiguration configuration)
      throws CoreException {
    List<String> extendedClasspath = new ArrayList<>();
    Collections.addAll(extendedClasspath, super.getClasspath(configuration));
    IProject project = ResourcesPlugin.getWorkspace().getRoot()
        .getProject(configuration.getAttribute(
            RuminaqLaunchConfigurationConstants.ATTR_PROJECT_NAME, ""));
    IPath path = project.getLocation();
    extendedClasspath.add(path.toString() + "/target/test-classes");

//    if (Platform.inDevelopmentMode()) {
//      try {
//        for (SicPlugin p : SicPlugin.values())
//          extendedClasspath.add(pluginIdToJarPath(p.s()) + "target/classes");
//      } catch (IOException e) {
//        throw new CoreException(
//            new Status(IStatus.ERROR, SicPlugin.LAUNCH_ID.s(), IStatus.OK,
//                "Failed to compose classpath!", e));
//      }
//    }
    try {
      Bundle bundle = Activator.getDefault().getBundle(SicPlugin.LOGS_ID.s());
      if (bundle != null)
        addInsideJars(bundle, extendedClasspath);

      extendedClasspath.add(pluginIdToJarPath("ch.qos.logback.core"));
      extendedClasspath.add(pluginIdToJarPath("ch.qos.logback.classic"));
      extendedClasspath.add(pluginIdToJarPath("org.slf4j.api"));
      extendedClasspath.add(pluginIdToJarPath("de.walware.rj.server"));
      extendedClasspath.add(pluginIdToJarPath("de.walware.rj.servi"));
      extendedClasspath.add(pluginIdToJarPath("de.walware.rj.data"));
      extendedClasspath.add(pluginIdToJarPath("de.walware.rj.client"));
      extendedClasspath.add(pluginIdToJarPath("de.walware.ecommons.coremisc"));
      extendedClasspath.add(pluginIdToJarPath("org.aspectj.runtime"));
      extendedClasspath.add(pluginIdToJarPath("org.aspectj.weaver"));
      extendedClasspath
          .add(pluginIdToJarPath("org.eclipse.equinox.weaving.aspectj"));
      extendedClasspath
          .add(pluginIdToJarPath("org.eclipse.equinox.weaving.caching"));

      extendedClasspath.add(pluginIdToJarPath("org.apache.commons.cli"));
      extendedClasspath.add(pluginIdToJarPath("org.cojen.dirmi"));
      extendedClasspath.add(pluginIdToJarPath("org.cojen"));
      extendedClasspath.add(pluginIdToJarPath("com.google.guava"));
      extendedClasspath.add(pluginIdToJarPath("org.ruminaq.runner.thrift"));
      extendedClasspath.add(pluginIdToJarPath("org.apache.thrift"));
      extendedClasspath.add(pluginIdToJarPath("org.apache.commons.lang3"));
      extendedClasspath.add(pluginIdToJarPath("org.apache.commons.math3"));

      for (SicPlugin p : SicPlugin.values())
        extendedClasspath.add(pluginIdToJarPath(p.s()));

      for (String id : launchExtensions.getPluginIdsToRunnerClasspath())
        extendedClasspath.add(id);

      extendedClasspath.add(pluginIdToJarPath(ECLIPSE_CORE_RUNTIME_PLUGIN_ID));
      extendedClasspath
          .add(pluginIdToJarPath(ECLIPSE_CORE_RESOURCES_PLUGIN_ID));
      extendedClasspath.add(pluginIdToJarPath(ECLIPSE_CORE_JOBS_PLUGIN_ID));
      extendedClasspath
          .add(pluginIdToJarPath(ECLIPSE_EQUINOX_COMMON_PLUGIN_ID));
      extendedClasspath.add(pluginIdToJarPath(ECLIPSE_OSGI_PLUGIN_ID));
      extendedClasspath.add(pluginIdToJarPath(EMF_ECORE_PLUGIN_ID));
      extendedClasspath.add(pluginIdToJarPath(EMF_COMMON_PLUGIN_ID));
      extendedClasspath.add(pluginIdToJarPath(EMF_TRANSACTION_PLUGIN_ID));
      extendedClasspath.add(pluginIdToJarPath(EMF_XMI_PLUGIN_ID));
      extendedClasspath.add(pluginIdToJarPath(GRAPHITI_MM_PLUGIN_ID));

    } catch (IOException e) {
      throw new CoreException(new Status(IStatus.ERROR, SicPlugin.LAUNCH_ID.s(),
          IStatus.OK, "Failed to compose classpath!", e));
    }
    return extendedClasspath.toArray(new String[extendedClasspath.size()]);
  }

  private void addInsideJars(Bundle bundle, List<String> extendedClasspath)
      throws IOException {
    Enumeration<?> jarEnum = bundle.findEntries("/", "*.jar", true);
    while (jarEnum != null && jarEnum.hasMoreElements()) {
      URL element = (URL) jarEnum.nextElement();
      extendedClasspath.add(FileLocator.toFileURL(element).getFile());
    }
  }

  public static String pluginIdToJarPath(String pluginId) throws IOException {
    Bundle bundle = Activator.getDefault().getBundle(pluginId);
    URL url = bundle.getEntry("/");
    if (url == null)
      throw new IOException();
    return FileLocator.toFileURL(url).getFile();
  }
}
