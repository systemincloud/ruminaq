/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.debug;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.ruminaq.debug.api.DebugExtension;
import org.ruminaq.debug.api.DebugExtensionHandler;
import org.ruminaq.debug.api.dispatcher.EventDispatchJob;

@Component(immediate = true)
public class DebugExtensionHandlerImpl implements DebugExtensionHandler {

  private Collection<DebugExtension> extensions;

  @Reference(cardinality = ReferenceCardinality.MULTIPLE,
      policy = ReferencePolicy.DYNAMIC)
  protected void bind(DebugExtension extension) {
    if (extensions == null) {
      extensions = new ArrayList<>();
    }
    extensions.add(extension);
  }

  protected void unbind(DebugExtension extension) {
    extensions.remove(extension);
  }

  @Override
  public Collection<? extends IDebugTarget> getDebugTargets(ILaunch launch,
      IProject project, EventDispatchJob dispatcher) {
    return extensions.stream()
        .map(ext -> ext.getDebugTargets(launch, project, dispatcher))
        .flatMap(Collection::stream).collect(Collectors.toList());
  }
}
