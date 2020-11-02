/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tests.common.reddeer;

import java.util.Optional;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReconnectionFeature;
import org.eclipse.graphiti.features.context.impl.ReconnectionContext;
import org.eclipse.graphiti.internal.datatypes.impl.LocationImpl;
import org.eclipse.graphiti.ui.internal.parts.ContainerShapeEditPart;
import org.eclipse.reddeer.gef.editor.GEFEditor;
import org.eclipse.reddeer.graphiti.api.GraphitiEditPart;
import org.ruminaq.eclipse.editor.RuminaqEditor;
import org.ruminaq.gui.model.diagram.RuminaqShape;
import org.ruminaq.model.ruminaq.ModelUtil;

public class ReconnectConnection {

  protected IFeatureProvider featureProvider;
  protected TransactionalEditingDomain editDomain;
  protected ReconnectionContext context;
  protected IReconnectionFeature feature;

  private static RuminaqShape shape(WithBoGraphitiEditPart ep) {
    return Optional.of(ep).map(GraphitiEditPart::getGEFEditPart)
        .filter(ContainerShapeEditPart.class::isInstance)
        .map(ContainerShapeEditPart.class::cast)
        .map(ContainerShapeEditPart::getPictogramElement)
        .filter(RuminaqShape.class::isInstance).map(RuminaqShape.class::cast)
        .get();
  }

  public ReconnectConnection(GEFEditor gefEditor, WithBoGraphitiEditPart ep1,
      WithBoGraphitiEditPart ep2) {
    RuminaqEditor ruminaqEditor = ((RuminaqEditor) gefEditor.getEditorPart());
    this.editDomain = ruminaqEditor.getDiagramBehavior().getEditingDomain();
    this.featureProvider = ruminaqEditor.getDiagramTypeProvider()
        .getFeatureProvider();

    RuminaqShape shape1 = shape(ep1);
    RuminaqShape shape2 = shape(ep2);

    this.context = new ReconnectionContext(
        shape1.getAnchors().get(0).getIncomingConnections().get(0),
        shape1.getAnchors().get(0), shape2.getAnchors().get(0),
        new LocationImpl(shape2.getX(), shape2.getY()));

    this.feature = featureProvider.getReconnectionFeature(context);
  }

  public void execute() {
    if (feature.canExecute(context)) {
      ModelUtil.runModelChange(() -> {
        feature.execute(context);
      }, editDomain, "Reconnect connection");
    }
  }

}
