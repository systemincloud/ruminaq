package org.ruminaq.tests.common.reddeer;

import java.util.Optional;

import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.impl.CreateConnectionContext;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.internal.parts.ContainerShapeEditPart;
import org.eclipse.reddeer.gef.editor.GEFEditor;
import org.eclipse.reddeer.graphiti.api.GraphitiEditPart;
import org.ruminaq.eclipse.editor.RuminaqEditor;
import org.ruminaq.gui.features.create.CreateSimpleConnectionFeature;
import org.ruminaq.model.util.ModelUtil;

public class CreateSimpleConnection {

  private IFeatureProvider featureProvider;
  private TransactionalEditingDomain editDomain;
  private CreateConnectionContext context;

  public CreateSimpleConnection(GEFEditor gefEditor, GraphitiEditPart sourceEp,
      GraphitiEditPart destinationEp) {
    RuminaqEditor ruminaqEditor = ((RuminaqEditor) gefEditor.getEditorPart());
    this.editDomain = ruminaqEditor.getDiagramBehavior().getEditingDomain();
    this.featureProvider = ruminaqEditor.getDiagramTypeProvider().getFeatureProvider();
    
    PictogramElement sourcePe = Optional.of(sourceEp)
        .map(GraphitiEditPart::getGEFEditPart)
        .filter(ContainerShapeEditPart.class::isInstance)
        .map(o -> (ContainerShapeEditPart) o)
        .map(ContainerShapeEditPart::getPictogramElement).get();
    PictogramElement destinationPe = Optional.of(destinationEp)
        .map(GraphitiEditPart::getGEFEditPart)
        .filter(ContainerShapeEditPart.class::isInstance)
        .map(o -> (ContainerShapeEditPart) o)
        .map(ContainerShapeEditPart::getPictogramElement).get();

    this.context = new CreateConnectionContext();
    context.setSourceAnchor(getAnchor(sourcePe));
    context.setTargetAnchor(getAnchor(destinationPe));
    context.setSourcePictogramElement(sourcePe);
    context.setTargetPictogramElement(destinationPe);
    context.setSourceLocation(null);
    context.setTargetLocation(null);
  }

  private Anchor getAnchor(PictogramElement pe) {
    Anchor ret = null;
    if (pe instanceof Anchor) {
      ret = (Anchor) pe;
    } else if (pe instanceof AnchorContainer) {
      ret = Graphiti.getPeService().getChopboxAnchor((AnchorContainer) pe);
    }
    return ret;
  }

  public void execute() {
    CreateSimpleConnectionFeature f = new CreateSimpleConnectionFeature(featureProvider);
    f.canCreate(context);
    ModelUtil.runModelChange(() -> f.create(context),
        editDomain,
        "Add connection");
  }
}
