package org.ruminaq.tasks.gate.xor;

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
import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.tasks.api.ITaskApi;
import org.ruminaq.tasks.gate.features.UpdateGateFeature;
import org.ruminaq.tasks.gate.xor.features.AddFeature;
import org.ruminaq.tasks.gate.xor.features.CreateFeature;
import org.ruminaq.tasks.gate.xor.model.xor.Xor;
import org.ruminaq.tasks.gate.xor.model.xor.XorPackage;

public class TaskApi implements ITaskApi, EclipseExtension {

	@Override
	public void initEditor() {
	    XorPackage.eINSTANCE.getClass();
	}

	@Override
	public List<ICreateFeature> getCreateFeatures(IFeatureProvider fp) {
	    return Arrays.asList(new CreateFeature(fp, symbolicName, version));
	}

	@Override
	public Optional<IAddFeature> getAddFeature(IAddContext cxt, Task t, IFeatureProvider fp) {
	    return ITaskApi.ifInstance(t, Xor.class, new AddFeature(fp));
	}

	@Override
	public Optional<IUpdateFeature> getUpdateFeature(IUpdateContext cxt, Task t, IFeatureProvider fp) {
	    return ITaskApi.ifInstance(t, Xor.class, new UpdateGateFeature(fp));
	}

	@Override
	public Map<String, String> getImageKeyPath() {
	    return Images.getImageKeyPath();
	}
}
