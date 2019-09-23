package org.ruminaq.tasks.gate.or;

import org.osgi.service.component.annotations.Component;
import org.ruminaq.eclipse.api.EclipseExtension;
import org.ruminaq.tasks.gate.or.model.or.OrPackage;

@Component(property = { "service.ranking:Integer=10" })
public class EclipseExtensionImpl implements EclipseExtension {

  @Override
  public void initEditor() {
    OrPackage.eINSTANCE.getClass();
  }
}
