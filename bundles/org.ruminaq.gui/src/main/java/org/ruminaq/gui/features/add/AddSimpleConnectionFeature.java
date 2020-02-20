/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.add;

import java.util.Optional;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddConnectionContext;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddFeature;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.ruminaq.consts.Constants;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.add.AddSimpleConnectionFeature.Filter;
import org.ruminaq.gui.model.diagram.DiagramFactory;
import org.ruminaq.gui.model.diagram.SimpleConnectionShape;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.model.ruminaq.SimpleConnection;
import org.ruminaq.util.Util;

/**
 * IAddFeature for SimpleConnection.
 * 
 * @author Marek Jagielski
 */
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
    Optional<IAddConnectionContext> addConContext = Optional.of(context)
        .filter(IAddConnectionContext.class::isInstance)
        .map(IAddConnectionContext.class::cast);
    if (addConContext.isPresent()) {
      SimpleConnection addedSimpleConnection = (SimpleConnection) addConContext
          .get().getNewObject();

      SimpleConnectionShape connectionShape = DiagramFactory.eINSTANCE
          .createSimpleConnectionShape();
      connectionShape.setParent(getDiagram());
      connectionShape.setStart(addConContext.get().getSourceAnchor());
      connectionShape.setEnd(addConContext.get().getTargetAnchor());

      connectionShape.setModelObject(addedSimpleConnection);
      linkToConnectionBeforePoint(connectionShape, addedSimpleConnection);

      return connectionShape;
    }
    return null;
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
