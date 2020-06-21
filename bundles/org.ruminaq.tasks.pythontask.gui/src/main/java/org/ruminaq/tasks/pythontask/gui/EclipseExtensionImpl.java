package org.ruminaq.tasks.pythontask.gui;

import org.osgi.service.component.annotations.Component;
import org.ruminaq.eclipse.api.EclipseExtension;
import org.ruminaq.tasks.pythontask.model.pythontask.PythontaskPackage;

@Component(property = { "service.ranking:Integer=10" })
public class EclipseExtensionImpl implements EclipseExtension {

  public static final String MAIN_PYTHON = "src/main/py";
  public static final String TEST_PYTHON = "src/test/py";
  
  @Override
  public void initEditor() {
    PythontaskPackage.eINSTANCE.getClass();
  }
}
