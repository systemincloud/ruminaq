/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.create;

import java.util.Optional;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.impl.AbstractCreateConnectionFeature;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.ruminaq.gui.model.diagram.RuminaqDiagram;
import org.ruminaq.gui.model.diagram.RuminaqShape;
import org.ruminaq.model.ruminaq.FlowSource;
import org.ruminaq.model.ruminaq.FlowTarget;
import org.ruminaq.model.ruminaq.RuminaqFactory;
import org.ruminaq.model.ruminaq.SimpleConnection;

/**
 * SimpleConnection create feature.
 * 
 * @author Marek Jagielski
 */
public class CreateSimpleConnectionFeature
    extends AbstractCreateConnectionFeature {

  public CreateSimpleConnectionFeature(IFeatureProvider fp) {
    super(fp, "Simple Connection", null);
  }

  @Override
  public boolean canStartConnection(ICreateConnectionContext context) {
    return Optional.ofNullable(getFlowEnd(context.getSourceAnchor(), FlowSource.class))
        .isPresent();
  }

  @Override
  public boolean canCreate(ICreateConnectionContext context) {
    Optional<FlowSource> source = getFlowEnd(context.getSourceAnchor(), FlowSource.class);
    Optional<FlowTarget> target = getFlowEnd(context.getTargetAnchor(), FlowTarget.class);
    return source.isPresent() && target.isPresent()
        && context.getTargetAnchor().getIncomingConnections().isEmpty();
  }

  @Override
  public Connection create(ICreateConnectionContext context) {
    Connection newConnection = null;
    Optional<FlowSource> source = getFlowEnd(context.getSourceAnchor(), FlowSource.class);
    Optional<FlowTarget> target = getFlowEnd(context.getTargetAnchor(), FlowTarget.class);

    if (source.isPresent() && target.isPresent()) {
      SimpleConnection simpleConnection = createSimpleConnection(source.get(),
          target.get());
      AddConnectionContext addContext = new AddConnectionContext(
          context.getSourceAnchor(), context.getTargetAnchor());
      addContext.setNewObject(simpleConnection);
      newConnection = (Connection) getFeatureProvider()
          .addIfPossible(addContext);
    }

    return newConnection;
  }

  protected RuminaqDiagram getRuminaqDiagram() {
    return (RuminaqDiagram) getDiagram();
  }

  private static <T> Optional<T> getFlowEnd(Anchor anchor, Class<T> type) {
    return Optional.ofNullable(anchor).map(Anchor::getParent)
        .filter(RuminaqShape.class::isInstance).map(RuminaqShape.class::cast)
        .map(RuminaqShape::getModelObject).filter(type::isInstance)
        .map(type::cast);
  }

  private SimpleConnection createSimpleConnection(FlowSource source,
      FlowTarget target) {
    SimpleConnection simpleConnection = RuminaqFactory.eINSTANCE
        .createSimpleConnection();
    simpleConnection.setSourceRef(source);
    simpleConnection.setTargetRef(target);
    getRuminaqDiagram().getMainTask().getConnection().add(simpleConnection);

    return simpleConnection;
  }
}
