package org.ruminaq.tasks.demux;

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
import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.tasks.api.ITaskApi;
import org.ruminaq.tasks.demux.features.AddFeature;
import org.ruminaq.tasks.demux.features.CreateFeature;
import org.ruminaq.tasks.demux.features.UpdateFeature;
import org.ruminaq.tasks.demux.model.demux.Demux;
import org.ruminaq.tasks.demux.model.demux.DemuxPackage;

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
	public List<ICreateFeature> getCreateFeatures(IFeatureProvider fp) {
	    return Arrays.asList(new CreateFeature(fp, symbolicName, version));
	}

	@Override
	public Optional<IAddFeature> getAddFeature(IAddContext cxt, Task t, IFeatureProvider fp) {
	    return ITaskApi.ifInstance(t, Demux.class, new AddFeature(fp));
	}

	@Override
	public Optional<IUpdateFeature> getUpdateFeature(IUpdateContext cxt, Task t, IFeatureProvider fp) {
	    return ITaskApi.ifInstance(t, Demux.class, new UpdateFeature(fp));
	}

	@Override
	public Map<String, String> getImageKeyPath() {
	    return Images.getImageKeyPath();
	}

    @Override
    public void initEditor() {
        DemuxPackage.eINSTANCE.getClass();
    }
}
