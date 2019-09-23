package org.ruminaq.tasks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;
import org.eclipse.debug.core.ILaunch;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.ruminaq.debug.api.dispatcher.EventDispatchJob;
import org.ruminaq.tasks.api.TasksExtension;
import org.ruminaq.tasks.api.TasksExtensionHandler;

@Component(immediate = true)
public class TasksExtensionHandlerImpl implements TasksExtensionHandler {

  private Collection<TasksExtension> extensions;

  @Reference(cardinality = ReferenceCardinality.MULTIPLE,
      policy = ReferencePolicy.DYNAMIC)
  protected void bind(TasksExtension extension) {
    if (extensions == null) {
      extensions = new ArrayList<>();
    }
    extensions.add(extension);
  }

  protected void unbind(TasksExtension extension) {
    extensions.remove(extension);
  }

  @Override
  public void init(BundleContext ctx) {
    extensions.forEach(t -> t.init(ctx));
  }

  @Override
  public Collection<String> getListJson() {
    return extensions.stream().map(TasksExtension::getListJson)
        .collect(Collectors.toList());
  }

  @Override
  public Object getDebugTargets(ILaunch launch, IProject project,
      EventDispatchJob dispatcher) {
    // TODO Auto-generated method stub
    return null;
  }
}
