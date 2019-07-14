package org.ruminaq.gui.features;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICopyContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.features.AbstractCopyFeature;
import org.ruminaq.consts.Constants;
import org.ruminaq.model.ruminaq.BaseElement;

public class CopyElementFeature extends AbstractCopyFeature {

	public CopyElementFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canCopy(ICopyContext context) {
        final PictogramElement[] pes = context.getPictogramElements();
        if (pes == null || pes.length == 0) return false;

        boolean onlyLabels = true;
        for(PictogramElement pe : pes) {
        	if(pe instanceof Shape && Graphiti.getPeService().getPropertyValue((Shape) pe, Constants.SIMPLE_CONNECTION_POINT) != null) continue;
            final Object bo = getBusinessObjectForPictogramElement(pe);
            if(!(bo instanceof BaseElement)) return false;
            if(pe instanceof Shape && Graphiti.getPeService().getPropertyValue((Shape) pe, Constants.LABEL_PROPERTY) == null) onlyLabels = false;
        }
        if(onlyLabels) return false;

        return true;
	}

	@Override
	public void copy(ICopyContext context) {
		Set<PictogramElement> pes = new HashSet<>();
		for(PictogramElement pe : context.getPictogramElements())
            if     (pe instanceof Shape && Graphiti.getPeService().getPropertyValue((Shape) pe, Constants.LABEL_PROPERTY)          != null) continue;
            else if(pe instanceof Shape && Graphiti.getPeService().getPropertyValue((Shape) pe, Constants.SIMPLE_CONNECTION_POINT) != null) continue;
            else pes.add(pe);

        putToClipboard(pes.toArray());
	}
}
