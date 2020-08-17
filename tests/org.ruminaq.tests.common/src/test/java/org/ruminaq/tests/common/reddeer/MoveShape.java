/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tests.common.reddeer;

import java.util.Optional;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IMoveFeature;
import org.eclipse.graphiti.features.context.impl.MoveShapeContext;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.ui.internal.parts.ContainerShapeEditPart;
import org.eclipse.reddeer.gef.editor.GEFEditor;
import org.eclipse.reddeer.graphiti.api.GraphitiEditPart;
import org.ruminaq.eclipse.editor.RuminaqEditor;
import org.ruminaq.gui.model.diagram.RuminaqShape;
import org.ruminaq.model.ruminaq.ModelUtil;

public class MoveShape {

  protected IFeatureProvider featureProvider;
  protected TransactionalEditingDomain editDomain;
  protected MoveShapeContext context;
  protected IMoveFeature feature;

  public MoveShape(GEFEditor gefEditor, GraphitiEditPart ep, int deltaX,
      int deltaY) {
    this(gefEditor, ep, deltaX, deltaY, null);
  }

  public MoveShape(GEFEditor gefEditor, GraphitiEditPart ep, int deltaX,
      int deltaY, ContainerShape targetShape) {
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

    this.context = new MoveShapeContext(shape);
    this.context.setSourceContainer(shape.getContainer());
    if (Optional.ofNullable(targetShape).isPresent()) {
      this.context.setTargetContainer(targetShape);
      this.context.setX(deltaX);
      this.context.setY(deltaY);
    } else {
      this.context.setTargetContainer(shape.getContainer());
      this.context.setX(shape.getX() + deltaX);
      this.context.setY(shape.getY() + deltaY);
    }
    this.context.setDeltaX(deltaX);
    this.context.setDeltaY(deltaY);
    this.feature = featureProvider.getMoveShapeFeature(context);
  }

  public void execute() {
    feature.canExecute(context);
    ModelUtil.runModelChange(() -> {
      feature.execute(context);
    }, editDomain, "Move shape");
  }
}
