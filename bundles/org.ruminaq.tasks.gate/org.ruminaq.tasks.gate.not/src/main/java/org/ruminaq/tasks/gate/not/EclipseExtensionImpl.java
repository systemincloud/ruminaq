package org.ruminaq.tasks.gate.not;

import org.osgi.service.component.annotations.Component;
import org.ruminaq.eclipse.api.EclipseExtension;
import org.ruminaq.tasks.gate.not.model.not.NotPackage;

@Component(property = { "service.ranking:Integer=10" })
public class EclipseExtensionImpl implements EclipseExtension {

  @Override
  public void initEditor() {
  	NotPackage.eINSTANCE.getClass();
  }
}
