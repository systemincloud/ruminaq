package org.ruminaq.tasks.gate.and;

import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.swt.widgets.Composite;
import org.ruminaq.tasks.gate.GatePropertySection;

public class PropertySection extends GatePropertySection {

  public PropertySection(Composite parent, PictogramElement pe,
      TransactionalEditingDomain ed, IDiagramTypeProvider dtp) {
    super(parent, pe, ed, dtp);
  }

}
