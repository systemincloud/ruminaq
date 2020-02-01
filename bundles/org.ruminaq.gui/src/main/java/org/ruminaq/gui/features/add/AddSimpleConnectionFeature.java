/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.add;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddConnectionContext;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddFeature;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.util.IColorConstant;
import org.ruminaq.consts.Constants;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.add.AddSimpleConnectionFeature.Filter;
import org.ruminaq.gui.model.diagram.DiagramFactory;
import org.ruminaq.gui.model.diagram.SimpleConnectionShape;
import org.ruminaq.gui.model.diagram.impl.GuiUtil;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.model.ruminaq.SimpleConnection;
import org.ruminaq.util.Util;

@FeatureFilter(Filter.class)
public class AddSimpleConnectionFeature extends AbstractAddFeature {

  public static class Filter extends AbstractAddFeatureFilter {
    @Override
    public Class<? extends BaseElement> forBusinessObject() {
      return SimpleConnection.class;
    }
  }

  public static final String ARROW_DECORATOR = "ARROW_DECORATOR";

  public AddSimpleConnectionFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canAdd(IAddContext context) {
    return context instanceof IAddConnectionContext;
  }

  @Override
  public PictogramElement add(IAddContext context) {
    IAddConnectionContext addConContext = (IAddConnectionContext) context;
    SimpleConnection addedSimpleConnection = (SimpleConnection) context
        .getNewObject();
    IPeCreateService peCreateService = Graphiti.getPeCreateService();

    SimpleConnectionShape connectionShape = DiagramFactory.eINSTANCE
        .createSimpleConnectionShape();
    connectionShape.setParent(getDiagram());
    connectionShape.setStart(addConContext.getSourceAnchor());
    connectionShape.setEnd(addConContext.getTargetAnchor());

    IGaService gaService = Graphiti.getGaService();
    Polyline polyline = gaService.createPolyline(connectionShape);
    polyline.setLineWidth(1);
    polyline.setForeground(manageColor(IColorConstant.BLACK));

    ConnectionDecorator cd = peCreateService
        .createConnectionDecorator(connectionShape, false, 1.0, true);
    Graphiti.getPeService().setPropertyValue(cd, ARROW_DECORATOR, "true");
    GuiUtil.createArrow(cd, getDiagram());

    connectionShape.setModelObject(addedSimpleConnection);
    linkToConnectionBeforePoint(connectionShape, addedSimpleConnection);

    return connectionShape;
  }

  public void linkToConnectionBeforePoint(Connection connection,
      SimpleConnection addedSimpleConnection) {
    String connectionPointPropertyStart = Graphiti.getPeService()
        .getPropertyValue(connection.getStart().getParent(),
            Constants.SIMPLE_CONNECTION_POINT);
    if (Boolean.parseBoolean(connectionPointPropertyStart)) {
      for (Connection c : connection.getStart().getIncomingConnections()) {
        link(c, Util.concat(c.getLink().getBusinessObjects().toArray(),
            addedSimpleConnection));
        linkToConnectionBeforePoint(c, addedSimpleConnection);
      }
    }
  }

}
