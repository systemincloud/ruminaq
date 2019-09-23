package org.ruminaq.tasks.gate.not;

import java.util.Optional;

import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.tasks.api.ITaskApi;
import org.ruminaq.tasks.gate.features.UpdateGateFeature;
import org.ruminaq.tasks.gate.not.features.AddFeature;
import org.ruminaq.tasks.gate.not.model.not.Not;

@Component
public class TaskApi implements ITaskApi {

  @Override
  public Optional<IAddFeature> getAddFeature(IAddContext cxt, Task t,
      IFeatureProvider fp) {
    return ITaskApi.ifInstance(t, Not.class, new AddFeature(fp));
  }

  @Override
  public Optional<IUpdateFeature> getUpdateFeature(IUpdateContext cxt, Task t,
      IFeatureProvider fp) {
    return ITaskApi.ifInstance(t, Not.class, new UpdateGateFeature(fp));
  }

}
