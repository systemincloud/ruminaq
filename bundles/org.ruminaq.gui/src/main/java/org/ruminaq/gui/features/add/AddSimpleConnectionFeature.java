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
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.add.AddSimpleConnectionFeature.Filter;
import org.ruminaq.gui.model.diagram.DiagramFactory;
import org.ruminaq.gui.model.diagram.SimpleConnectionPointShape;
import org.ruminaq.gui.model.diagram.SimpleConnectionShape;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.model.ruminaq.SimpleConnection;

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

  public AddSimpleConnectionFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canAdd(IAddContext context) {
    return context instanceof IAddConnectionContext;
  }

  @Override
  public PictogramElement add(IAddContext context) {
    return Optional.of(context).filter(IAddConnectionContext.class::isInstance)
        .map(IAddConnectionContext.class::cast)
        .map(IAddConnectionContext::getNewObject)
        .filter(SimpleConnection.class::isInstance)
        .map(SimpleConnection.class::cast).map((SimpleConnection sc) -> {
          SimpleConnectionShape connectionShape = DiagramFactory.eINSTANCE
              .createSimpleConnectionShape();
          connectionShape.setParent(getDiagram());
          connectionShape
              .setStart(((IAddConnectionContext) context).getSourceAnchor());
          connectionShape
              .setEnd(((IAddConnectionContext) context).getTargetAnchor());

          connectionShape.setModelObject(sc);
          addModelObjectToConnectionBeforePoint(connectionShape, sc);

          return connectionShape;
        }).orElse(null);
  }

  private static void addModelObjectToConnectionBeforePoint(Connection connection,
      SimpleConnection addedSimpleConnection) {
    Optional.of(connection).map(Connection::getStart).map(Anchor::getParent)
        .filter(SimpleConnectionPointShape.class::isInstance).ifPresent(
            s -> connection.getStart().getIncomingConnections().forEach((Connection c) -> {
              Optional.of(c).filter(SimpleConnectionShape.class::isInstance)
                  .map(SimpleConnectionShape.class::cast)
                  .ifPresent(scs -> scs.setModelObject(addedSimpleConnection));
              addModelObjectToConnectionBeforePoint(c, addedSimpleConnection);
            }));
  }
}
