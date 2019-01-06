package org.ruminaq.tasks.gate.and;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.Version;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.eclipse.api.EclipseExtension;
import org.ruminaq.model.model.ruminaq.Task;
import org.ruminaq.tasks.api.ITaskApi;
import org.ruminaq.tasks.gate.and.features.AddFeature;
import org.ruminaq.tasks.gate.and.features.CreateFeature;
import org.ruminaq.tasks.gate.and.model.and.And;
import org.ruminaq.tasks.gate.and.model.and.AndPackage;
import org.ruminaq.tasks.gate.features.UpdateGateFeature;

@Component
public class TaskApi implements ITaskApi, EclipseExtension {

	private String symbolicName;
	private Version version;

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

	@Override
	public List<ICreateFeature> getCreateFeatures(IFeatureProvider fp) {
	    return Arrays.asList(new CreateFeature(fp, symbolicName, version));
	}

	@Override
	public Optional<IAddFeature> getAddFeature(IAddContext cxt, Task t, IFeatureProvider fp) {
	    return ITaskApi.ifInstance(t, And.class, new AddFeature(fp));
	}

	@Override
	public Optional<IUpdateFeature> getUpdateFeature(IUpdateContext cxt, Task t, IFeatureProvider fp) {
	    return ITaskApi.ifInstance(t, And.class, new UpdateGateFeature (fp));
	}

	@Override
	public Map<String, String> getImageKeyPath() {
	    return Images.getImageKeyPath();
	}

    @Override
    public void initEditor() {
        AndPackage.eINSTANCE.getClass();
    }
}
