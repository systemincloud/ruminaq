package org.ruminaq.tasks.pythontask;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
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
import org.javatuples.Triplet;
import org.osgi.framework.Version;
import org.python.pydev.ast.interpreter_managers.InterpreterManagersAPI;
import org.python.pydev.core.IInterpreterInfo;
import org.python.pydev.core.IInterpreterManager;
import org.python.pydev.core.IPythonNature;
import org.python.pydev.core.IPythonPathNature;
import org.python.pydev.plugin.nature.PythonNature;
import org.ruminaq.eclipse.api.EclipseExtension;
import org.ruminaq.gui.features.tools.IContextButtonPadTool;
import org.ruminaq.model.sic.Task;
import org.ruminaq.tasks.api.ITaskApi;
import org.ruminaq.tasks.pythontask.features.AddFeature;
import org.ruminaq.tasks.pythontask.features.ContextButtonPadTool;
import org.ruminaq.tasks.pythontask.features.CreateFeature;
import org.ruminaq.tasks.pythontask.features.DoubleClickFeatureFilter;
import org.ruminaq.tasks.pythontask.features.UpdateFeature;
import org.ruminaq.tasks.pythontask.impl.PythonTaskI;
import org.ruminaq.tasks.pythontask.impl.cpython.CpythonProgramArguments;
import org.ruminaq.tasks.pythontask.impl.jython.JythonProgramArguments;
import org.ruminaq.tasks.pythontask.wizards.CreateProjectWizard;

public class TaskApi implements ITaskApi, EclipseExtension {

    private String  symbolicName;
    private Version version;

    @Override
    public void initEditor() {
        PythontaskPackage.eINSTANCE.getClass();
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
        this.version      = version;
    }

    @Override
    public boolean createProjectWizardPerformFinish(IJavaProject newProject) {
        try {
            return new CreateProjectWizard().performFinish(newProject);
        } catch (CoreException e) {
            return false;
        }
    }

    @Override
    public List<IClasspathEntry> createClasspathEntries(IJavaProject javaProject) {
        return new CreateProjectWizard().createClasspathEntries(javaProject);
    }

    @Override
    public List<ICreateFeature> getCreateFeatures(IFeatureProvider fp) {
        return Arrays.asList(new CreateFeature(fp, symbolicName, version));
    }

    @Override
    public Optional<List<IContextButtonPadTool>> getContextButtonPadTools(IFeatureProvider fp, Task t) {
        return ITaskApi.ifInstance(t, PythonTask.class, Arrays.asList(new ContextButtonPadTool(fp)));
    }

    @Override
    public List<Triplet<String, String, String>> getMavenDependencies() {
        return Arrays.asList(new Triplet<String, String, String>(
                PythonTaskI.PROCESS_LIB_GROUPID,
                PythonTaskI.PROCESS_LIB_ARTIFACT,
                PythonTaskI.PROCESS_LIB_VERSION));
    }

    @Override
    public LinkedHashSet<String> getProgramArguments(IProject p) {
        LinkedHashSet<String> ret = new LinkedHashSet<>();
        PythonNature        nature           = PythonNature.getPythonNature(p);
        if(nature == null) return ret;
        IPythonPathNature   pythonPathNature = nature.getPythonPathNature();
        IInterpreterManager im               = InterpreterManagersAPI.getInterpreterManager(nature);
        IInterpreterInfo    ii = null;
        String iname = "Default";
        try { iname = nature.getProjectInterpreterName();
        } catch (CoreException e2) { }
        if("Default".equals(iname) && im.getInterpreterInfos().length > 0) ii = im.getInterpreterInfos()[0];
        else
            for(IInterpreterInfo i : im.getInterpreterInfos())
                if(i.getName().equals(iname)) ii = i;
        if(ii == null) return ret;

        int type = ii.getInterpreterType();

        ret.add("-" + PythonTaskI.ATTR_PY_TYPE);
        ret.add(Integer.toString(type));

        String bin = ii.getExecutableOrJar();
        ret.add("-" + PythonTaskI.ATTR_PY_BIN);
        ret.add(bin);

        StringBuilder sb;
        String[] envars = ii.getEnvVariables();
        if(envars != null) {
            sb = new StringBuilder();
            ret.add("-" + PythonTaskI.ATTR_PY_ENV);
            for(String e : envars) sb.append(e).append(";");
            ret.add(sb.toString());
        }


        List<String> path = ii.getPythonPath();
        ret.add("-" + PythonTaskI.ATTR_PY_PATH);
        sb = new StringBuilder();
        if(!path.isEmpty())
            for(String pt : path) sb.append(pt).append(";");

        if(type == IPythonNature.INTERPRETER_TYPE_PYTHON) CpythonProgramArguments.INSTANCE.addToPath(sb, ret, p, nature, pythonPathNature, im, ii);
        else                                              JythonProgramArguments .INSTANCE.addToPath(sb, ret, p, nature, pythonPathNature, im, ii);

        ret.add(sb.toString());


        try {
            List<String> projectExternalSourcePath = pythonPathNature.getProjectExternalSourcePathAsList(true);
            if(!projectExternalSourcePath.isEmpty()) {
                sb = new StringBuilder();
                ret.add("-" + PythonTaskI.ATTR_PY_EXT_LIBS);
                for(String el : projectExternalSourcePath) sb.append(el).append(";");
                ret.add(sb.toString());
            }
        } catch (CoreException e) { }

        if(type != IPythonNature.INTERPRETER_TYPE_PYTHON) JythonProgramArguments .INSTANCE.getProgramArguments(ret, p, nature, pythonPathNature, im, ii);

        return ret;
    }

    @Override
    public Optional<IAddFeature> getAddFeature(IAddContext cxt, Task t, IFeatureProvider fp) {
        return ITaskApi.ifInstance(t, PythonTask.class, new AddFeature(fp));
    }

    @Override
    public Optional<ICustomFeature> getDoubleClickFeature(IDoubleClickContext cxt, Task t, IFeatureProvider fp) {
        return ITaskApi.ifInstance(t, PythonTask.class, new DoubleClickFeatureFilter().filter(t, fp));
    }

    @Override
    public Optional<IUpdateFeature> getUpdateFeature(IUpdateContext cxt, Task t, IFeatureProvider fp) {
        return ITaskApi.ifInstance(t, PythonTask.class, new UpdateFeature(fp));
    }

    @Override
    public Map<String, String> getImageKeyPath() {
        return Images.getImageKeyPath();
    }
}
