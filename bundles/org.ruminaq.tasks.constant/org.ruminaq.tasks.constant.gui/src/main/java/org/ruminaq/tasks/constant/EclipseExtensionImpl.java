package org.ruminaq.tasks.constant;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;
import org.ruminaq.eclipse.api.EclipseExtension;
import org.ruminaq.tasks.constant.model.constant.ConstantPackage;

@Component(service = EclipseExtension.class, scope = ServiceScope.SINGLETON)
public class EclipseExtensionImpl implements EclipseExtension {

  @Override
  public void initEditor() {
    ConstantPackage.eINSTANCE.getClass();
  }
}
