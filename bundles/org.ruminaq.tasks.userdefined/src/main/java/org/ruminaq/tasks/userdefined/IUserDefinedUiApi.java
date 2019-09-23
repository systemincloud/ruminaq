package org.ruminaq.tasks.userdefined;

import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.swt.widgets.Composite;
import org.ruminaq.tasks.api.IPropertySection;

public interface IUserDefinedUiApi {
  IPropertySection createParametersSection(Composite parent,
      PictogramElement pe, TransactionalEditingDomain ed,
      IDiagramTypeProvider dtp);
}
