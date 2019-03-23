package org.ruminaq.tasks.pythontask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.concurrent.Executors;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.m2e.core.MavenPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.python.pydev.ast.interpreter_managers.InterpreterManagersAPI;
import org.python.pydev.core.IInterpreterInfo;
import org.ruminaq.logs.ModelerLoggerFactory;
import org.ruminaq.tasks.AbstractTaskActivator;
import org.ruminaq.tasks.api.ITaskApi;
import org.ruminaq.tasks.api.ITaskUiApi;
import org.ruminaq.tasks.pythontask.extension.PythonTaskExtensionManager;
import org.ruminaq.tasks.pythontask.impl.PythonTaskI;
import org.slf4j.Logger;

public class Activator extends AbstractTaskActivator {

    private final Logger logger = ModelerLoggerFactory.getLogger(Activator.class);

    @Override
    protected ITaskApi getTaskApi(Bundle bundle) {
        return new TaskApi(bundle.getSymbolicName(), bundle.getVersion());
    }

    @Override
    protected ITaskUiApi getTaskUiApi(Bundle bundle) {
        return new TaskUiApi(bundle.getSymbolicName(), bundle.getVersion());
    }

    public static String jythonPath;

    @Override
    public void start(BundleContext context) throws Exception {
    	PythonTaskExtensionManager.INSTANCE.init(context);
        super.start(context);

        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    // jython
                    Artifact jythonJar = new DefaultArtifact("org.python", "jython-standalone", "2.7.0", null, "jar", "", new DefaultArtifactHandler("jar"));
                    jythonJar = MavenPlugin.getMaven().getLocalRepository().find(jythonJar);
                    logger.trace("jython should be here: {}", jythonJar.getFile().getAbsolutePath());
                    if(!jythonJar.getFile().exists()) {
                        logger.trace("jython jar not present in maven repo");
                        MavenPlugin.getMaven().resolve(jythonJar.getGroupId(),
                                                       jythonJar.getArtifactId(),
                                                       jythonJar.getVersion(),
                                                       jythonJar.getType(), "",
                                                       MavenPlugin.getMaven().getArtifactRepositories(), new NullProgressMonitor());
                    }

                    String jythonLibDirPath = jythonJar.getFile().getParentFile().getAbsolutePath() + File.separator + "Lib";
                    File jythonLibDir = new File(jythonLibDirPath);
                    if(!jythonLibDir.exists()) {
                        try {
                            JarFile jar = new JarFile(jythonJar.getFile());
                            Enumeration<JarEntry> enumEntries = jar.entries();
                            while (enumEntries.hasMoreElements()) {
                                JarEntry file = enumEntries.nextElement();
                                if(!file.getName().startsWith("Lib")) continue;
                                File f = new File(jythonJar.getFile().getParentFile().getAbsolutePath() + File.separator + file.getName());
                                if(file.isDirectory()) {
                                    f.mkdir();
                                    continue;
                                }
                                InputStream is = jar.getInputStream(file);
                                FileOutputStream fos = new java.io.FileOutputStream(f);
                                while (is.available() > 0)
                                    fos.write(is.read());
                                fos.close();
                                is.close();
                            }
                            jar.close();
                        } catch (IOException e) { e.printStackTrace(); }
                    }

                    Activator.jythonPath = jythonJar.getFile().getAbsolutePath();

                    boolean jythonJarIncluded = false;
                    for(IInterpreterInfo ii : InterpreterManagersAPI.getJythonInterpreterManager(true).getInterpreterInfos())
                        if(ii.getExecutableOrJar().equals(jythonJar.getFile().getPath())) {
                            jythonJarIncluded = true;
                            if(!ii.getPythonPath().contains(jythonLibDirPath))
                                ii.getPythonPath().add(jythonLibDirPath);
                        }

                    logger.trace("jython jar included in interprepers : {}", jythonJarIncluded);

                    if(!jythonJarIncluded) {
                        IInterpreterInfo ii = InterpreterManagersAPI.getJythonInterpreterManager(true).createInterpreterInfo(jythonJar.getFile().getPath(), new NullProgressMonitor(), false);
                        ii.setName("jython-2.7.0");
                        InterpreterManagersAPI.getJythonInterpreterManager(true)
                                   .setInfos(new IInterpreterInfo[] {ii},
                                             new HashSet<String>(Arrays.asList(new String[]{"jython-2.7.0"})),
                                             new NullProgressMonitor());
                        ii.getPythonPath().add(jythonLibDirPath);
                   }

                    // pythontask.process
                    Artifact processJar = new DefaultArtifact(PythonTaskI.PROCESS_LIB_GROUPID,
                                                              PythonTaskI.PROCESS_LIB_ARTIFACT,
                                                              PythonTaskI.PROCESS_LIB_VERSION, null, "jar", "", new DefaultArtifactHandler("jar"));
                    processJar = MavenPlugin.getMaven().getLocalRepository().find(processJar);
                    if(!processJar.getFile().exists())
                        MavenPlugin.getMaven().resolve(processJar.getGroupId(), processJar.getArtifactId(), processJar.getVersion(), processJar.getType(), "",
                                                       MavenPlugin.getMaven().getArtifactRepositories(), new NullProgressMonitor());

                } catch(CoreException e) { }
            }
        });
    }
}
