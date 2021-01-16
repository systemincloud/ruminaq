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
          SimpleConnectionShape newConnectionShape = EcoreUtil.copy(scs);
          getDiagram().getConnections().add(newConnectionShape);
          newConnectionShape.getModelObject().clear();
          newConnectionShape.setTarget(newConnectionPoint);
          Optional.of(scs.getSource())
              .filter(SimpleConnectionPointShape.class::isInstance)
              .map(SimpleConnectionPointShape.class::cast)
              .ifPresentOrElse((SimpleConnectionPointShape scp) -> Optional
                  .ofNullable(scp).map(oldPoitNewPoint::get)
                  .ifPresentOrElse(newConnectionShape::setSource, () -> {
                    SimpleConnectionPointShape newPointShape = copyPoint(scp,
                        deltaX, deltaY);
                    newConnectionShape.setSource(newPointShape);
                    getDiagram().getChildren().add(newPointShape);
                    oldPoitNewPoint.put(scp, newPointShape);
                    copyConnectionBeforePoint(scp, newPointShape,
                        oldPoitNewPoint, deltaX, deltaY);
                  }), () -> {
                    Anchor newStartAnchor = oldAnchorNewAnchor
                        .get(scs.getStart());
                    newConnectionShape.setStart(newStartAnchor);
                  });
          moveBendpoints(newConnectionShape, deltaX, deltaY);
        });
  }

  /**
   * It goes to the beginning of connection shapes through connection points.
   *
   * @param simpleConnectionPointShape initial connection point
   * @return anchor of the non connection point end of connection
   */
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

  private static SimpleConnectionPointShape copyPoint(
      SimpleConnectionPointShape scp, int deltaX, int deltaY) {
    SimpleConnectionPointShape point = EcoreUtil.copy(scp);
    moveConnectionPoint(point, deltaX, deltaY);
    return point;
  }

  private static void setModelSource(SimpleConnection connection,
      Anchor anchor) {
    Optional.of(anchor).map(Anchor::getParent)
        .filter(RuminaqShape.class::isInstance).map(RuminaqShape.class::cast)
        .map(RuminaqShape::getModelObject).filter(FlowSource.class::isInstance)
        .map(FlowSource.class::cast).ifPresent(connection::setSourceRef);
  }

  private static void setModelTarget(SimpleConnection newConnection,
      Anchor newEndAnchor) {
    Optional.of(newEndAnchor).map(Anchor::getParent)
        .filter(RuminaqShape.class::isInstance).map(RuminaqShape.class::cast)
        .map(RuminaqShape::getModelObject).filter(FlowTarget.class::isInstance)
        .map(FlowTarget.class::cast).ifPresent(newConnection::setTargetRef);
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
            SimpleConnectionShape newConnectionShape = EcoreUtil.copy(scs);
            newConnectionShape.getModelObject().clear();
            SimpleConnection newConnection = copyModel(scs);
            newConnectionShape.getModelObject().add(newConnection);
            getRuminaqDiagram().getMainTask().getConnection()
                .add(newConnection);
            Anchor newEndAnchor = oldAnchorNewAnchor.get(scs.getEnd());
            newConnectionShape.setEnd(newEndAnchor);
            setModelTarget(newConnection, newEndAnchor);
            Optional.of(scs.getSource())
                .filter(SimpleConnectionPointShape.class::isInstance)
                .map(SimpleConnectionPointShape.class::cast)
                .ifPresentOrElse((SimpleConnectionPointShape scp) -> Optional
                    .ofNullable(scp).map(oldPoitNewPoint::get)
                    .ifPresentOrElse((
                        SimpleConnectionPointShape point) -> alreadyCopiedPoint(
                            point, newConnectionShape, newConnection),
                        () -> {
                          SimpleConnectionPointShape newScp = copyPoint(scp,
                              deltaX, deltaY);
                          newConnectionShape.setSource(newScp);
                          getDiagram().getChildren().add(newScp);
                          oldPoitNewPoint.put(scp, newScp);
                          copyConnectionBeforePoint(scp, newScp,
                              oldPoitNewPoint, deltaX, deltaY);
                          findStartAnchor(newScp)
                              .ifPresent(a -> setModelSource(newConnection, a));
                          addModelObjectsBeforConnectionPoint(newScp,
                              Collections.singletonList(newConnection));
                        }),
                    () -> {
                      Anchor newStartAnchor = oldAnchorNewAnchor
                          .get(scs.getStart());
                      newConnectionShape.setStart(newStartAnchor);
                      setModelSource(newConnection, newStartAnchor);
                    });
            moveBendpoints(newConnectionShape, deltaX, deltaY);
            getDiagram().getConnections().add(newConnectionShape);
          }, connectionShapes::clear);
    }
  }

  private static void alreadyCopiedPoint(SimpleConnectionPointShape point,
      SimpleConnectionShape newConnectionShape,
      SimpleConnection newConnection) {
    newConnectionShape.setSource(point);
    findStartAnchor(point).ifPresent(a -> setModelSource(newConnection, a));
    addModelObjectsBeforConnectionPoint(point,
        Collections.singletonList(newConnection));
  }
}
