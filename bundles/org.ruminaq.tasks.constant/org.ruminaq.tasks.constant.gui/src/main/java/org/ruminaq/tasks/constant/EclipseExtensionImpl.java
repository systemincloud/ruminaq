package org.ruminaq.tasks.constant;

import org.osgi.service.component.annotations.Component;
import org.ruminaq.eclipse.api.EclipseExtension;
import org.ruminaq.tasks.constant.model.constant.ConstantPackage;

@Component(property = { "service.ranking:Integer=10" })
public class EclipseExtensionImpl implements EclipseExtension {

  @Override
  public void initEditor() {
    ConstantPackage.eINSTANCE.getClass();
  }
}
