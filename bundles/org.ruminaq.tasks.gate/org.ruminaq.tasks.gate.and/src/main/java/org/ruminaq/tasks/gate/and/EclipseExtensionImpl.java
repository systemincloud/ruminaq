package org.ruminaq.tasks.gate.and;

import org.osgi.service.component.annotations.Component;
import org.ruminaq.eclipse.api.EclipseExtension;
import org.ruminaq.tasks.gate.and.model.and.AndPackage;

@Component(property = { "service.ranking:Integer=10" })
public class EclipseExtensionImpl implements EclipseExtension {

  @Override
  public void initEditor() {
    AndPackage.eINSTANCE.getClass();
  }
}
