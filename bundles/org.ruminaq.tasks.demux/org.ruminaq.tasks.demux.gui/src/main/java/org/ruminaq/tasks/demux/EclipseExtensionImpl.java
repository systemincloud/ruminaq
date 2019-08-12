package org.ruminaq.tasks.demux;

import org.osgi.service.component.annotations.Component;
import org.ruminaq.eclipse.api.EclipseExtension;
import org.ruminaq.tasks.demux.model.demux.DemuxPackage;

@Component(property = { "service.ranking:Integer=10" })
public class EclipseExtensionImpl implements EclipseExtension {

  @Override
  public void initEditor() {
  	DemuxPackage.eINSTANCE.getClass();
  }
}
