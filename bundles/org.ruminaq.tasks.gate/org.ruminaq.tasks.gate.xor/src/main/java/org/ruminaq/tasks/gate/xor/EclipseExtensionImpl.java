package org.ruminaq.tasks.gate.xor;

import org.osgi.service.component.annotations.Component;
import org.ruminaq.eclipse.api.EclipseExtension;
import org.ruminaq.tasks.gate.xor.model.xor.XorPackage;

@Component(property = { "service.ranking:Integer=10" })
public class EclipseExtensionImpl implements EclipseExtension {

  @Override
  public void initEditor() {
  	XorPackage.eINSTANCE.getClass();
  }
}
