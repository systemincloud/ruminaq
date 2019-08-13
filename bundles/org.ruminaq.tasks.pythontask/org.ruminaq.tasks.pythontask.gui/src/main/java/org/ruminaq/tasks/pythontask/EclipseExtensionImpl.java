package org.ruminaq.tasks.pythontask;

import org.osgi.service.component.annotations.Component;
import org.ruminaq.eclipse.api.EclipseExtension;
import org.ruminaq.tasks.pythontask.model.pythontask.PythontaskPackage;

@Component(property = { "service.ranking:Integer=10" })
public class EclipseExtensionImpl implements EclipseExtension {

  @Override
  public void initEditor() {
    PythontaskPackage.eINSTANCE.getClass();
  }
}
