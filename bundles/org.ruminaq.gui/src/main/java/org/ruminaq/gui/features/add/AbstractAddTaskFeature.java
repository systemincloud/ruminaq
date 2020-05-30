/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.add;

import java.util.Optional;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.mm.algorithms.MultiText;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeService;
import org.ruminaq.consts.Constants;
import org.ruminaq.gui.model.GuiUtil;
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

    return taskShape;
  }
  
  public TaskShape createTaskShape() {
    return DiagramFactory.eINSTANCE.createTaskShape();
  }

//  private void addDefaultInternalPorts(Task task, TaskShape taskShape) {

//        addLabel(iips);

//        ContainerShape portLabelShape = addInternalPortLabel(getDiagram(),
//            taskShape, ti.getValue0().getId(), width, height, x, y,
//            InternalPortLabelPosition.BOTTOM);
//
//        link(containerShape, new Object[] { ti.getValue0(), portLabelShape });
//        link(portLabelShape, new Object[] { ti.getValue0(), containerShape });
//
//        portLabelShape.setVisible(ti.getValue1().label());
//      }
//
//
//        ContainerShape portLabelShape = addInternalPortLabel(getDiagram(),
//            taskShape, to.getValue0().getId(), width, height, x, y,
//            InternalPortLabelPosition.BOTTOM);
//
//        link(containerShape, new Object[] { to.getValue0(), portLabelShape });
//        link(portLabelShape, new Object[] { to.getValue0(), containerShape });
//
//        portLabelShape.setVisible(to.getValue1().label());
//      }
//    }
//
//    int nbLeft = leftIns.size() + leftOuts.size();
//    if (nbLeft > 0) {
//      int stepLeftPorts = taskShape.getGraphicsAlgorithm().getHeight() / nbLeft;
//      int leftPosition = stepLeftPorts >> 1;
//      for (Pair<InternalInputPort, IN> li : leftIns) {
//        int x = 0;
//        int y = leftPosition - (height >> 1);
//        leftPosition += stepLeftPorts;
//
//        int lineWidth = INPUT_PORT_WIDTH;
//        LineStyle lineStyle = LineStyle.SOLID;
//        if (li.getValue0().isAsynchronous())
//          lineStyle = LineStyle.DOT;
//        ContainerShape containerShape = createPictogramForInternalPort(parent,
//            x, y, width, height, getDiagram(), lineWidth, lineStyle);
//        peCreateService.createChopboxAnchor(containerShape);
//
//        ContainerShape portLabelShape = addInternalPortLabel(getDiagram(),
//            parent, li.getValue0().getId(), width, height, x, y,
//            InternalPortLabelPosition.RIGHT);
//
//        link(containerShape, new Object[] { li.getValue0(), portLabelShape });
//        link(portLabelShape, new Object[] { li.getValue0(), containerShape });
//
//        portLabelShape.setVisible(li.getValue1().label());
//      }
//
//      for (Pair<InternalOutputPort, OUT> lo : leftOuts) {
//        int x = 0;
//        int y = leftPosition - (height >> 1);
//        leftPosition += stepLeftPorts;
//
//        int lineWidth = OUTPUT_PORT_WIDTH;
//        LineStyle lineStyle = LineStyle.SOLID;
//        ContainerShape containerShape = createPictogramForInternalPort(parent,
//            x, y, width, height, getDiagram(), lineWidth, lineStyle);
//        peCreateService.createChopboxAnchor(containerShape);
//
//        ContainerShape portLabelShape = addInternalPortLabel(getDiagram(),
//            parent, lo.getValue0().getId(), width, height, x, y,
//            InternalPortLabelPosition.RIGHT);
//
//        link(containerShape, new Object[] { lo.getValue0(), portLabelShape });
//        link(portLabelShape, new Object[] { lo.getValue0(), containerShape });
//
//        portLabelShape.setVisible(lo.getValue1().label());
//      }
//    }

//    int nbRight = (int) (ins.get().map(SimpleEntry::getValue).map(PortInfo::pos)
//        .filter(Position.RIGHT::equals).count()
//        + outs.get().map(SimpleEntry::getValue).map(PortInfo::pos)
//            .filter(Position.RIGHT::equals).count());

//    if (nbRight > 0) {
//      int stepPorts = taskShape.getHeight() / nbRight;
//      int position = stepPorts >> 1;
//      for (SimpleEntry<InternalInputPort, PortInfo> se : ins.get()
//          .filter(se -> Position.RIGHT == se.getValue().pos())
//          .collect(Collectors.toList())) {
//        InternalInputPortShape ips = DiagramFactory.eINSTANCE
//            .createInternalInputPortShape();
//        int x = taskShape.getWidth() - ips.getWidth();
//        int y = position - (ips.getHeight() >> 1);
//        ips.setContainer(taskShape);
//        ips.setModelObject(se.getKey());
//        ips.setX(x);
//        ips.setY(y);
//        addLabel(ips);
//
//        position += stepPorts;

//        ContainerShape containerShape = createPictogramForInternalPort(parent,
//            x, y, width, height, getDiagram(), lineWidth, lineStyle);
//        peCreateService.createChopboxAnchor(containerShape);
//
//        ContainerShape portLabelShape = addInternalPortLabel(getDiagram(),
//            parent, ri.getValue0().getId(), width, height, x, y,
//            InternalPortLabelPosition.LEFT);
//
//        portLabelShape.setVisible(ri.getValue1().label());
//      }

//      for (SimpleEntry<InternalOutputPort, PortInfo> se : outs.get()
//          .filter(se -> Position.RIGHT == se.getValue().pos())
//          .collect(Collectors.toList())) {
//        InternalOutputPortShape ips = DiagramFactory.eINSTANCE
//            .createInternalOutputPortShape();
//        int x = taskShape.getWidth() - ips.getWidth();
//        int y = position - (ips.getHeight() >> 1);
//        ips.setContainer(taskShape);
//        ips.setModelObject(se.getKey());
//        ips.setX(x);
//        ips.setY(y);
//        addLabel(ips);

//        position += stepPorts;

//        peCreateService.createChopboxAnchor(containerShape);
//
//        ContainerShape portLabelShape = addInternalPortLabel(getDiagram(),
//            parent, ro.getValue0().getId(), width, height, x, y,
//            InternalPortLabelPosition.LEFT);
//
//        portLabelShape.setVisible(ro.getValue1().label());
//      }
//    }
//
//    int nbBottom = bottomIns.size() + bottomOuts.size();
//    if (nbBottom > 0) {
//      int stepBottomPorts = parent.getGraphicsAlgorithm().getWidth() / nbBottom;
//      int bottomPosition = stepBottomPorts >> 1;
//      for (Pair<InternalInputPort, IN> bi : bottomIns) {
//        int x = bottomPosition - (width >> 1);
//        int y = parent.getGraphicsAlgorithm().getHeight() - height;
//        bottomPosition += stepBottomPorts;
//
//        ContainerShape containerShape = createPictogramForInternalPort(parent,
//            x, y, width, height, getDiagram(), lineWidth, lineStyle);
//        peCreateService.createChopboxAnchor(containerShape);
//
//        ContainerShape portLabelShape = addInternalPortLabel(getDiagram(),
//            parent, bi.getValue0().getId(), width, height, x, y,
//            InternalPortLabelPosition.TOP);
//
//        link(containerShape, new Object[] { bi.getValue0(), portLabelShape });
//        link(portLabelShape, new Object[] { bi.getValue0(), containerShape });
//
//        portLabelShape.setVisible(bi.getValue1().label());
//      }
//
//      for (Pair<InternalOutputPort, OUT> bo : bottomOuts) {
//        int x = bottomPosition - (width >> 1);
//        int y = parent.getGraphicsAlgorithm().getHeight() - height;
//        bottomPosition += stepBottomPorts;
//
//        ContainerShape containerShape = createPictogramForInternalPort(parent,
//            x, y, width, height, getDiagram(), lineWidth, lineStyle);
//        peCreateService.createChopboxAnchor(containerShape);
//
//        ContainerShape portLabelShape = addInternalPortLabel(getDiagram(),
//            parent, bo.getValue0().getId(), width, height, x, y,
//            InternalPortLabelPosition.TOP);
//
//        link(containerShape, new Object[] { bo.getValue0(), portLabelShape });
//        link(portLabelShape, new Object[] { bo.getValue0(), containerShape });
//
//        portLabelShape.setVisible(bo.getValue1().label());
//      }
//    }
//  }
  
  public static ContainerShape addInternalPortLabel(Diagram diagram,
      ContainerShape parent, String label, int width, int height, int x, int y,
      InternalPortLabelPosition position) {

    IPeService peService = Graphiti.getPeService();
    IGaService gaService = Graphiti.getGaService();

    ContainerShape textContainerShape = peService.createContainerShape(parent,
        true);
    textContainerShape.setActive(false);

    Rectangle r = gaService.createInvisibleRectangle(textContainerShape);
    MultiText text = gaService.createDefaultMultiText(diagram, r, label);
    text.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
    text.setVerticalAlignment(Orientation.ALIGNMENT_TOP);

    switch (position) {
      case RIGHT:
        GuiUtil.onRightOfShape(text, textContainerShape, width, height, x, y, 0,
            0);
        break;
      case LEFT:
        GuiUtil.onLeftOfShape(text, textContainerShape, width, height, x, y, 0,
            0);
        break;
      case TOP:
        GuiUtil.onTopOfShape(text, textContainerShape, width, height, x, y, 0,
            0);
        break;
      case BOTTOM:
        GuiUtil.onBottomOfShape(text, textContainerShape, width, height, x, y,
            0, 0);
        break;
    }

    Graphiti.getPeService().setPropertyValue(textContainerShape,
        Constants.PORT_LABEL_PROPERTY, "true");

    return textContainerShape;
  }

}
