/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.tasks.randomgenerator;

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
import org.ruminaq.tasks.randomgenerator.features.AddFeature;
import org.ruminaq.tasks.randomgenerator.features.CreateFeature;
import org.ruminaq.tasks.randomgenerator.features.UpdateFeature;
import org.ruminaq.tasks.randomgenerator.model.randomgenerator.RandomGenerator;
import org.ruminaq.tasks.randomgenerator.model.randomgenerator.RandomgeneratorPackage;

public class TaskApi implements ITaskApi, EclipseExtension {

	private String symbolicName;
	private Version version;

	@Override
	public void initEditor() {
	    RandomgeneratorPackage.eINSTANCE.getClass();
	}

	@Override
	public String  getSymbolicName() { return symbolicName; }

	@Override
	public Version getVersion()      { return version; }

	public TaskApi(String symbolicName, Version version) {
		this.symbolicName = symbolicName;
		this.version      = version;
	}

	@Override
	public List<ICreateFeature> getCreateFeatures(IFeatureProvider fp) {
	    return Arrays.asList(new CreateFeature(fp, symbolicName, version));
	}

	@Override
	public Optional<IAddFeature> getAddFeature(IAddContext cxt, Task t, IFeatureProvider fp) {
	    return ITaskApi.ifInstance(t, RandomGenerator.class, new AddFeature(fp));
	}

	@Override
	public Optional<IUpdateFeature> getUpdateFeature(IUpdateContext cxt, Task t, IFeatureProvider fp) {
	    return ITaskApi.ifInstance(t, RandomGenerator.class, new UpdateFeature(fp));
	}

	@Override
	public Map<String, String> getImageKeyPath() {
	    return Images.getImageKeyPath();
	}
}
