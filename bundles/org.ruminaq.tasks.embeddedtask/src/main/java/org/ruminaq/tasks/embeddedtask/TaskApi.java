package org.ruminaq.tasks.embeddedtask;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.IDoubleClickContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.Version;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.features.tools.IContextButtonPadTool;
import org.ruminaq.model.ruminaq.EmbeddedTask;
import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.tasks.api.ITaskApi;
import org.ruminaq.tasks.embeddedtask.features.AddFeature;
import org.ruminaq.tasks.embeddedtask.features.ContextButtonPadTool;
import org.ruminaq.tasks.embeddedtask.features.CreateFeature;
import org.ruminaq.tasks.embeddedtask.features.DoubleClickFeatureFilter;
import org.ruminaq.tasks.embeddedtask.features.UpdateFeature;

@Component
public class TaskApi implements ITaskApi {

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
	public Optional<List<IContextButtonPadTool>> getContextButtonPadTools(IFeatureProvider fp, Task t) {
	    return ITaskApi.ifInstance(t, EmbeddedTask.class, Arrays.asList(new ContextButtonPadTool(fp)));
	}

	@Override
	public Optional<IAddFeature> getAddFeature(IAddContext cxt, Task t, IFeatureProvider fp) {
	    return ITaskApi.ifInstance(t, EmbeddedTask.class, new AddFeature(fp));
	}

	@Override
	public Optional<ICustomFeature> getDoubleClickFeature(IDoubleClickContext cxt, Task t, IFeatureProvider fp) {
	    return ITaskApi.ifInstance(t, EmbeddedTask.class, new DoubleClickFeatureFilter().filter(t, fp));
	}

	@Override
	public Optional<IUpdateFeature> getUpdateFeature     (IUpdateContext      cxt, Task t, IFeatureProvider fp) {
	    return ITaskApi.ifInstance(t, EmbeddedTask.class, new UpdateFeature(fp));
	}

	@Override
	public Map<String, String> getImageKeyPath() {
	    return Images.getImageKeyPath();
	}
}
