package org.ruminaq.gui.features.contextpad;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.features.context.impl.CreateConnectionContext;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.tb.ContextButtonEntry;
import org.eclipse.graphiti.tb.IContextButtonEntry;
import org.ruminaq.gui.Images;
import org.ruminaq.gui.features.tools.AbstractContextButtonPadTool;
import org.ruminaq.gui.features.tools.IContextButtonPadTool;

public class ContextButtonPadFlowSourceTool extends AbstractContextButtonPadTool implements IContextButtonPadTool {

	public ContextButtonPadFlowSourceTool(IFeatureProvider fp) {
		super(fp);
	}
	
	@Override
	public Collection<IContextButtonEntry> getContextButtonPad(IPictogramElementContext context) {
		List<IContextButtonEntry> buttons = new ArrayList<>();
		
		PictogramElement pe = context.getPictogramElement();
		
		CreateConnectionContext ccc = new CreateConnectionContext();
		ccc.setSourcePictogramElement(pe);
		Anchor anchor = null;
		if (pe instanceof Anchor) anchor = (Anchor) pe;
		else if (pe instanceof AnchorContainer) anchor = Graphiti.getPeService().getChopboxAnchor((AnchorContainer) pe);
		ccc.setSourceAnchor(anchor);
		
		ICreateConnectionFeature[] features = getFeatureProvider().getCreateConnectionFeatures();
		ContextButtonEntry button = new ContextButtonEntry(null, context);
		button.setText("Create connection"); //$NON-NLS-1$
		ArrayList<String> names = new ArrayList<>();
		button.setIconId(Images.Image.IMG_CONTEXT_SIMPLECONNECTION.name());
		for (ICreateConnectionFeature feature : features) {
			if (feature.isAvailable(ccc) && feature.canStartConnection(ccc)) {
				button.addDragAndDropFeature(feature);
				names.add(feature.getCreateName());
			}
		}
	    
		buttons.add(button);
		
		return buttons;	
	}

}
