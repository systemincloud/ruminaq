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
    return Optional.ofNullable(getFlowSource(context.getSourceAnchor()))
        .isPresent();
  }

  @Override
  public boolean canCreate(ICreateConnectionContext context) {
    Optional<FlowSource> source = getFlowSource(context.getSourceAnchor());
    Optional<FlowTarget> target = getFlowTarget(context.getTargetAnchor());

    return source.isPresent() && target.isPresent()
        && context.getTargetAnchor().getIncomingConnections().isEmpty();
  }

  @Override
  public Connection create(ICreateConnectionContext context) {
    Connection newConnection = null;

    Optional<FlowSource> source = getFlowSource(context.getSourceAnchor());
    Optional<FlowTarget> target = getFlowTarget(context.getTargetAnchor());

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

  private static Optional<FlowSource> getFlowSource(Anchor anchor) {
    return Optional.ofNullable(anchor).map(Anchor::getParent)
        .filter(RuminaqShape.class::isInstance).map(RuminaqShape.class::cast)
        .map(RuminaqShape::getModelObject).filter(FlowSource.class::isInstance)
        .map(FlowSource.class::cast);
  }

  private static Optional<FlowTarget> getFlowTarget(Anchor anchor) {
    return Optional.ofNullable(anchor).map(Anchor::getParent)
        .filter(RuminaqShape.class::isInstance).map(RuminaqShape.class::cast)
        .map(RuminaqShape::getModelObject).filter(FlowTarget.class::isInstance)
        .map(FlowTarget.class::cast);
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
