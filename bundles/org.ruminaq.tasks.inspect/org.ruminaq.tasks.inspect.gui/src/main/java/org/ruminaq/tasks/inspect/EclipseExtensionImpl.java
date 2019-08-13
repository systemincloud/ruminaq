package org.ruminaq.tasks.inspect;

import org.osgi.service.component.annotations.Component;
import org.ruminaq.eclipse.api.EclipseExtension;
import org.ruminaq.tasks.inspect.model.inspect.InspectPackage;

@Component(property = { "service.ranking:Integer=10" })
public class EclipseExtensionImpl implements EclipseExtension {

  @Override
  public void initEditor() {
  	InspectPackage.eINSTANCE.getClass();
  }
}
