/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.create;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.algorithms.Ellipse;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.ICreateService;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.services.IPeService;
import org.eclipse.graphiti.util.IColorConstant;
import org.ruminaq.consts.Constants;
import org.ruminaq.gui.features.add.AddSimpleConnectionFeature;
import org.ruminaq.util.GraphicsUtil;

public class CreateSimpleConnectionPointFeature extends AbstractCustomFeature {

  public static final String NAME = "Create connection point";

  public static final int POINT_SIZE = 9;

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
    ICreateService cs = Graphiti.getCreateService();
    IPeCreateService peCreateService = Graphiti.getPeCreateService();
    IPeService peService = Graphiti.getPeService();
    PictogramElement pe = context.getPictogramElements()[0];
    FreeFormConnection ffc = (FreeFormConnection) pe;
    Point p = GraphicsUtil.projectOnConnection(ffc, context.getX(),
        context.getY(), Constants.INTERNAL_PORT);

    Shape s = createConnectionPoint(p.getX(), p.getY(), getDiagram());
    Anchor pointAnchor = cs.createChopboxAnchor(s);

    // delete following bendpoints
    deleteBendpointsNear(ffc, p, 5);
    List<Point> deletedPoints = deleteFollowingBendpoints(ffc, p);

    // end connect on point
    Anchor end = ffc.getEnd();
    ffc.setEnd(pointAnchor);
    for (ConnectionDecorator cd : ffc.getConnectionDecorators()) {
      String arrowDecorator = peService.getPropertyValue(cd,
          AddSimpleConnectionFeature.ARROW_DECORATOR);
      if (Boolean.parseBoolean(arrowDecorator)) {
        ffc.getConnectionDecorators().remove(cd);
        break;
      }
    }

    // create connection from point to last end
    FreeFormConnection connection = peCreateService
        .createFreeFormConnection(getDiagram());
    connection.setStart(pointAnchor);
    connection.setEnd(end);
    connection.getBendpoints().addAll(deletedPoints);

    IGaService gaService = Graphiti.getGaService();
    Polyline polyline = gaService.createPolyline(connection);
    polyline.setLineWidth(1);
    polyline.setForeground(manageColor(IColorConstant.BLACK));

    String isConnectionPoint = peService.getPropertyValue(end.getParent(),
        Constants.SIMPLE_CONNECTION_POINT);
    if (!Boolean.parseBoolean(isConnectionPoint)) {
      ConnectionDecorator cd = peCreateService
          .createConnectionDecorator(connection, false, 1.0, true);
      peService.setPropertyValue(cd, AddSimpleConnectionFeature.ARROW_DECORATOR,
          "true");
      GraphicsUtil.createArrow(cd, getDiagram());
    }

    // link to new connection all bo from old connection
    link(connection, ffc.getLink().getBusinessObjects()
        .toArray(new Object[ffc.getLink().getBusinessObjects().size()]));
  }

  private Shape createConnectionPoint(int x, int y, ContainerShape cs) {
    ICreateService createService = Graphiti.getCreateService();
    IPeService peService = Graphiti.getPeService();
    Shape ret = createService.createShape(cs, true);
    peService.setPropertyValue(ret, Constants.SIMPLE_CONNECTION_POINT, "true");
    Ellipse ellipse = createService.createEllipse(ret);
    Graphiti.getLayoutService().setLocationAndSize(ellipse, x - POINT_SIZE / 2,
        y - POINT_SIZE / 2, POINT_SIZE, POINT_SIZE);
    ellipse.setFilled(true);
    Diagram diagram = peService.getDiagramForPictogramElement(ret);
    ellipse.setForeground(
        Graphiti.getGaService().manageColor(diagram, IColorConstant.BLACK));
    ellipse.setBackground(
        Graphiti.getGaService().manageColor(diagram, IColorConstant.BLACK));
    return ret;
  }

  private void deleteBendpointsNear(FreeFormConnection ffc, Point p, int d) {
    EList<Point> points = ffc.getBendpoints();
    if (points.size() == 0)
      return;

    for (int i = 0; i < points.size(); i++)
      if (GraphicsUtil.distance(points.get(i), p) < d)
        points.remove(i);
  }

  private List<Point> deleteFollowingBendpoints(FreeFormConnection ffc,
      Point p) {
    EList<Point> points = ffc.getBendpoints();
    List<Point> deletedPoints = new ArrayList<>();
    if (points.size() == 0)
      return deletedPoints;

    int x_start = ffc.getStart().getParent().getGraphicsAlgorithm().getX()
        + (ffc.getStart().getParent().getGraphicsAlgorithm().getWidth() >> 1)
        + ((ContainerShape) ffc.getStart().getParent().eContainer())
            .getGraphicsAlgorithm().getX();
    int y_start = ffc.getStart().getParent().getGraphicsAlgorithm().getY()
        + (ffc.getStart().getParent().getGraphicsAlgorithm().getHeight() >> 1)
        + ((ContainerShape) ffc.getStart().getParent().eContainer())
            .getGraphicsAlgorithm().getY();

    for (int i = 0; i < points.size(); i++) {
      if (i == 0) {
        if (GraphicsUtil.pointBelongsToSection(p, x_start, y_start,
            points.get(0).getX(), points.get(0).getY(), 1)) {
          while (i < points.size()) {
            deletedPoints.add(points.get(i));
            points.remove(i);
          }
          return deletedPoints;
        }
      } else {
        if (GraphicsUtil.pointBelongsToSection(p, points.get(i - 1).getX(),
            points.get(i - 1).getY(), points.get(i).getX(),
            points.get(i).getY(), 1)) {
          while (i < points.size()) {
            deletedPoints.add(points.get(i));
            points.remove(i);
          }
          return deletedPoints;
        }
      }
    }
    return deletedPoints;
  }
}
