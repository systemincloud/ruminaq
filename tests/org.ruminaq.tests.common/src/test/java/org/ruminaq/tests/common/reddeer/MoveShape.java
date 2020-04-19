package org.ruminaq.tests.common.reddeer;

import java.util.Optional;

import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.impl.MoveShapeContext;
import org.eclipse.graphiti.ui.internal.parts.ContainerShapeEditPart;
import org.eclipse.reddeer.gef.editor.GEFEditor;
import org.eclipse.reddeer.graphiti.api.GraphitiEditPart;
import org.ruminaq.eclipse.editor.RuminaqEditor;
import org.ruminaq.gui.features.move.MoveElementFeature;
import org.ruminaq.gui.model.diagram.RuminaqShape;
import org.ruminaq.model.ruminaq.ModelUtil;

public class MoveShape {

  private IFeatureProvider featureProvider;
  private TransactionalEditingDomain editDomain;
  private MoveShapeContext context;

  public MoveShape(GEFEditor gefEditor, GraphitiEditPart ep, int deltaX,
      int deltaY) {

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
    this.context.setTargetContainer(shape.getContainer());
    this.context.setX(shape.getX() + deltaX);
    this.context.setY(shape.getY() + deltaY);
    this.context.setDeltaX(deltaX);
    this.context.setDeltaY(deltaY);
  }

  public void execute() {
    MoveElementFeature f = new MoveElementFeature(featureProvider);
    f.canMoveShape(context);
    ModelUtil.runModelChange(() -> f.moveShape(context), editDomain,
        "Move shape");
  }
}
