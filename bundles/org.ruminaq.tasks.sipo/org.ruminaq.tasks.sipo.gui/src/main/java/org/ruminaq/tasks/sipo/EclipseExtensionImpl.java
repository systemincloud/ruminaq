package org.ruminaq.tasks.sipo;

import org.osgi.service.component.annotations.Component;
import org.ruminaq.eclipse.api.EclipseExtension;
import org.ruminaq.tasks.sipo.model.sipo.SipoPackage;

@Component(property = { "service.ranking:Integer=10" })
public class EclipseExtensionImpl implements EclipseExtension {

  @Override
  public void initEditor() {
  	SipoPackage.eINSTANCE.getClass();
  }
}
