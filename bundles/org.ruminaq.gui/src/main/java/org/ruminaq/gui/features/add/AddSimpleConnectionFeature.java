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
import org.ruminaq.gui.GuiUtil;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.add.AddSimpleConnectionFeature.Filter;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.model.ruminaq.SimpleConnection;
import org.ruminaq.util.Util;

@FeatureFilter(Filter.class)
public class AddSimpleConnectionFeature extends AbstractAddFeature {

  static class Filter extends AddFeatureFilter {
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

    // CONNECTION WITH POLYLINE
    Connection connection = peCreateService
        .createFreeFormConnection(getDiagram());
    connection.setStart(addConContext.getSourceAnchor());
    connection.setEnd(addConContext.getTargetAnchor());

    IGaService gaService = Graphiti.getGaService();
    Polyline polyline = gaService.createPolyline(connection);
    polyline.setLineWidth(1);
    polyline.setForeground(manageColor(IColorConstant.BLACK));

    ConnectionDecorator cd = peCreateService
        .createConnectionDecorator(connection, false, 1.0, true);
    Graphiti.getPeService().setPropertyValue(cd, ARROW_DECORATOR, "true");
    GuiUtil.createArrow(cd, getDiagram());

    // create link and wire it
    link(connection, addedSimpleConnection);
    linkToConnectionBeforePoint(connection, addedSimpleConnection);

    return connection;
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
