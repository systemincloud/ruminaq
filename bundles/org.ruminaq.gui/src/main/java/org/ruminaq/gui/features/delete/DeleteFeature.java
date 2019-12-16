/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.delete;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.impl.CreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.DeleteContext;
import org.eclipse.graphiti.features.context.impl.MultiDeleteInfo;
import org.eclipse.graphiti.features.context.impl.RemoveContext;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.ruminaq.consts.Constants;
import org.ruminaq.gui.features.create.CreateSimpleConnectionFeature;
import org.ruminaq.gui.features.create.CreateSimpleConnectionPointFeature;
import org.ruminaq.gui.model.diagram.LabeledRuminaqShape;
import org.ruminaq.gui.model.diagram.RuminaqShape;

public class DeleteFeature extends RuminaqDeleteFeature {

  List<AnchorContainer> connectionPointsToDelete = new ArrayList<>();

  Connection connectionToRemove = null;

  public DeleteFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public void preDelete(IDeleteContext context) {
    super.preDelete(context);

    if (context.getPictogramElement() instanceof Shape)
      preDeleteShape((Shape) context.getPictogramElement());
    else if (context.getPictogramElement() instanceof Connection)
      preDeleteConnection((Connection) context.getPictogramElement());
  }

  @Override
  public void postDelete(IDeleteContext context) {
    super.postDelete(context);

    Optional.of(context.getPictogramElement())
        .filter(LabeledRuminaqShape.class::isInstance).map(LabeledRuminaqShape.class::cast)
        .map(LabeledRuminaqShape::getLabel).ifPresent(l -> {
          DeleteContext ctx = new DeleteContext(l);
          getFeatureProvider().getDeleteFeature(ctx).delete(ctx);
          
        });

    for (AnchorContainer ac : connectionPointsToDelete) {
      RemoveContext ctx = new RemoveContext(ac);
      IRemoveFeature removeFeature = getFeatureProvider().getRemoveFeature(ctx);
      if (removeFeature != null)
        removeFeature.remove(ctx);
    }

    if (connectionToRemove != null) {
      DeleteContext ctx = new DeleteContext(connectionToRemove);
      ctx.setMultiDeleteInfo(new MultiDeleteInfo(false, false, 1));
      IDeleteFeature deleteFeature = getFeatureProvider().getDeleteFeature(ctx);
      if (deleteFeature != null)
        deleteFeature.delete(ctx);
    }
  }

  private void preDeleteShape(Shape shape) {
    String connectionPointProperty = Graphiti.getPeService()
        .getPropertyValue(shape, Constants.SIMPLE_CONNECTION_POINT);
    if (Boolean.parseBoolean(connectionPointProperty))
      preDeleteConnectionPoint(shape);
    else
      deleteConnections(shape);
  }

  private void deleteConnections(Shape shape) {
    if (shape instanceof ContainerShape)
      for (Shape child : ((ContainerShape) shape).getChildren())
        deleteConnections(child);

    for (Anchor a : shape.getAnchors()) {
      for (Connection c : Graphiti.getPeService().getAllConnections(a)) {
        DeleteContext ctx = new DeleteContext(c);
        ctx.setMultiDeleteInfo(new MultiDeleteInfo(false, false, 1));
        IDeleteFeature deleteFeature = getFeatureProvider()
            .getDeleteFeature(ctx);
        if (deleteFeature != null)
          deleteFeature.delete(ctx);
      }
    }
  }

  private void preDeleteConnection(Connection con) {
    removeConnectionsAfterConnectionPoint(con);
    if (con.getStart() == null)
      return;
    String connectionPointPropertyStart = Graphiti.getPeService()
        .getPropertyValue(con.getStart().getParent(),
            Constants.SIMPLE_CONNECTION_POINT);
    if (Boolean.parseBoolean(connectionPointPropertyStart)) {
      if (con.getLink().getBusinessObjects().size() > 0)
        removeBusinessObjectsBeforeConnectionPoint(con,
            con.getLink().getBusinessObjects());
      if (con.getStart().getOutgoingConnections().size() == 1)
        connectionToRemove = con.getStart().getIncomingConnections().get(0);
    }
  }

  private void removeBusinessObjectsBeforeConnectionPoint(Connection con,
      EList<EObject> obj) {
    String connectionPointPropertyStart = Graphiti.getPeService()
        .getPropertyValue(con.getStart().getParent(),
            Constants.SIMPLE_CONNECTION_POINT);
    if (Boolean.parseBoolean(connectionPointPropertyStart)) {
      for (Connection c : con.getStart().getIncomingConnections()) {
        c.getLink().getBusinessObjects().remove(obj.get(0));
        removeBusinessObjectsBeforeConnectionPoint(c, obj);
      }
    }
  }

  private void removeConnectionsAfterConnectionPoint(Connection con) {
    String connectionPointPropertyEnd = Graphiti.getPeService()
        .getPropertyValue(con.getEnd().getParent(),
            Constants.SIMPLE_CONNECTION_POINT);
    if (Boolean.parseBoolean(connectionPointPropertyEnd)) {
      while (con.getEnd() != null
          && con.getEnd().getOutgoingConnections().size() > 0) {
        Connection c = con.getEnd().getOutgoingConnections().get(0);
        DeleteContext ctx = new DeleteContext(c);
        ctx.setMultiDeleteInfo(new MultiDeleteInfo(false, false, 1));
        IDeleteFeature deleteFeature = getFeatureProvider()
            .getDeleteFeature(ctx);
        if (deleteFeature != null)
          deleteFeature.delete(ctx);
      }

      if (con.getEnd() != null)
        connectionPointsToDelete.add(con.getEnd().getParent());
    }
  }

  private void preDeleteConnectionPoint(Shape anchorContainer) {
    CreateSimpleConnectionFeature cscf = new CreateSimpleConnectionFeature(
        getFeatureProvider());
    CreateConnectionContext ctx = new CreateConnectionContext();

    FreeFormConnection oldIncoming = (FreeFormConnection) anchorContainer
        .getAnchors().get(0).getIncomingConnections().get(0);
    FreeFormConnection oldOutgoing = (FreeFormConnection) anchorContainer
        .getAnchors().get(0).getOutgoingConnections().get(0);

    while (anchorContainer.getAnchors().get(0).getOutgoingConnections()
        .size() > 1) {
      DeleteContext deleteCtx = new DeleteContext(
          anchorContainer.getAnchors().get(0).getOutgoingConnections().get(1));
      deleteCtx.setMultiDeleteInfo(new MultiDeleteInfo(false, false, 1));
      IDeleteFeature deleteFeature = getFeatureProvider()
          .getDeleteFeature(deleteCtx);
      if (deleteFeature != null)
        deleteFeature.delete(deleteCtx);
    }

    Anchor sourceAnchor = oldIncoming.getStart();
    Anchor targetAnchor = oldOutgoing.getEnd();

    Point bendpoint = Graphiti.getCreateService().createPoint(
        anchorContainer.getGraphicsAlgorithm().getX()
            + (CreateSimpleConnectionPointFeature.POINT_SIZE >> 1),
        anchorContainer.getGraphicsAlgorithm().getY()
            + (CreateSimpleConnectionPointFeature.POINT_SIZE >> 1));

    String isConnectionPoint = Graphiti.getPeService().getPropertyValue(
        targetAnchor.getParent(), Constants.SIMPLE_CONNECTION_POINT);
    if (Boolean.parseBoolean(isConnectionPoint)) {
      oldIncoming.setEnd(targetAnchor);
      oldIncoming.getBendpoints().add(bendpoint);
      oldIncoming.getBendpoints().addAll(oldOutgoing.getBendpoints());

      RemoveContext removeCtx = new RemoveContext(oldOutgoing);
      IRemoveFeature removeFeature = getFeatureProvider()
          .getRemoveFeature(removeCtx);
      if (removeFeature != null)
        removeFeature.remove(removeCtx);
    } else {
      ctx.setSourceAnchor(sourceAnchor);
      ctx.setTargetAnchor(targetAnchor);

      FreeFormConnection connection = (FreeFormConnection) cscf.create(ctx);
      connection.getBendpoints().addAll(oldIncoming.getBendpoints());

      connection.getBendpoints().add(bendpoint);
      connection.getBendpoints().addAll(oldOutgoing.getBendpoints());

      DeleteContext deleteCtx = new DeleteContext(oldOutgoing);
      deleteCtx.setMultiDeleteInfo(new MultiDeleteInfo(false, false, 1));
      IDeleteFeature deleteFeature = getFeatureProvider()
          .getDeleteFeature(deleteCtx);
      if (deleteFeature != null)
        deleteFeature.delete(deleteCtx);
    }
  }
}
