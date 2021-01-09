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
import java.util.Map.Entry;
import java.util.Optional;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IPasteContext;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.services.Graphiti;
import org.ruminaq.gui.model.diagram.SimpleConnectionPointShape;
import org.ruminaq.gui.model.diagram.SimpleConnectionShape;
import org.ruminaq.model.ruminaq.FlowSource;
import org.ruminaq.model.ruminaq.FlowTarget;
import org.ruminaq.model.ruminaq.SimpleConnection;

public class PasteSimpleConnections
    extends PictogramElementPasteFeature<SimpleConnectionShape> {

  private Map<FlowSource, Anchor> oldFlowSources;
  private Map<FlowTarget, Anchor> oldFlowTargets;
  private Map<Connection, List<SimpleConnection>> oldDiagramElementBusinessObjects;
  private Map<Anchor, Anchor> oldAnchorNewAnchor;

  private Map<SimpleConnection, SimpleConnection> oldSCnewSC = new HashMap<>();
  private Map<Connection, Connection> newColdC = new HashMap<>();

  public PasteSimpleConnections(Map<FlowSource, Anchor> flowSources,
      Map<FlowTarget, Anchor> flowTargets,
      Map<Connection, List<SimpleConnection>> peBos,
      Map<Anchor, Anchor> anchors, IFeatureProvider fp) {
    super(fp, null);
//    this.oldFlowSources = flowSources;
//    this.oldFlowTargets = flowTargets;
//    this.oldDiagramElementBusinessObjects = peBos;
//    this.oldAnchorNewAnchor = anchors;
  }

  @Override
  public void paste(IPasteContext context) {
    
//    for (List<SimpleConnection> lsc : oldDiagramElementBusinessObjects.values())
//      for (SimpleConnection sc : lsc)
//        if (!oldSCnewSC.containsKey(sc)) {
//          SimpleConnection newSc = EcoreUtil.copy(sc);
//          Object o1 = getFeatureProvider()
//              .getBusinessObjectForPictogramElement(oldAnchorNewAnchor
//                  .get(oldFlowSources.get(newSc.getSourceRef())).getParent());
//          if (o1 instanceof FlowSource)
//            newSc.setSourceRef((FlowSource) o1);
//          Object o2 = getFeatureProvider()
//              .getBusinessObjectForPictogramElement(oldAnchorNewAnchor
//                  .get(oldFlowTargets.get(newSc.getTargetRef())).getParent());
//          if (o2 instanceof FlowTarget)
//            newSc.setTargetRef((FlowTarget) o2);
//          getRuminaqDiagram().getMainTask().getConnection().add(newSc);
//          oldSCnewSC.put(sc, newSc);
//        }
//
//    for (Entry<FlowSource, Anchor> fs : oldFlowSources.entrySet()) {
//      Anchor oldAnchor = fs.getValue();
//      int oldX = Graphiti.getPeLayoutService()
//          .getLocationRelativeToDiagram(oldAnchor).getX();
//      int oldY = Graphiti.getPeLayoutService()
//          .getLocationRelativeToDiagram(oldAnchor).getY();
//      Anchor newAnchor = oldAnchorNewAnchor.get(oldAnchor);
//      int newX = Graphiti.getPeLayoutService()
//          .getLocationRelativeToDiagram(newAnchor).getX();
//      int newY = Graphiti.getPeLayoutService()
//          .getLocationRelativeToDiagram(newAnchor).getY();
//      int deltaX = newX - oldX;
//      int deltaY = newY - oldY;
//      for (Connection c : oldAnchor.getOutgoingConnections()) {
//        if (oldDiagramElementBusinessObjects.containsKey(c)) {
//          Connection newC = EcoreUtil.copy(c);
//          newPes.add(newC);
//          newC.setStart(newAnchor);
//          for (Point p : ((FreeFormConnection) newC).getBendpoints()) {
//            p.setX(p.getX() + deltaX);
//            p.setY(p.getY() + deltaY);
//          }
//          newC.setEnd(getEndAnchor(c, deltaX, deltaY));
//          newColdC.put(newC, c);
//          getFeatureProvider().getDiagramTypeProvider().getDiagram()
//              .getConnections().add(newC);
//        }
//      }
//    }
//
//    for (Entry<Connection, Connection> c : newColdC.entrySet()) {
//      List<SimpleConnection> newSc = new LinkedList<>();
//      for (SimpleConnection old : oldDiagramElementBusinessObjects
//          .get(c.getValue()))
//        newSc.add(oldSCnewSC.get(old));
//
//      getFeatureProvider().link(c.getKey(),
//          newSc.toArray(new Object[newSc.size()]));
//    }
  }

  private Anchor getEndAnchor(Connection c, int deltaX, int deltaY) {
    Anchor ret = null;
    if (oldAnchorNewAnchor.containsKey(c.getEnd()))
      ret = oldAnchorNewAnchor.get(c.getEnd());
    else {
      Optional<SimpleConnectionPointShape> endOpt = Optional.of(c)
          .map(Connection::getEnd).map(Anchor::getParent)
          .filter(SimpleConnectionPointShape.class::isInstance)
          .map(SimpleConnectionPointShape.class::cast);
      if (endOpt.isPresent()) {
        SimpleConnectionPointShape end = endOpt.get();
        SimpleConnectionPointShape newSCP = EcoreUtil.copy(end);
        newPes.add(newSCP);

        newSCP.setX(newSCP.getX() + deltaX);
        newSCP.setY(newSCP.getY() + deltaY);
        getDiagram().getChildren().add(newSCP);
        ret = newSCP.getAnchors().get(0);
        for (Connection c2 : end.getAnchors().get(0).getOutgoingConnections()) {
          if (oldDiagramElementBusinessObjects.containsKey(c2)) {
            Connection newC = EcoreUtil.copy(c2);
            newPes.add(newC);
            newC.setStart(ret);
            for (Point p : ((FreeFormConnection) newC).getBendpoints()) {
              p.setX(p.getX() + deltaX);
              p.setY(p.getY() + deltaY);
            }
            newC.setEnd(getEndAnchor(c2, deltaX, deltaY));
            newColdC.put(newC, c);
            getDiagram().getConnections().add(newC);
          }
        }
      }
    }
    return ret;
  }

  @Override
  public boolean canPaste(IPasteContext context) {
    return true;
  }
}
