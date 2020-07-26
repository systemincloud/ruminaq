/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.add;

import java.util.Optional;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.ruminaq.gui.model.diagram.DiagramFactory;
import org.ruminaq.gui.model.diagram.TaskShape;
import org.ruminaq.model.ruminaq.Task;

/**
 * IAddFeature for Task.
 *
 * @author Marek Jagielski
 */
public abstract class AbstractAddTaskFeature extends AbstractAddElementFeature {

  private static final int DEFAULT_TASK_WIDTH = 120;

  private static final int DEFAULT_TASK_HEIGHT = 70;

  public enum InternalPortLabelPosition {
    LEFT, RIGHT, TOP, BOTTOM;
  }

  public AbstractAddTaskFeature(IFeatureProvider fp) {
    super(fp);
  }

  protected int getWidth() {
    return DEFAULT_TASK_WIDTH;
  }

  protected int getHeight() {
    return DEFAULT_TASK_HEIGHT;
  }

  protected String getInsideIconId() {
    return null;
  }

  protected String getInsideIconDesc() {
    return null;
  }

  @Override
  public boolean canAdd(IAddContext context) {
    return context.getNewObject() instanceof Task
        && context.getTargetContainer() instanceof Diagram;
  }

  @Override
  public PictogramElement add(IAddContext context) {
    TaskShape taskShape = createTaskShape();
    taskShape.setContainer(context.getTargetContainer());
    taskShape.setX(context.getX());
    taskShape.setY(context.getY());
    taskShape.setIconId(getInsideIconId());

    taskShape.setWidth(Optional.of(context).map(IAddContext::getWidth)
        .filter(i -> i > 0).orElseGet(this::getWidth));
    taskShape.setHeight(Optional.of(context).map(IAddContext::getHeight)
        .filter(i -> i > 0).orElseGet(this::getHeight));

    Task task = (Task) context.getNewObject();
    taskShape.setModelObject(task);
    addLabel(taskShape);

    updatePictogramElement(taskShape);
    taskShape.getInternalPort().forEach(this::updatePictogramElement);

    return taskShape;
  }

  public TaskShape createTaskShape() {
    return DiagramFactory.eINSTANCE.createTaskShape();
  }

}
