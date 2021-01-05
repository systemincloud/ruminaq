/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.paste;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IPasteContext;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.ruminaq.gui.features.FeaturePredicate;
import org.ruminaq.gui.features.ModelFeatureFilter;
import org.ruminaq.gui.model.diagram.TaskShape;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.model.ruminaq.InternalPort;
import org.ruminaq.model.ruminaq.Task;

/**
 * IPasteFeature for Task.
 *
 * @author Marek Jagielski
 */
@ModelFeatureFilter(PasteTaskFeature.Filter.class)
public class PasteTaskFeature extends LabeledRuminaqPasteFeature<TaskShape>
    implements PasteAnchorTracker {

  private static class Filter implements FeaturePredicate<BaseElement> {
    @Override
    public boolean test(BaseElement bo) {
      return bo instanceof Task;
    }
  }

  private Map<Anchor, Anchor> anchors = new HashMap<>();

  public PasteTaskFeature(IFeatureProvider fp, PictogramElement oldPe, int xMin,
      int yMin) {
    super(fp, (TaskShape) oldPe, xMin, yMin);
  }

  @Override
  public void paste(IPasteContext context) {
    super.paste(context);
    Task newBo = (Task) newPe.getModelObject();
    getRuminaqDiagram().getMainTask().getTask().add(newBo);
    updateInternalPorts(newBo, newPe);
//
//    Iterator<Shape> itNewChild = ((ContainerShape) newPe).getChildren()
//        .iterator();
//    Iterator<Shape> itOldChild = ((ContainerShape) oldPe).getChildren()
//        .iterator();
//    while (itNewChild.hasNext() && itOldChild.hasNext()) {
//      Iterator<Anchor> itOld = itOldChild.next().getAnchors().iterator();
//      Iterator<Anchor> itNew = itNewChild.next().getAnchors().iterator();
//      while (itOld.hasNext() && itNew.hasNext())
//        anchors.put(itOld.next(), itNew.next());
//    }
  }

  private void updateInternalPorts(Task newBo, TaskShape newPe) {
//    for (Shape newPortShape : newPe.getChildren()) {
////      if (AbstractAddTaskFeature.isInternalPortLabel(newPortShape))
////        continue;
//      EList<EObject> os = newPortShape.getLink().getBusinessObjects();
//      String id = null;
//      ContainerShape l = null;
//      for (EObject o : os)
//        if (o instanceof InternalPort)
//          id = ((InternalPort) o).getId();
//        else if (o instanceof ContainerShape)
//          l = (ContainerShape) o;
//      InternalPort ip = newBo.getInputPort(id);
////      InternalPort ip = newBo.getOutputPort(id);
//
//      while (newPortShape.getLink().getBusinessObjects().size() > 0)
//        newPortShape.getLink().getBusinessObjects().remove(0);
//      while (newPortShape.getLink().getBusinessObjects().size() > 0)
//        newPortShape.getLink().getBusinessObjects().remove(0);
//    }
  }

  @Override
  public Map<Anchor, Anchor> getAnchors() {
    return anchors;
  }
}
