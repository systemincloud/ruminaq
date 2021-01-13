/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.paste;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.emf.common.util.EList;
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

  private static Optional<SimpleConnectionShape> notEndedByPoint(
      Collection<SimpleConnectionShape> connectionShapes) {
    return connectionShapes.stream()
        .filter(cs -> Optional.of(cs).map(SimpleConnectionShape::getTarget)
            .filter(SimpleConnectionPointShape.class::isInstance).isEmpty())
        .findFirst();
  }

  private static void addModelObjectsBeforConnectionPoint(
      SimpleConnectionPointShape simpleConnectionPointShape,
      Collection<SimpleConnection> simpleConnections) {
    simpleConnectionPointShape.getIncomingConnections()
        .forEach((SimpleConnectionShape scs) -> {
          scs.getModelObject().addAll(simpleConnections);
          Optional.of(scs).map(SimpleConnectionShape::getSource)
              .filter(SimpleConnectionPointShape.class::isInstance)
              .map(SimpleConnectionPointShape.class::cast)
              .ifPresent(scps -> addModelObjectsBeforConnectionPoint(scps,
                  simpleConnections));
        });
  }

  private void copyConnectionBeforePoint(
      SimpleConnectionPointShape oldConnectionPoint,
      SimpleConnectionPointShape newConnectionPoint,
      Map<SimpleConnectionPointShape, SimpleConnectionPointShape> oldPoitNewPoint,
      int deltaX, int deltaY) {
    oldConnectionPoint.getIncomingConnections().stream()
        .forEach((SimpleConnectionShape scs) -> {
          SimpleConnectionShape newSimpleConnectionShape = copy(scs, deltaX,
              deltaY);
          newSimpleConnectionShape.getModelObject().clear();
          newSimpleConnectionShape.setTarget(newConnectionPoint);
          Optional.of(scs.getSource())
              .filter(SimpleConnectionPointShape.class::isInstance)
              .map(SimpleConnectionPointShape.class::cast)
              .ifPresentOrElse((SimpleConnectionPointShape scp) -> {
                if (oldPoitNewPoint.containsKey(scp)) {
                  newSimpleConnectionShape.setSource(oldPoitNewPoint.get(scp));
                } else {
                  SimpleConnectionPointShape newSimpleConnectionPointShape = copy(
                      scp, deltaX, deltaY);
                  newSimpleConnectionShape
                      .setSource(newSimpleConnectionPointShape);
                  oldPoitNewPoint.put(scp, newSimpleConnectionPointShape);
                  copyConnectionBeforePoint(scp, newSimpleConnectionPointShape,
                      oldPoitNewPoint, deltaX, deltaY);
                }
              }, () -> {
                Anchor newStartAnchor = oldAnchorNewAnchor.get(scs.getStart());
                newSimpleConnectionShape.setStart(newStartAnchor);
              });
        });
  }

  private static Optional<Anchor> findStartAnchor(
      SimpleConnectionPointShape simpleConnectionPointShape) {
    return simpleConnectionPointShape.getAnchors().stream().findFirst()
        .map(Anchor::getIncomingConnections).map(EList::stream)
        .orElseGet(Stream::empty)
        .filter(SimpleConnectionShape.class::isInstance)
        .map(SimpleConnectionShape.class::cast).findFirst()
        .map(SimpleConnectionShape::getStart)
        .filter(a -> Optional.of(a).map(Anchor::getParent)
            .filter(SimpleConnectionPointShape.class::isInstance).isPresent())
        .map(a -> findStartAnchor((SimpleConnectionPointShape) a.getParent()))
        .orElseGet(() -> simpleConnectionPointShape.getAnchors().stream()
            .findFirst().map(Anchor::getIncomingConnections).map(EList::stream)
            .orElseGet(Stream::empty)
            .filter(SimpleConnectionShape.class::isInstance)
            .map(SimpleConnectionShape.class::cast).findFirst()
            .map(SimpleConnectionShape::getStart));
  }

  private static void moveBendpoints(
      SimpleConnectionShape newSimpleConnectionShape, int deltaX, int deltaY) {
    newSimpleConnectionShape.getBendpoints().stream().forEach((Point p) -> {
      p.setX(p.getX() + deltaX);
      p.setY(p.getY() + deltaY);
    });
  }

  private static void moveConnectionPoint(SimpleConnectionPointShape point,
      int deltaX, int deltaY) {
    point.setX(point.getX() + deltaX);
    point.setY(point.getY() + deltaY);
  }

  private static SimpleConnection copyModel(SimpleConnectionShape shape) {
    return Optional.of(shape).map(SimpleConnectionShape::getModelObject)
        .map(List::stream).orElseGet(Stream::empty)
        .filter(SimpleConnection.class::isInstance)
        .map(SimpleConnection.class::cast).findFirst().map(EcoreUtil::copy)
        .orElseThrow();
  }

  private static void setModelSource(SimpleConnection connection,
      Anchor anchor) {
    Optional.of(anchor).map(Anchor::getParent)
        .filter(RuminaqShape.class::isInstance).map(RuminaqShape.class::cast)
        .map(RuminaqShape::getModelObject).filter(FlowSource.class::isInstance)
        .map(FlowSource.class::cast).ifPresent(connection::setSourceRef);
  }

  @Override
  public void paste(IPasteContext context) {
    LinkedList<SimpleConnectionShape> connectionShapes = shapes(context)
        .collect(Collectors.toCollection(LinkedList::new));
    int deltaX = context.getX();
    int deltaY = context.getY();

    Map<SimpleConnectionPointShape, SimpleConnectionPointShape> oldPoitNewPoint = new HashMap<>();

    while (!connectionShapes.isEmpty()) {
      notEndedByPoint(connectionShapes)
          .ifPresentOrElse((SimpleConnectionShape scs) -> {
            connectionShapes.remove(scs);
            SimpleConnectionShape newSimpleConnectionShape = copy(scs, deltaX,
                deltaY);
            newSimpleConnectionShape.getModelObject().clear();
            SimpleConnection newSimpleConnection = copyModel(scs);
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
                .filter(FlowTarget.class::isInstance)
                .map(FlowTarget.class::cast)
                .ifPresent(newSimpleConnection::setTargetRef);
            Optional.of(scs.getSource())
                .filter(SimpleConnectionPointShape.class::isInstance)
                .map(SimpleConnectionPointShape.class::cast)
                .ifPresentOrElse((SimpleConnectionPointShape scp) -> {
                  if (oldPoitNewPoint.containsKey(scp)) {
                    newSimpleConnectionShape
                        .setSource(oldPoitNewPoint.get(scp));
                    findStartAnchor(oldPoitNewPoint.get(scp))
                        .ifPresent(a -> setModelSource(newSimpleConnection, a));
                  } else {
                    SimpleConnectionPointShape newSimpleConnectionPointShape = copy(
                        scp, deltaX, deltaY);
                    newSimpleConnectionShape
                        .setSource(newSimpleConnectionPointShape);
                    oldPoitNewPoint.put(scp, newSimpleConnectionPointShape);
                    copyConnectionBeforePoint(scp,
                        newSimpleConnectionPointShape, oldPoitNewPoint, deltaX,
                        deltaY);
                    findStartAnchor(newSimpleConnectionPointShape)
                        .ifPresent(a -> setModelSource(newSimpleConnection, a));
                  }
                  addModelObjectsBeforConnectionPoint(oldPoitNewPoint.get(scp),
                      Collections.singletonList(newSimpleConnection));
                }, () -> {
                  Anchor newStartAnchor = oldAnchorNewAnchor
                      .get(scs.getStart());
                  newSimpleConnectionShape.setStart(newStartAnchor);
                  setModelSource(newSimpleConnection, newStartAnchor);
                });
          }, connectionShapes::clear);
    }
  }

  private SimpleConnectionPointShape copy(SimpleConnectionPointShape point,
      int deltaX, int deltaY) {
    SimpleConnectionPointShape newPoint = EcoreUtil.copy(point);
    getDiagram().getChildren().add(newPoint);
    moveConnectionPoint(point, deltaX, deltaY);
    return newPoint;
  }

  private SimpleConnectionShape copy(SimpleConnectionShape shape, int deltaX,
      int deltaY) {
    SimpleConnectionShape newShape = EcoreUtil.copy(shape);
    getDiagram().getConnections().add(newShape);
    moveBendpoints(newShape, deltaX, deltaY);
    return shape;
  }
}
