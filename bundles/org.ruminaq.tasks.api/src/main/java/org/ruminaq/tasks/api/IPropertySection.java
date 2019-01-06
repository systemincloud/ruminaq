package org.ruminaq.tasks.api;

import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public interface IPropertySection {
	void refresh(PictogramElement pe, TransactionalEditingDomain ed);
}
