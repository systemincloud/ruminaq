/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tests.common.reddeer;

import java.util.Optional;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.context.impl.ResizeShapeContext;
import org.eclipse.graphiti.ui.internal.parts.ContainerShapeEditPart;
import org.eclipse.reddeer.gef.editor.GEFEditor;
import org.eclipse.reddeer.graphiti.api.GraphitiEditPart;
import org.ruminaq.eclipse.editor.RuminaqEditor;
import org.ruminaq.gui.model.diagram.RuminaqShape;
import org.ruminaq.model.ruminaq.ModelUtil;

public class ResizeShape {

  protected IFeatureProvider featureProvider;
  protected TransactionalEditingDomain editDomain;
  protected ResizeShapeContext context;
  protected IResizeShapeFeature feature;

  public ResizeShape(GEFEditor gefEditor, GraphitiEditPart ep, int direction,
      int deltaX, int deltaY) {
    RuminaqEditor ruminaqEditor = ((RuminaqEditor) gefEditor.getEditorPart());
    this.editDomain = ruminaqEditor.getDiagramBehavior().getEditingDomain();
    this.featureProvider = ruminaqEditor.getDiagramTypeProvider()
        .getFeatureProvider();

    RuminaqShape shape = Optional.of(ep).map(GraphitiEditPart::getGEFEditPart)
        .filter(ContainerShapeEditPart.class::isInstance)
        .map(ContainerShapeEditPart.class::cast)
        .map(ContainerShapeEditPart::getPictogramElement)
        .filter(RuminaqShape.class::isInstance).map(RuminaqShape.class::cast)
        .get();

    this.context = new ResizeShapeContext(shape);
    this.context.setDirection(direction);
    this.context.setX(shape.getX());
    this.context.setY(shape.getY());
    this.context.setWidth(shape.getWidth() + deltaX);
    this.context.setHeight(shape.getHeight() + deltaY);

    this.feature = featureProvider.getResizeShapeFeature(context);
  }

  public void execute() {
    if (feature.canExecute(context)) {
      ModelUtil.runModelChange(() -> {
        feature.execute(context);
      }, editDomain, "Resize shape");
    }
  }
}
