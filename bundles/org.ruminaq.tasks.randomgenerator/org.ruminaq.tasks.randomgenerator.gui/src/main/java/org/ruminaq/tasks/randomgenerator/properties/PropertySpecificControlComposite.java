package org.ruminaq.tasks.randomgenerator.properties;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wb.swt.SWTResourceManager;
import org.ruminaq.tasks.randomgenerator.PropertySpecificComposite;
import org.ruminaq.tasks.randomgenerator.ValueSaveListener;

public class PropertySpecificControlComposite extends PropertySpecificComposite {

	public PropertySpecificControlComposite(
			ValueSaveListener saveListener, 
			Composite specificRoot, 
			PictogramElement pe,
			TransactionalEditingDomain ed) {
		super(saveListener, specificRoot, pe, ed);
		composite = new Composite(this.specificRoot, SWT.NONE);
		composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
	}

	@Override
	public void initValues(EMap<String, String> eMap) {
	}

	@Override
	public void refresh(EMap<String, String> eMap) {
	}

	@Override 
	public boolean hasDimensions() { 
		return false;
	}
}
