package org.ruminaq.tasks.gate.or;

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
import org.osgi.framework.Version;
import org.ruminaq.eclipse.api.EclipseExtension;
import org.ruminaq.model.model.ruminaq.Task;
import org.ruminaq.tasks.api.ITaskApi;
import org.ruminaq.tasks.gate.features.UpdateGateFeature;
import org.ruminaq.tasks.gate.or.features.AddFeature;
import org.ruminaq.tasks.gate.or.features.CreateFeature;
import org.ruminaq.tasks.gate.or.model.or.Or;
import org.ruminaq.tasks.gate.or.model.or.OrPackage;

public class TaskApi implements ITaskApi, EclipseExtension {

	private String symbolicName;
	private Version version;

	@Override
	public void initEditor() {
	    OrPackage.eINSTANCE.getClass();
	}

	@Override
	public String  getSymbolicName() {
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
	public Optional<IAddFeature> getAddFeature(IAddContext cxt, Task t, IFeatureProvider fp) {
	    return ITaskApi.ifInstance(t, Or.class, new AddFeature(fp));
	}

	@Override
	public Optional<IUpdateFeature> getUpdateFeature(IUpdateContext cxt, Task t, IFeatureProvider fp) {
	    return ITaskApi.ifInstance(t, Or.class, new UpdateGateFeature(fp));
	}

	@Override
	public Map<String, String> getImageKeyPath() {
	    return Images.getImageKeyPath();
	}
}
