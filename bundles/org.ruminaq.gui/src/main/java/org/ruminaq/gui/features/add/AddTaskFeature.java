/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.add;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.impl.MoveShapeContext;
import org.eclipse.graphiti.mm.algorithms.MultiText;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.algorithms.styles.LineStyle;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.services.IPeService;
import org.javatuples.Pair;
import org.ruminaq.consts.Constants;
import org.ruminaq.gui.TasksUtil;
import org.ruminaq.gui.features.move.MoveInternalPortFeature;
import org.ruminaq.gui.model.diagram.DiagramFactory;
import org.ruminaq.gui.model.diagram.TaskShape;
import org.ruminaq.gui.model.diagram.impl.GuiUtil;
import org.ruminaq.model.desc.IN;
import org.ruminaq.model.desc.OUT;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.model.desc.Position;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.model.ruminaq.InternalInputPort;
import org.ruminaq.model.ruminaq.InternalOutputPort;
import org.ruminaq.model.ruminaq.InternalPort;
import org.ruminaq.model.ruminaq.Task;

/**
 * IAddFeature for Task.
 *
 * @author Marek Jagielski
 */
public abstract class AddTaskFeature extends AbstractAddElementFeature {

  private static final int DEFAULT_TASK_WIDTH = 120;

  private static final int DEFAULT_TASK_HEIGHT = 70;

  public static final int PORT_SIZE = 10;
  public static final int INPUT_PORT_WIDTH = 1;
  public static final int OUTPUT_PORT_WIDTH = 2;

  public static final String ICON_DESC_PROPERTY = "icon-description";

  public enum InternalPortLabelPosition {
    LEFT, RIGHT, TOP, BOTTOM;
  }

  protected int getWidth() {
    return DEFAULT_TASK_WIDTH;
  }

  protected int getHeight() {
    return DEFAULT_TASK_HEIGHT;
  }

  protected boolean useIconInsideShape() {
    return false;
  }

  protected String getInsideIconId() {
    return "";
  }

  protected String getInsideIconDesc() {
    return null;
  }

  protected abstract Class<? extends PortsDescr> getPortsDescription();

  public AddTaskFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canAdd(IAddContext context) {
    return context.getNewObject() instanceof Task
        && context.getTargetContainer() instanceof Diagram;
  }

  @Override
  public PictogramElement add(IAddContext context) {
    Task addedTask = (Task) context.getNewObject();

    TaskShape taskShape = DiagramFactory.eINSTANCE.createTaskShape();
    taskShape.setContainer(context.getTargetContainer());
    taskShape.setX(context.getX());
    taskShape.setY(context.getY());
    taskShape.setIconId(getInsideIconId());

    taskShape.setWidth(Optional.of(context).map(IAddContext::getWidth)
        .filter(i -> i > 0).orElseGet(() -> getWidth()));
    taskShape.setHeight(Optional.of(context).map(IAddContext::getHeight)
        .filter(i -> i > 0).orElseGet(() -> getHeight()));

    BaseElement task = (BaseElement) context.getNewObject();
    taskShape.setModelObject(task);
    addLabel(taskShape);

    addInternalPorts(addedTask, taskShape);
    updatePictogramElement(taskShape);

    return taskShape;
  }

  private void addInternalPorts(Task addedTask, ContainerShape parent) {
    final IPeCreateService peCreateService = Graphiti.getPeCreateService();
    Class<? extends PortsDescr> pd = getPortsDescription();

    List<Pair<InternalInputPort, IN>> topIns = new LinkedList<>();
    List<Pair<InternalOutputPort, OUT>> topOuts = new LinkedList<>();
    List<Pair<InternalInputPort, IN>> leftIns = new LinkedList<>();
    List<Pair<InternalOutputPort, OUT>> leftOuts = new LinkedList<>();
    List<Pair<InternalInputPort, IN>> rightIns = new LinkedList<>();
    List<Pair<InternalOutputPort, OUT>> rightOuts = new LinkedList<>();
    List<Pair<InternalInputPort, IN>> bottomIns = new LinkedList<>();
    List<Pair<InternalOutputPort, OUT>> bottomOuts = new LinkedList<>();

    if (addedTask.getInputPort().size() > 0) {
      np: for (InternalInputPort iip : addedTask.getInputPort()) {
        for (Field f : pd.getFields()) {
          IN in = f.getAnnotation(IN.class);
          if (in != null) {
            if (in.n() == 1 && iip.getId().equals(in.name()) || (in.n() > 1)
                && TasksUtil.isMultiplePortId(iip.getId(), in.name())) {
              switch (in.pos()) {
                case TOP:
                  topIns.add(new Pair<InternalInputPort, IN>(iip, in));
                  break;
                case LEFT:
                  leftIns.add(new Pair<InternalInputPort, IN>(iip, in));
                  break;
                case RIGHT:
                  rightIns.add(new Pair<InternalInputPort, IN>(iip, in));
                  break;
                case BOTTOM:
                  bottomIns.add(new Pair<InternalInputPort, IN>(iip, in));
                  break;
              }
              continue np;
            }
          }
        }
      }
    }

    if (addedTask.getOutputPort().size() > 0) {
      np: for (InternalOutputPort iop : addedTask.getOutputPort()) {
        for (Field f : pd.getFields()) {
          OUT out = f.getAnnotation(OUT.class);
          if (out != null) {
            if (out.n() == 1 && iop.getId().equals(out.name()) || (out.n() > 1)
                && TasksUtil.isMultiplePortId(iop.getId(), out.name())) {
              switch (out.pos()) {
                case TOP:
                  topOuts.add(new Pair<InternalOutputPort, OUT>(iop, out));
                  break;
                case LEFT:
                  leftOuts.add(new Pair<InternalOutputPort, OUT>(iop, out));
                  break;
                case RIGHT:
                  rightOuts.add(new Pair<InternalOutputPort, OUT>(iop, out));
                  break;
                case BOTTOM:
                  bottomOuts.add(new Pair<InternalOutputPort, OUT>(iop, out));
                  break;
              }
              continue np;
            }
          }
        }
      }
    }

    int width = PORT_SIZE;
    int height = PORT_SIZE;

    int nbTop = topIns.size() + topOuts.size();
    if (nbTop > 0) {
      int stepTopPorts = parent.getGraphicsAlgorithm().getWidth() / nbTop;
      int topPosition = stepTopPorts >> 1;
      for (Pair<InternalInputPort, IN> ti : topIns) {
        int x = topPosition - (width >> 1);
        int y = 0;
        topPosition += stepTopPorts;

        int lineWidth = INPUT_PORT_WIDTH;
        LineStyle lineStyle = LineStyle.SOLID;
        if (ti.getValue0().isAsynchronous())
          lineStyle = LineStyle.DOT;
        ContainerShape containerShape = createPictogramForInternalPort(parent,
            x, y, width, height, getDiagram(), lineWidth, lineStyle);
        peCreateService.createChopboxAnchor(containerShape);

        ContainerShape portLabelShape = addInternalPortLabel(getDiagram(),
            parent, ti.getValue0().getId(), width, height, x, y,
            InternalPortLabelPosition.BOTTOM);

        link(containerShape, new Object[] { ti.getValue0(), portLabelShape });
        link(portLabelShape, new Object[] { ti.getValue0(), containerShape });

        portLabelShape.setVisible(ti.getValue1().label());
      }

      for (Pair<InternalOutputPort, OUT> to : topOuts) {
        int x = topPosition - (width >> 1);
        int y = 0;
        topPosition += stepTopPorts;

        int lineWidth = OUTPUT_PORT_WIDTH;
        LineStyle lineStyle = LineStyle.SOLID;
        ContainerShape containerShape = createPictogramForInternalPort(parent,
            x, y, width, height, getDiagram(), lineWidth, lineStyle);
        peCreateService.createChopboxAnchor(containerShape);

        ContainerShape portLabelShape = addInternalPortLabel(getDiagram(),
            parent, to.getValue0().getId(), width, height, x, y,
            InternalPortLabelPosition.BOTTOM);

        link(containerShape, new Object[] { to.getValue0(), portLabelShape });
        link(portLabelShape, new Object[] { to.getValue0(), containerShape });

        portLabelShape.setVisible(to.getValue1().label());
      }
    }

    int nbLeft = leftIns.size() + leftOuts.size();
    if (nbLeft > 0) {
      int stepLeftPorts = parent.getGraphicsAlgorithm().getHeight() / nbLeft;
      int leftPosition = stepLeftPorts >> 1;
      for (Pair<InternalInputPort, IN> li : leftIns) {
        int x = 0;
        int y = leftPosition - (height >> 1);
        leftPosition += stepLeftPorts;

        int lineWidth = INPUT_PORT_WIDTH;
        LineStyle lineStyle = LineStyle.SOLID;
        if (li.getValue0().isAsynchronous())
          lineStyle = LineStyle.DOT;
        ContainerShape containerShape = createPictogramForInternalPort(parent,
            x, y, width, height, getDiagram(), lineWidth, lineStyle);
        peCreateService.createChopboxAnchor(containerShape);

        ContainerShape portLabelShape = addInternalPortLabel(getDiagram(),
            parent, li.getValue0().getId(), width, height, x, y,
            InternalPortLabelPosition.RIGHT);

        link(containerShape, new Object[] { li.getValue0(), portLabelShape });
        link(portLabelShape, new Object[] { li.getValue0(), containerShape });

        portLabelShape.setVisible(li.getValue1().label());
      }

      for (Pair<InternalOutputPort, OUT> lo : leftOuts) {
        int x = 0;
        int y = leftPosition - (height >> 1);
        leftPosition += stepLeftPorts;

        int lineWidth = OUTPUT_PORT_WIDTH;
        LineStyle lineStyle = LineStyle.SOLID;
        ContainerShape containerShape = createPictogramForInternalPort(parent,
            x, y, width, height, getDiagram(), lineWidth, lineStyle);
        peCreateService.createChopboxAnchor(containerShape);

        ContainerShape portLabelShape = addInternalPortLabel(getDiagram(),
            parent, lo.getValue0().getId(), width, height, x, y,
            InternalPortLabelPosition.RIGHT);

        link(containerShape, new Object[] { lo.getValue0(), portLabelShape });
        link(portLabelShape, new Object[] { lo.getValue0(), containerShape });

        portLabelShape.setVisible(lo.getValue1().label());
      }
    }

    int nbRight = rightIns.size() + rightOuts.size();
    if (nbRight > 0) {
      int stepRightPorts = parent.getGraphicsAlgorithm().getHeight() / nbRight;
      int rightPosition = stepRightPorts >> 1;
      for (Pair<InternalInputPort, IN> ri : rightIns) {
        int x = parent.getGraphicsAlgorithm().getWidth() - width;
        int y = rightPosition - (height >> 1);
        rightPosition += stepRightPorts;

        int lineWidth = INPUT_PORT_WIDTH;
        LineStyle lineStyle = LineStyle.SOLID;
        if (ri.getValue0().isAsynchronous())
          lineStyle = LineStyle.DOT;
        ContainerShape containerShape = createPictogramForInternalPort(parent,
            x, y, width, height, getDiagram(), lineWidth, lineStyle);
        peCreateService.createChopboxAnchor(containerShape);

        ContainerShape portLabelShape = addInternalPortLabel(getDiagram(),
            parent, ri.getValue0().getId(), width, height, x, y,
            InternalPortLabelPosition.LEFT);

        link(containerShape, new Object[] { ri.getValue0(), portLabelShape });
        link(portLabelShape, new Object[] { ri.getValue0(), containerShape });

        portLabelShape.setVisible(ri.getValue1().label());
      }

      for (Pair<InternalOutputPort, OUT> ro : rightOuts) {
        int x = parent.getGraphicsAlgorithm().getWidth() - width;
        int y = rightPosition - (width >> 1);
        rightPosition += stepRightPorts;

        int lineWidth = OUTPUT_PORT_WIDTH;
        LineStyle lineStyle = LineStyle.SOLID;
        ContainerShape containerShape = createPictogramForInternalPort(parent,
            x, y, width, height, getDiagram(), lineWidth, lineStyle);
        peCreateService.createChopboxAnchor(containerShape);

        ContainerShape portLabelShape = addInternalPortLabel(getDiagram(),
            parent, ro.getValue0().getId(), width, height, x, y,
            InternalPortLabelPosition.LEFT);

        link(containerShape, new Object[] { ro.getValue0(), portLabelShape });
        link(portLabelShape, new Object[] { ro.getValue0(), containerShape });

        portLabelShape.setVisible(ro.getValue1().label());
      }
    }

    int nbBottom = bottomIns.size() + bottomOuts.size();
    if (nbBottom > 0) {
      int stepBottomPorts = parent.getGraphicsAlgorithm().getWidth() / nbBottom;
      int bottomPosition = stepBottomPorts >> 1;
      for (Pair<InternalInputPort, IN> bi : bottomIns) {
        int x = bottomPosition - (width >> 1);
        int y = parent.getGraphicsAlgorithm().getHeight() - height;
        bottomPosition += stepBottomPorts;

        int lineWidth = INPUT_PORT_WIDTH;
        LineStyle lineStyle = LineStyle.SOLID;
        if (bi.getValue0().isAsynchronous())
          lineStyle = LineStyle.DOT;
        ContainerShape containerShape = createPictogramForInternalPort(parent,
            x, y, width, height, getDiagram(), lineWidth, lineStyle);
        peCreateService.createChopboxAnchor(containerShape);

        ContainerShape portLabelShape = addInternalPortLabel(getDiagram(),
            parent, bi.getValue0().getId(), width, height, x, y,
            InternalPortLabelPosition.TOP);

        link(containerShape, new Object[] { bi.getValue0(), portLabelShape });
        link(portLabelShape, new Object[] { bi.getValue0(), containerShape });

        portLabelShape.setVisible(bi.getValue1().label());
      }

      for (Pair<InternalOutputPort, OUT> bo : bottomOuts) {
        int x = bottomPosition - (width >> 1);
        int y = parent.getGraphicsAlgorithm().getHeight() - height;
        bottomPosition += stepBottomPorts;

        int lineWidth = OUTPUT_PORT_WIDTH;
        LineStyle lineStyle = LineStyle.SOLID;
        ContainerShape containerShape = createPictogramForInternalPort(parent,
            x, y, width, height, getDiagram(), lineWidth, lineStyle);
        peCreateService.createChopboxAnchor(containerShape);

        ContainerShape portLabelShape = addInternalPortLabel(getDiagram(),
            parent, bo.getValue0().getId(), width, height, x, y,
            InternalPortLabelPosition.TOP);

        link(containerShape, new Object[] { bo.getValue0(), portLabelShape });
        link(portLabelShape, new Object[] { bo.getValue0(), containerShape });

        portLabelShape.setVisible(bo.getValue1().label());
      }
    }
  }

  public static ContainerShape createPictogramForInternalPort(
      ContainerShape parent, int x, int y, int width, int height,
      Diagram diagram, int lineWidth, LineStyle lineStyle) {
    final IPeCreateService peCreateService = Graphiti.getPeCreateService();
    final IGaService gaService = Graphiti.getGaService();

    final ContainerShape containerShape = peCreateService
        .createContainerShape(parent, true);
    final RoundedRectangle invisibleRectangle = gaService
        .createRoundedRectangle(containerShape, 5, 5);
    invisibleRectangle.setFilled(false);
    invisibleRectangle.setLineVisible(false);
    gaService.setLocationAndSize(invisibleRectangle, x, y, width, height);

    final RoundedRectangle rectangle = gaService
        .createRoundedRectangle(invisibleRectangle, 5, 5);
    rectangle.setParentGraphicsAlgorithm(invisibleRectangle);
//    rectangle.setStyle(InternalPortStyle.getStyle(diagram));
    rectangle.setLineWidth(lineWidth);
    rectangle.setLineStyle(lineStyle);
    gaService.setLocationAndSize(rectangle, 0, 0, width, height);

    Graphiti.getPeService().setPropertyValue(containerShape,
        Constants.INTERNAL_PORT, "true");

    return containerShape;
  }

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

  public static void distributePortsOnLeft(ContainerShape parent,
      IFeatureProvider fp) {
    distributePortsVertically(parent, 0, fp);
  }

  public static void distributePortsOnRight(ContainerShape parent,
      IFeatureProvider fp) {
    distributePortsVertically(parent,
        parent.getGraphicsAlgorithm().getWidth() - PORT_SIZE, fp);
  }

  public static void distributePortsOnTop(ContainerShape parent,
      IFeatureProvider fp) {
    distributePortsHorizontally(parent, 0, fp);
  }

  public static void distributePortsOnBottom(ContainerShape parent,
      IFeatureProvider fp) {
    distributePortsHorizontally(parent,
        parent.getGraphicsAlgorithm().getHeight() - PORT_SIZE, fp);
  }

  private static void distributePortsVertically(ContainerShape parent, int x,
      IFeatureProvider fp) {
    LinkedList<Shape> orderedChilds = new LinkedList<>();

    loop: for (Shape child : parent.getChildren()) {
      if (isInternalPortLabel(child)) {
        if (child.getGraphicsAlgorithm().getX() == x) {
          for (Shape s : orderedChilds) {
            if (child.getGraphicsAlgorithm().getY() < s.getGraphicsAlgorithm()
                .getY()) {
              orderedChilds.add(orderedChilds.indexOf(s), child);
              continue loop;
            }
          }
          orderedChilds.addLast(child);
        }
      }
    }

    if (orderedChilds.size() != 0) {
      int stepPorts = parent.getGraphicsAlgorithm().getHeight()
          / orderedChilds.size();
      int position = (stepPorts >> 1) - (PORT_SIZE >> 1);
      for (Shape child : orderedChilds) {
        int dy = position - child.getGraphicsAlgorithm().getY();
        MoveShapeContext moveShapeContext = new MoveShapeContext(child);
        moveShapeContext.setX(child.getGraphicsAlgorithm().getX());
        moveShapeContext.setY(position);
        moveShapeContext.setDeltaX(0);
        moveShapeContext.setDeltaY(dy);
        moveShapeContext.setSourceContainer(child.getContainer());
        moveShapeContext.setTargetContainer(child.getContainer());
        MoveInternalPortFeature moveFeature = new MoveInternalPortFeature(fp);
        if (moveFeature.canMoveShape(moveShapeContext)) {
          moveFeature.moveShape(moveShapeContext);
          moveFeature.postMoveShape(moveShapeContext);
        }
        position += stepPorts;
      }
    }
  }

  private static void distributePortsHorizontally(ContainerShape parent, int y,
      IFeatureProvider fp) {
    LinkedList<Shape> orderedChilds = new LinkedList<>();

    loop: for (Shape child : parent.getChildren()) {
      if (isInternalPortLabel(child)) {
        if (child.getGraphicsAlgorithm().getY() == y) {
          for (Shape s : orderedChilds) {
            if (child.getGraphicsAlgorithm().getX() < s.getGraphicsAlgorithm()
                .getX()) {
              orderedChilds.add(orderedChilds.indexOf(s), child);
              continue loop;
            }
          }
          orderedChilds.addLast(child);
        }
      }
    }

    if (orderedChilds.size() != 0) {
      int stepPorts = parent.getGraphicsAlgorithm().getWidth()
          / orderedChilds.size();
      int position = (stepPorts >> 1) - (PORT_SIZE >> 1);
      for (Shape child : orderedChilds) {
        int dx = position - child.getGraphicsAlgorithm().getX();
        MoveShapeContext moveShapeContext = new MoveShapeContext(child);
        moveShapeContext.setY(child.getGraphicsAlgorithm().getY());
        moveShapeContext.setX(position);
        moveShapeContext.setDeltaY(0);
        moveShapeContext.setDeltaX(dx);
        moveShapeContext.setSourceContainer(child.getContainer());
        moveShapeContext.setTargetContainer(child.getContainer());
        MoveInternalPortFeature moveFeature = new MoveInternalPortFeature(fp);
        if (moveFeature.canMoveShape(moveShapeContext)) {
          moveFeature.moveShape(moveShapeContext);
          moveFeature.postMoveShape(moveShapeContext);
        }
        position += stepPorts;
      }
    }
  }

  public static PictogramElement getPictogramElementOfInternalPort(
      Diagram diagram, InternalPort ip) {
    PictogramElement ret = null;
    for (PictogramElement pe : Graphiti.getLinkService()
        .getPictogramElements(diagram, ip))
      if (isInternalPortLabel(pe)) {
        ret = pe;
        break;
      }
    return ret;
  }

  public static Shape getPictogramElementOfInternalPort(ContainerShape parent,
      InternalPort internalPort) {
    for (Shape s : parent.getChildren())
      if (isInternalPortLabel(s)
          && s.getLink().getBusinessObjects().get(0) == internalPort)
        return s;
    return null;
  }

  public static Position getPosition(ContainerShape parent,
      InternalPort internalPort) {
    for (Shape s : parent.getChildren()) {
      if (isInternalPortLabel(s)
          && s.getLink().getBusinessObjects().get(0) == internalPort) {
        int x = s.getGraphicsAlgorithm().getX();
        int y = s.getGraphicsAlgorithm().getY();
        int W = parent.getGraphicsAlgorithm().getWidth();
        int H = parent.getGraphicsAlgorithm().getHeight();
        int w = s.getGraphicsAlgorithm().getWidth();
        int h = s.getGraphicsAlgorithm().getHeight();

        if (x == 0)
          return Position.LEFT;
        if (x == W - w)
          return Position.RIGHT;
        if (y == 0)
          return Position.TOP;
        if (y == H - h)
          return Position.BOTTOM;
      }
    }
    return null;
  }

  public static boolean isInternalPortLabel(PictogramElement pe) {
    return Graphiti.getPeService().getPropertyValue(pe,
        Constants.INTERNAL_PORT) != null;
  }
}
