package org.ruminaq.tasks.randomgenerator.gui.properties;

import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.swt.widgets.Composite;
import org.ruminaq.tasks.randomgenerator.gui.PropertySpecificComposite;
import org.ruminaq.tasks.randomgenerator.gui.ValueSaveListener;

public class TextProperty {

  public static PropertySpecificComposite createSpecificComposite(
      ValueSaveListener listener, Composite specificRoot, PictogramElement pe,
      TransactionalEditingDomain ed) {
    return new PropertySpecificTextComposite(listener, specificRoot, pe, ed);
  }
}