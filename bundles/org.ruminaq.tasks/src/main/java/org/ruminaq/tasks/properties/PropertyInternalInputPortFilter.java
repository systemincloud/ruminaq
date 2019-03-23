package org.ruminaq.tasks.properties;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.platform.AbstractPropertySectionFilter;
import org.ruminaq.consts.Constants;
import org.ruminaq.model.ruminaq.InternalInputPort;

public class PropertyInternalInputPortFilter extends AbstractPropertySectionFilter {

    @Override
    protected boolean accept(PictogramElement pe) {
        EObject eObject = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
        if(eObject instanceof InternalInputPort
           && Graphiti.getPeService().getPropertyValue(pe, Constants.LABEL_PROPERTY) == null) return true;
        else return false;
    }
}
