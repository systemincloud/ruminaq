package org.ruminaq.tasks.randomgenerator.properties;

import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.swt.widgets.Composite;
import org.ruminaq.tasks.randomgenerator.PropertySpecificComposite;
import org.ruminaq.tasks.randomgenerator.ValueSaveListener;

public class DecimalProperty {

  public static PropertySpecificComposite createSpecificComposite(
      ValueSaveListener listener, Composite specificRoot, PictogramElement pe,
      TransactionalEditingDomain ed) {
    return new PropertySpecificDecimalComposite(listener, specificRoot, pe, ed);
  }
}
