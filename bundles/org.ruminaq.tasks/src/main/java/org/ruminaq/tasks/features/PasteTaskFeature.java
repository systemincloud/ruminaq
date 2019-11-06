package org.ruminaq.tasks.features;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IPasteContext;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.ruminaq.gui.GuiUtil;
import org.ruminaq.gui.features.paste.PasteAnchorTracker;
import org.ruminaq.gui.features.paste.PasteDefaultElementFeature;
import org.ruminaq.gui.features.paste.RuminaqPasteFeature;
import org.ruminaq.model.ModelHandler;
import org.ruminaq.model.ruminaq.InternalPort;
import org.ruminaq.model.ruminaq.MainTask;
import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.tasks.util.TasksUtil;

public class PasteTaskFeature extends RuminaqPasteFeature
    implements PasteAnchorTracker {

  private PictogramElement oldPe;
  private int xMin;
  private int yMin;

  private Map<Anchor, Anchor> anchors = new HashMap<>();

  @Override
  public List<PictogramElement> getNewPictogramElements() {
    return newPes;
  }

  public PasteTaskFeature(IFeatureProvider fp, PictogramElement oldPe, int xMin,
      int yMin) {
    super(fp);
    this.oldPe = oldPe;
    this.xMin = xMin;
    this.yMin = yMin;
  }

  @Override
  public boolean canPaste(IPasteContext context) {
    PictogramElement[] pes = context.getPictogramElements();
    if (pes.length != 1 || !(pes[0] instanceof Diagram))
      return false;
    return true;
  }

  @Override
  public void paste(IPasteContext context) {
    PictogramElement[] pes = context.getPictogramElements();
    int x = context.getX();
    int y = context.getY();

    Diagram diagram = (Diagram) pes[0];

    Task oldBo = null;
    ContainerShape oldLabel = null;

    for (Object o : getAllBusinessObjectsForPictogramElement(oldPe)) {
      if (o instanceof Task)
        oldBo = (Task) o;
      if (GuiUtil.isLabel(o))
        oldLabel = (ContainerShape) o;
    }

    PictogramElement newPe = EcoreUtil.copy(oldPe);
    newPes.add(newPe);
    Task newBo = EcoreUtil.copy(oldBo);

    MainTask mt = ModelHandler.getModel(getDiagram(), getFeatureProvider());
    mt.getTask().add(newBo);

    newPe.getGraphicsAlgorithm()
        .setX(x + newPe.getGraphicsAlgorithm().getX() - xMin);
    newPe.getGraphicsAlgorithm()
        .setY(y + newPe.getGraphicsAlgorithm().getY() - yMin);

    String newId = PasteDefaultElementFeature.setId(newBo.getId(), newBo,
        diagram);

    diagram.getChildren().add((Shape) newPe);

    ContainerShape newLabel = PasteDefaultElementFeature.addLabel(oldPe,
        oldLabel, x, y, newId, diagram, newPe);
    newPes.add(newLabel);

    link(newPe, new Object[] { newBo, newLabel });
    link(newLabel, new Object[] { newBo, newPe });

    updatePictogramElement(newPe);

    updatePictogramElement(newLabel);
    layoutPictogramElement(newLabel);

    updateInternalPorts(newBo, (ContainerShape) newPe);

    Iterator<Shape> itNewChild = ((ContainerShape) newPe).getChildren()
        .iterator();
    Iterator<Shape> itOldChild = ((ContainerShape) oldPe).getChildren()
        .iterator();
    while (itNewChild.hasNext() && itOldChild.hasNext()) {
      Iterator<Anchor> itOld = itOldChild.next().getAnchors().iterator();
      Iterator<Anchor> itNew = itNewChild.next().getAnchors().iterator();
      while (itOld.hasNext() && itNew.hasNext())
        anchors.put(itOld.next(), itNew.next());
    }
  }

  private void updateInternalPorts(Task newBo, ContainerShape newPe) {
    for (Shape newPortShape : newPe.getChildren()) {
      if (AddTaskFeature.isInternalPortLabel(newPortShape))
        continue;
      EList<EObject> os = newPortShape.getLink().getBusinessObjects();
      String id = null;
      ContainerShape l = null;
      for (EObject o : os)
        if (o instanceof InternalPort)
          id = ((InternalPort) o).getId();
        else if (o instanceof ContainerShape)
          l = (ContainerShape) o;
      InternalPort ip = TasksUtil.getInternalPort(newBo, id);

      while (newPortShape.getLink().getBusinessObjects().size() > 0)
        newPortShape.getLink().getBusinessObjects().remove(0);
      while (newPortShape.getLink().getBusinessObjects().size() > 0)
        newPortShape.getLink().getBusinessObjects().remove(0);

      link(newPortShape, new Object[] { ip, l });
      link(l, new Object[] { ip, newPortShape });
    }
  }

  @Override
  public Map<Anchor, Anchor> getAnchors() {
    return anchors;
  }
}
