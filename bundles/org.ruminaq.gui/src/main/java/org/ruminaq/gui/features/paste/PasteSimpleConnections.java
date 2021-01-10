/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.paste;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IPasteContext;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.ruminaq.gui.model.diagram.RuminaqShape;
import org.ruminaq.gui.model.diagram.SimpleConnectionPointShape;
import org.ruminaq.gui.model.diagram.SimpleConnectionShape;
import org.ruminaq.model.ruminaq.FlowSource;
import org.ruminaq.model.ruminaq.FlowTarget;
import org.ruminaq.model.ruminaq.SimpleConnection;

/**
 * SimpleConnections are copied when both ends are being copied.
 *
 * @author Marek Jagielski
 */
public class PasteSimpleConnections
    extends PictogramElementPasteFeature<SimpleConnectionShape> {

  private Map<Anchor, Anchor> oldAnchorNewAnchor;

  public PasteSimpleConnections(Map<Anchor, Anchor> anchors,
      IFeatureProvider fp) {
    super(fp, null);
    this.oldAnchorNewAnchor = anchors;
  }

  private static Stream<SimpleConnectionShape> shapes(IPasteContext context) {
    return Stream.of(context.getPictogramElements())
        .filter(SimpleConnectionShape.class::isInstance)
        .map(SimpleConnectionShape.class::cast);
  }

  private Optional<SimpleConnectionShape> notEndedByPoint(
      Collection<SimpleConnectionShape> connectionShapes) {
    return connectionShapes.stream()
        .filter(cs -> Optional.of(cs).map(SimpleConnectionShape::getTarget)
            .filter(SimpleConnectionPointShape.class::isInstance).isEmpty())
        .findFirst();
  }

  @Override
  public void paste(IPasteContext context) {
    LinkedList<SimpleConnectionShape> connectionShapes = shapes(context)
        .collect(Collectors.toCollection(LinkedList::new));
    int deltaX = context.getX();
    int deltaY = context.getY();

    Map<SimpleConnectionPointShape, SimpleConnectionPointShape> oldConnectionPoitNewConnectionPoint = new HashMap<>();

    while (!connectionShapes.isEmpty()) {
      notEndedByPoint(connectionShapes).ifPresentOrElse(scs -> {
        connectionShapes.remove(scs);
        SimpleConnectionShape newSimpleConnectionShape = EcoreUtil.copy(scs);
        newSimpleConnectionShape.getModelObject().clear();
        SimpleConnection newSimpleConnection = Optional.of(scs)
            .map(SimpleConnectionShape::getModelObject).map(List::stream)
            .orElseGet(Stream::empty).filter(SimpleConnection.class::isInstance)
            .map(SimpleConnection.class::cast).findFirst().map(EcoreUtil::copy)
            .orElseThrow();
        newSimpleConnectionShape.getModelObject().add(newSimpleConnection);
        newSimpleConnection.setSourceRef(null);
        newSimpleConnection.setTargetRef(null);
        getRuminaqDiagram().getMainTask().getConnection()
            .add(newSimpleConnection);
        Anchor newEndAnchor = oldAnchorNewAnchor.get(scs.getEnd());
        newSimpleConnectionShape.setEnd(newEndAnchor);
        Optional.of(newEndAnchor).map(Anchor::getParent)
            .filter(RuminaqShape.class::isInstance)
            .map(RuminaqShape.class::cast).map(RuminaqShape::getModelObject)
            .filter(FlowTarget.class::isInstance).map(FlowTarget.class::cast)
            .ifPresent(newSimpleConnection::setTargetRef);
        Optional.of(scs.getSource())
            .filter(SimpleConnectionPointShape.class::isInstance)
            .map(SimpleConnectionPointShape.class::cast)
            .ifPresentOrElse(scp -> {
              if (oldConnectionPoitNewConnectionPoint.containsKey(scp)) {
                newSimpleConnectionShape
                    .setSource(oldConnectionPoitNewConnectionPoint.get(scp));
              } else {
                SimpleConnectionPointShape newSimpleConnectionPointShape = EcoreUtil
                    .copy(scp);
                newSimpleConnectionPointShape.setX(scp.getX() + deltaX);
                newSimpleConnectionPointShape.setY(scp.getY() + deltaY);
                newSimpleConnectionShape
                    .setSource(newSimpleConnectionPointShape);
                getDiagram().getChildren().add(newSimpleConnectionPointShape);
                oldConnectionPoitNewConnectionPoint.put(scp,
                    newSimpleConnectionPointShape);
              }
            }, () -> {
              Anchor newStartAnchor = oldAnchorNewAnchor.get(scs.getStart());
              newSimpleConnectionShape.setStart(newStartAnchor);
              Optional.of(newStartAnchor).map(Anchor::getParent)
                  .filter(RuminaqShape.class::isInstance)
                  .map(RuminaqShape.class::cast)
                  .map(RuminaqShape::getModelObject)
                  .filter(FlowSource.class::isInstance)
                  .map(FlowSource.class::cast)
                  .ifPresent(newSimpleConnection::setSourceRef);
            });
        newSimpleConnectionShape.getBendpoints().stream().forEach((Point p) -> {
          p.setX(p.getX() + deltaX);
          p.setY(p.getY() + deltaY);
        });
        getDiagram().getConnections().add(newSimpleConnectionShape);
      }, () -> connectionShapes.clear());
    }
  }
}
