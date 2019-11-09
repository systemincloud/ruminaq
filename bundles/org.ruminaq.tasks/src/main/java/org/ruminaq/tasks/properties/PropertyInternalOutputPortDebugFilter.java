package org.ruminaq.tasks.properties;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.platform.AbstractPropertySectionFilter;
import org.ruminaq.gui.LabelUtil;
import org.ruminaq.model.ruminaq.InternalOutputPort;

public class PropertyInternalOutputPortDebugFilter
    extends AbstractPropertySectionFilter {

  @Override
  protected boolean accept(PictogramElement pe) {
    EObject eObject = Graphiti.getLinkService()
        .getBusinessObjectForLinkedPictogramElement(pe);
    return eObject instanceof InternalOutputPort && !LabelUtil.isLabel(pe);
  }
}
