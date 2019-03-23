/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.tasks.constant;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.Version;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.eclipse.api.EclipseExtension;
import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.tasks.api.ITaskApi;
import org.ruminaq.tasks.constant.features.AddFeature;
import org.ruminaq.tasks.constant.features.CreateFeature;
import org.ruminaq.tasks.constant.features.ResizeShapeFeature;
import org.ruminaq.tasks.constant.features.UpdateFeature;
import org.ruminaq.tasks.constant.model.constant.Constant;
import org.ruminaq.tasks.constant.model.constant.ConstantPackage;

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
    public void initEditor() {
        ConstantPackage.eINSTANCE.getClass();
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
        return ITaskApi.ifInstance(t, Constant.class, new AddFeature(fp));
    }

    @Override
    public Optional<IResizeShapeFeature> getResizeShapeFeature(IResizeShapeContext cxt, Task t, IFeatureProvider fp) {
        return ITaskApi.ifInstance(t, Constant.class, new ResizeShapeFeature(fp));
    }

    @Override
    public Optional<IUpdateFeature> getUpdateFeature(IUpdateContext cxt, Task t, IFeatureProvider fp) {
        return ITaskApi.ifInstance(t, Constant.class, new UpdateFeature(fp));
    }

    @Override
    public Map<String, String> getImageKeyPath() {
        return Images.getImageKeyPath();
    }
}
