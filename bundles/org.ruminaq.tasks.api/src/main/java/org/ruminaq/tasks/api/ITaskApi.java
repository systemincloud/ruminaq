package org.ruminaq.tasks.api;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.core.resources.IProject;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IDoubleClickContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.tb.IContextMenuEntry;
import org.eclipse.graphiti.tb.IDecorator;
import org.osgi.framework.Version;
import org.ruminaq.debug.api.dispatcher.EventDispatchJob;
import org.ruminaq.model.ruminaq.Task;

public interface ITaskApi {

    default List<ICreateFeature> getCreateFeatures(IFeatureProvider fp) {
        return Collections.emptyList();
    }

    default List<ICustomFeature> getCustomFeatures(IFeatureProvider fp) {
        return Collections.emptyList();
    }

    default List<IDecorator> getDecorators(IFeatureProvider fp, Task task) {
        return Collections.emptyList();
    }

    default List<IContextMenuEntry> getContextMenu(ICustomContext ctx, IFeatureProvider fp) {
        return Collections.emptyList();
    }

    default List<IDebugTarget> getDebugTargets(ILaunch lnc, IProject p, EventDispatchJob d) {
        return Collections.emptyList();
    }

    default LinkedHashSet<String> getProgramArguments(IProject p) {
        return null;
    }

    default Optional<IAddFeature> getAddFeature(IAddContext cxt, Task t, IFeatureProvider fp) {
        return Optional.empty();
    }

    default Optional<ICustomFeature> getDoubleClickFeature(IDoubleClickContext cxt, Task t, IFeatureProvider fp) {
        return Optional.empty();
    }

    default Optional<IResizeShapeFeature> getResizeShapeFeature(IResizeShapeContext cxt, Task t, IFeatureProvider fp) {
        return Optional.empty();
    }

    default Optional<IUpdateFeature> getUpdateFeature(IUpdateContext cxt, Task t, IFeatureProvider fp) {
        return Optional.empty();
    }

    default Map<String, String> getImageKeyPath() {
        return Collections.emptyMap();
    }

    public static <T,U> Optional<T> ifInstance(Task t, Class<U> clazz1, T addFeature) {
        if (clazz1.isAssignableFrom(clazz1)) {
            return Optional.of(addFeature);
        } else {
            return Optional.empty();
        }
    }
}
