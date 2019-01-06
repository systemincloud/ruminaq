package org.ruminaq.tasks.randomgenerator;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.swt.widgets.Composite;

public abstract class PropertySpecificComposite {
	protected ValueSaveListener saveListener;
	protected Composite         specificRoot;
	protected PictogramElement           pe;
	protected TransactionalEditingDomain ed;
	
	protected Composite composite;
	public    Composite getComposite() { return composite; }
	
	public PropertySpecificComposite(ValueSaveListener saveListener, Composite specificRoot, PictogramElement pe, TransactionalEditingDomain ed) {
		this.saveListener = saveListener;
		this.specificRoot = specificRoot;
		this.pe           = pe;
		this.ed           = ed;
	}

	public abstract void initValues(EMap<String, String> eMap);
	public abstract void refresh(EMap<String, String> eMap);

	public boolean hasDimensions() { return true; }

}
