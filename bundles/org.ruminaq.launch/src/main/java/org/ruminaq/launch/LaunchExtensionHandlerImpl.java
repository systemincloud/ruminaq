package org.ruminaq.launch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;

import org.eclipse.core.resources.IProject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.ruminaq.launch.api.LaunchExtension;
import org.ruminaq.launch.api.LaunchExtensionHandler;

@Component(immediate = true)
public class LaunchExtensionHandlerImpl implements LaunchExtensionHandler {

  private Collection<LaunchExtension> extensions;

  @Reference(cardinality = ReferenceCardinality.MULTIPLE,
      policy = ReferencePolicy.DYNAMIC)
  protected void bind(LaunchExtension extension) {
    if (extensions == null) {
      extensions = new ArrayList<>();
    }
    extensions.add(extension);
  }

  protected void unbind(LaunchExtension extension) {
    extensions.remove(extension);
  }

  @Override
  public Collection<String> getPluginIdsToRunnerClasspath() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getVMArguments() {
    StringBuilder args = new StringBuilder();
    args.append(" ");
    extensions.stream().map(LaunchExtension::getVMArguments)
        .forEach(a -> args.append(a).append(" "));
    return args.toString();
  }

  @Override
  public LinkedHashSet<String> getProgramArguments(IProject p) {
    // TODO Auto-generated method stub
    return null;
  }

}
