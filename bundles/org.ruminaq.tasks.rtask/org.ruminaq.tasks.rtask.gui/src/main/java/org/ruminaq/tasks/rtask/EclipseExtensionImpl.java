package org.ruminaq.tasks.rtask;

import org.osgi.service.component.annotations.Component;
import org.ruminaq.eclipse.api.EclipseExtension;
import org.ruminaq.tasks.rtask.model.rtask.RtaskPackage;

@Component(property = { "service.ranking:Integer=10" })
public class EclipseExtensionImpl implements EclipseExtension {

  public static final String MAIN_R = "src/main/r";
  public static final String TEST_R = "src/test/r";

  @Override
  public void initEditor() {
    RtaskPackage.eINSTANCE.getClass();
  }
}
