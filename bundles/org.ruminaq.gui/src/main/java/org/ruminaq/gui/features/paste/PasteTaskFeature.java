/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.paste;

import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IPasteContext;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.ruminaq.gui.features.FeaturePredicate;
import org.ruminaq.gui.features.ModelFeatureFilter;
import org.ruminaq.gui.model.diagram.InternalInputPortShape;
import org.ruminaq.gui.model.diagram.InternalOutputPortShape;
import org.ruminaq.gui.model.diagram.InternalPortShape;
import org.ruminaq.gui.model.diagram.TaskShape;
import org.ruminaq.model.ruminaq.BaseElement;
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
  }

  private static void updateInternalPorts(Task newTask,
      TaskShape newTaskShape) {
    newTaskShape.getInternalPort().stream()
        .filter(InternalInputPortShape.class::isInstance)
        .forEach(ips -> newTask.getInputPort().stream()
            .filter(ip -> ip.getId().equals(ips.getModelObject().getId()))
            .findFirst().ifPresent(ips::setModelObject));
    newTaskShape.getInternalPort().stream()
        .filter(InternalOutputPortShape.class::isInstance)
        .forEach(ips -> newTask.getOutputPort().stream()
            .filter(ip -> ip.getId().equals(ips.getModelObject().getId()))
            .findFirst().ifPresent(ips::setModelObject));
  }

  @Override
  public Map<Anchor, Anchor> getAnchors() {
    Iterator<InternalPortShape> keyIter = oldPe.getInternalPort().iterator();
    Iterator<InternalPortShape> valIter = newPe.getInternalPort().iterator();
    return IntStream.range(0, oldPe.getInternalPort().size()).boxed()
        .collect(Collectors.toMap(_i -> keyIter.next().getAnchors().get(0),
            _i -> valIter.next().getAnchors().get(0)));
  }
}
