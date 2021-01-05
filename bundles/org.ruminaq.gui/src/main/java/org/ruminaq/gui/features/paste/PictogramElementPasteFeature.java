/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.paste;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IPasteContext;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.ui.features.AbstractPasteFeature;
import org.ruminaq.gui.model.diagram.RuminaqDiagram;
import org.ruminaq.gui.model.diagram.RuminaqShape;
import org.ruminaq.gui.model.diagram.SimpleConnectionPointShape;
import org.ruminaq.model.ruminaq.FlowSource;
import org.ruminaq.model.ruminaq.FlowTarget;
import org.ruminaq.model.ruminaq.SimpleConnection;

public class PictogramElementPasteFeature<T extends PictogramElement>
    extends AbstractPasteFeature {

  protected List<PictogramElement> newPes = new LinkedList<>();

  protected T oldPe;
  protected T newPe;

  protected PictogramElementPasteFeature(IFeatureProvider fp, T oldPe) {
    super(fp);
    this.oldPe = oldPe;
  }

  public List<PictogramElement> getNewPictogramElements() {
    return newPes;
  }

  protected RuminaqDiagram getRuminaqDiagram() {
    return (RuminaqDiagram) getDiagram();
  }

  @Override
  public void paste(IPasteContext context) {
    newPe = EcoreUtil.copy(oldPe);
    newPes.add(newPe);
  }

  @Override
  public boolean canPaste(IPasteContext context) {
    return false;
  }

  private void pasteSimpleConnections(
      List<RuminaqShapePasteFeature<? extends RuminaqShape>> pfs,
      IFeatureProvider fp) {
    Map<Anchor, Anchor> anchors = new HashMap<>();
    Map<FlowSource, Anchor> flowSources = new HashMap<>();
    Map<FlowTarget, Anchor> flowTargets = new HashMap<>();

    for (RuminaqShapePasteFeature<?> pf : pfs)
      if (pf instanceof PasteAnchorTracker)
        anchors.putAll(((PasteAnchorTracker) pf).getAnchors());
    for (Anchor a : anchors.keySet()) {
      for (Object o : getAllBusinessObjectsForPictogramElement(a.getParent())) {
        if (o instanceof FlowSource)
          flowSources.put((FlowSource) o, a);
        if (o instanceof FlowTarget)
          flowTargets.put((FlowTarget) o, a);
      }
    }
    if (anchors.size() == 0)
      return;
    Diagram oldDiagram = getDiagram(
        ((Shape) anchors.entrySet().iterator().next().getKey().getParent()));
    Map<Connection, List<SimpleConnection>> peBos = new HashMap<>();
    Map<Connection, AnchorContainer> simpleConnectionPointAtTheEnd = new HashMap<>();
    Map<Connection, AnchorContainer> simpleConnectionPointAtTheStart = new HashMap<>();
    for (Connection c : oldDiagram.getConnections()) {
      List<SimpleConnection> scsToCopy = new LinkedList<>();
      for (Object cn : getAllBusinessObjectsForPictogramElement(c)) {
        if (cn instanceof SimpleConnection) {
          SimpleConnection sc = (SimpleConnection) cn;
          if (flowSources.containsKey(sc.getSourceRef())
              && flowTargets.containsKey(sc.getTargetRef()))
            scsToCopy.add(sc);
        }
      }
      if (scsToCopy.size() > 0)
        peBos.put(c, scsToCopy);
      Optional<Anchor> sa = Optional.of(c).map(Connection::getStart);
      Optional<Anchor> ea = Optional.of(c).map(Connection::getEnd);
      if (ea.map(Anchor::getParent)
          .filter(SimpleConnectionPointShape.class::isInstance).isPresent()) {
        simpleConnectionPointAtTheEnd.put(c, ea.get().getParent());
      }
      if (sa.map(Anchor::getParent)
          .filter(SimpleConnectionPointShape.class::isInstance).isPresent()) {
        simpleConnectionPointAtTheStart.put(c, sa.get().getParent());
      }
    }
    PasteSimpleConnections psc = new PasteSimpleConnections(flowSources,
        flowTargets, peBos, anchors, fp);
    psc.paste(null);
  }

  private Diagram getDiagram(Shape shape) {
    if (shape instanceof Diagram)
      return (Diagram) shape;
    return getDiagram(shape.getContainer());
  }
}
