/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.custom;

import static org.ruminaq.gui.model.GuiUtil.distanceBetweenPoints;
import static org.ruminaq.gui.model.GuiUtil.pointBelongsToSection;
import static org.ruminaq.gui.model.diagram.impl.simpleconnection.SimpleConnectionUtil.distanceToConnection;
import static org.ruminaq.gui.model.diagram.impl.simpleconnection.SimpleConnectionUtil.projectOnConnection;
import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.ruminaq.gui.model.diagram.DiagramFactory;
import org.ruminaq.gui.model.diagram.RuminaqDiagram;
import org.ruminaq.gui.model.diagram.SimpleConnectionPointShape;
import org.ruminaq.gui.model.diagram.SimpleConnectionShape;
import org.ruminaq.gui.model.diagram.impl.simpleconnection.SimpleConnectionUtil;

/**
 * SimpleConnectionPoint create feature.
 *
 * @author Marek Jagielski
 */
public class CreateSimpleConnectionPointFeature extends AbstractCustomFeature {

  public static final String NAME = "Create connection point";

  private static final int NEAR_BENDPOINT_DISTANCE = 10;

  public CreateSimpleConnectionPointFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public boolean canExecute(ICustomContext context) {
    return true;
  }

  @Override
  public void execute(ICustomContext context) {
    PictogramElement pe = context.getPictogramElements()[0];
    Optional<SimpleConnectionShape> optScs = Optional.of(pe)
        .filter(SimpleConnectionShape.class::isInstance)
        .map(SimpleConnectionShape.class::cast)
        .or(() -> Optional.of(pe).filter(RuminaqDiagram.class::isInstance)
            .map(RuminaqDiagram.class::cast).map(RuminaqDiagram::getConnections)
            .stream().flatMap(EList::stream)
            .filter(SimpleConnectionShape.class::isInstance)
            .map(SimpleConnectionShape.class::cast)
            .min((scs1, scs2) -> Double.compare(
                distanceToConnection(scs1, context.getX(), context.getY()),
                distanceToConnection(scs2, context.getX(), context.getY()))));
    if (optScs.isPresent()) {
      SimpleConnectionShape scs = optScs.get();
      Optional<Point> optP = projectOnConnection(scs, context.getX(),
          context.getY());
      if (optP.isPresent()) {
        Point p = optP.get();
        SimpleConnectionPointShape s = DiagramFactory.eINSTANCE
            .createSimpleConnectionPointShape();
        s.setContainer(getDiagram());
        s.setCenteredX(p.getX());
        s.setCenteredY(p.getY());

        deleteBendpointsNear(scs, p, NEAR_BENDPOINT_DISTANCE);
        List<Point> followingBendpoints = followingBendpoints(scs, p);
        scs.getBendpoints().removeAll(followingBendpoints);

        SimpleConnectionShape connectionShapeAfterPoint = DiagramFactory.eINSTANCE
            .createSimpleConnectionShape();
        connectionShapeAfterPoint.setParent(getDiagram());
        connectionShapeAfterPoint.setSource(s);
        connectionShapeAfterPoint.setTarget(scs.getTarget());
        connectionShapeAfterPoint.getBendpoints().addAll(followingBendpoints);
        connectionShapeAfterPoint.getModelObject().addAll(scs.getModelObject());
        scs.setTarget(s);
      }
    }
  }

  /**
   * Delete bendpoint which is closed than d from point p.
   *
   * @param ffc connection with bendpoints
   * @param p reference point
   * @param d distance to refenrece point
   */
  private static void deleteBendpointsNear(FreeFormConnection ffc, Point p,
      int d) {
    ffc.getBendpoints()
        .removeAll(ffc.getBendpoints().stream()
            .filter(pi -> distanceBetweenPoints(pi, p) < d)
            .collect(Collectors.toList()));
  }

  private static List<Point> followingBendpoints(FreeFormConnection ffc,
      Point p) {
    LinkedList<Point> points = SimpleConnectionUtil
        .getBendpointsWithEndings(ffc);
    LinkedList<Point> notFollowingPoints = IntStream.range(0, points.size() - 1)
        .mapToObj(i -> new SimpleEntry<Point, Point>(points.get(i),
            points.get(i + 1)))
        .takeWhile(me -> !pointBelongsToSection(me.getKey(), me.getValue(), p))
        .map(SimpleEntry::getValue)
        .collect(Collectors.toCollection(LinkedList::new));
    points.removeFirst();
    points.removeLast();
    points.removeAll(notFollowingPoints);
    return points;
  }
}
