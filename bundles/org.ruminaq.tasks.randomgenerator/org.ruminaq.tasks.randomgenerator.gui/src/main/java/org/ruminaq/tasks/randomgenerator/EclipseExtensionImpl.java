package org.ruminaq.tasks.randomgenerator;

import org.osgi.service.component.annotations.Component;
import org.ruminaq.eclipse.api.EclipseExtension;
import org.ruminaq.tasks.randomgenerator.model.randomgenerator.RandomgeneratorPackage;

@Component(property = { "service.ranking:Integer=10" })
public class EclipseExtensionImpl implements EclipseExtension {

  @Override
  public void initEditor() {
    RandomgeneratorPackage.eINSTANCE.getClass();
  }
}
