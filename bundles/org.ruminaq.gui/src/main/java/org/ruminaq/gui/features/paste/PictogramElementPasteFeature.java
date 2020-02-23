package org.ruminaq.gui.features.paste;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.features.AbstractPasteFeature;
import org.ruminaq.gui.model.diagram.RuminaqDiagram;

public abstract class PictogramElementPasteFeature extends AbstractPasteFeature {

  protected List<PictogramElement> newPes = new LinkedList<>();

  public PictogramElementPasteFeature(IFeatureProvider fp) {
    super(fp);
  }

  public List<PictogramElement> getNewPictogramElements() {
    return newPes;
  }
  
  protected RuminaqDiagram getRuminaqDiagram() {
    return (RuminaqDiagram) getDiagram();
  }
}
