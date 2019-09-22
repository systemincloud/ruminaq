package org.ruminaq.tasks.javatask.gui;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.eclipse.api.EclipseExtension;
import org.ruminaq.tasks.javatask.gui.wizards.CreateProjectWizard;
import org.ruminaq.tasks.javatask.model.javatask.JavataskPackage;

@Component
public class EclipseExtensionImpl implements EclipseExtension {

  public static final String MAIN_JAVA = "src/main/java";
  public static final String TEST_JAVA = "src/test/java";

  public static final String GROUP_ID = "org.ruminaq.tasks.javatask";
  public static final String ARTIFACT_ID = "org.ruminaq.tasks.javatask.client";
  public static final String VERSION = "1.0.0-SNAPSHOT";

  @Override
  public boolean createProjectWizardPerformFinish(IJavaProject javaProject) {
    try {
      return new CreateProjectWizard().performFinish(javaProject);
    } catch (CoreException e) {
      return false;
    }
  }

  @Override
  public List<Dependency> getMavenDependencies() {
    var javaClient = new Dependency();
    javaClient.setGroupId(GROUP_ID);
    javaClient.setArtifactId(ARTIFACT_ID);
    javaClient.setVersion(VERSION);
    return Collections.singletonList(javaClient);
  }

  @Override
  public List<IClasspathEntry> getClasspathEntries(IJavaProject javaProject) {
    IPath[] javaPath = new IPath[] { new Path("**/*.java") };
    IPath testOutputLocation = javaProject.getPath()
        .append("target/test-classes");

    return Arrays.asList(
        JavaCore.newSourceEntry(javaProject.getPath().append(MAIN_JAVA),
            javaPath, null, null),
        JavaCore.newSourceEntry(javaProject.getPath().append(TEST_JAVA),
            javaPath, null, testOutputLocation));
  }

  @Override
  public void initEditor() {
    JavataskPackage.eINSTANCE.getClass();
  }
}
