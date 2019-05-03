package org.ruminaq.tasks.javatask;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.IDoubleClickContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.javatuples.Triplet;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.Version;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.eclipse.api.EclipseExtension;
import org.ruminaq.gui.features.tools.IContextButtonPadTool;
import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.tasks.api.ITaskApi;
import org.ruminaq.tasks.javatask.features.AddFeature;
import org.ruminaq.tasks.javatask.features.ContextButtonPadTool;
import org.ruminaq.tasks.javatask.features.CreateFeature;
import org.ruminaq.tasks.javatask.features.DoubleClickFeatureFilter;
import org.ruminaq.tasks.javatask.features.UpdateFeature;
import org.ruminaq.tasks.javatask.model.javatask.JavaTask;
import org.ruminaq.tasks.javatask.model.javatask.JavataskPackage;
import org.ruminaq.tasks.javatask.wizards.CreateProjectWizard;

@Component
public class TaskApi implements ITaskApi, EclipseExtension {

    public static final String MAIN_JAVA = "src/main/java";
    public static final String TEST_JAVA = "src/test/java";

    private String  symbolicName;
    private Version version;

    public TaskApi() {

    }

    @Activate
    void activate(Map<String, Object> properties) {
        Bundle b = FrameworkUtil.getBundle(getClass());
        symbolicName = b.getSymbolicName();
        version = b.getVersion();
    }

    @Override
    public String getSymbolicName() {
        return symbolicName;
    }

    @Override
    public Version getVersion() {
        return version;
    }

    public TaskApi(String symbolicName, Version version) {
        this.symbolicName = symbolicName;
        this.version = version;
    }

    @Override
    public List<ICreateFeature> getCreateFeatures(IFeatureProvider fp) {
        return Arrays.asList(new CreateFeature(fp, symbolicName, version));
    }

    @Override
    public Optional<List<IContextButtonPadTool>> getContextButtonPadTools(IFeatureProvider fp, Task t) {
        return ITaskApi.ifInstance(t, JavaTask.class, Arrays.asList(new ContextButtonPadTool(fp)));
    }

    @Override
    public Optional<IAddFeature> getAddFeature(IAddContext cxt, Task t, IFeatureProvider fp) {
        return ITaskApi.ifInstance(t, JavaTask.class, new AddFeature(fp));
    }

    @Override
    public Optional<ICustomFeature> getDoubleClickFeature(IDoubleClickContext cxt, Task t, IFeatureProvider fp) {
        return ITaskApi.ifInstance(t, JavaTask.class, new DoubleClickFeatureFilter().filter(t, fp));
    }

    @Override
    public Optional<IUpdateFeature> getUpdateFeature(IUpdateContext cxt, Task t, IFeatureProvider fp) {
        return ITaskApi.ifInstance(t, JavaTask.class, new UpdateFeature(fp));
    }

    @Override
    public Map<String, String> getImageKeyPath() {
        return Images.getImageKeyPath();
    }

    @Override
    public boolean createProjectWizardPerformFinish(IJavaProject javaProject) {
        try {
            return new CreateProjectWizard().performFinish(javaProject);
        } catch (CoreException e) {
            return false;
        }
    }

    @Override
    public List<Triplet<String, String, String>> getMavenDependencies() {
        return Arrays.asList(
                new Triplet<String, String, String>(
                        "org.ruminaq.tasks.javatask",
                        "org.ruminaq.tasks.javatask.api",
                        "0.7.0"));
    }

    @Override
    public List<IClasspathEntry> createClasspathEntries(IJavaProject javaProject) {
        IPath[] javaPath = new IPath[] { new Path("**/*.java") };
        IPath testOutputLocation = javaProject.getPath().append("target/test-classes");

        return Arrays.asList(
                JavaCore.newSourceEntry(
                        javaProject.getPath().append(MAIN_JAVA),
                        javaPath,
                        null,
                        null),
                JavaCore.newSourceEntry(
                        javaProject.getPath().append(TEST_JAVA),
                        javaPath,
                        null,
                        testOutputLocation));
    }

    @Override
    public void initEditor() {
        JavataskPackage.eINSTANCE.getClass();
    }
}
