/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.update;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.context.impl.DeleteContext;
import org.eclipse.graphiti.features.context.impl.MultiDeleteInfo;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.features.impl.Reason;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.update.UpdateTaskFeature.Filter;
import org.ruminaq.gui.model.GuiUtil;
import org.ruminaq.gui.model.PortDiagram;
import org.ruminaq.gui.model.Position;
import org.ruminaq.gui.model.diagram.DiagramFactory;
import org.ruminaq.gui.model.diagram.InternalInputPortShape;
import org.ruminaq.gui.model.diagram.InternalOutputPortShape;
import org.ruminaq.gui.model.diagram.InternalPortShape;
import org.ruminaq.gui.model.diagram.TaskShape;
import org.ruminaq.gui.model.diagram.impl.TasksUtil;
import org.ruminaq.gui.model.diagram.impl.task.InternalPortShapeGA;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.model.ruminaq.DataType;
import org.ruminaq.model.ruminaq.DataTypeManager;
import org.ruminaq.model.ruminaq.InternalInputPort;
import org.ruminaq.model.ruminaq.InternalOutputPort;
import org.ruminaq.model.ruminaq.InternalPort;
import org.ruminaq.model.ruminaq.ModelUtil;
import org.ruminaq.model.ruminaq.NoPorts;
import org.ruminaq.model.ruminaq.PortInfo;
import org.ruminaq.model.ruminaq.PortType;
import org.ruminaq.model.ruminaq.PortsDescr;
import org.ruminaq.model.ruminaq.RuminaqFactory;
import org.ruminaq.model.ruminaq.Task;

/**
 * IUpdateFeature for Task.
 *
 * @author Marek Jagielski
 */
@FeatureFilter(Filter.class)
public class UpdateTaskFeature extends UpdateBaseElementFeature {

  public static class Filter extends AbstractUpdateFeatureFilter {
    @Override
    public Class<? extends BaseElement> forBusinessObject() {
      return Task.class;
    }
  }

  public UpdateTaskFeature(IFeatureProvider fp) {
    super(fp);
  }

  protected static Optional<TaskShape> shapeFromContext(
      IUpdateContext context) {
    return Optional.of(context)
        .map(AbstractUpdateFeatureFilter.getPictogramElement)
        .filter(TaskShape.class::isInstance).map(TaskShape.class::cast);
  }

  protected static Optional<Task> modelFromContext(IUpdateContext context) {
    return shapeFromContext(context).map(TaskShape::getModelObject)
        .filter(Task.class::isInstance).map(Task.class::cast);
  }

  protected static <T> Optional<T> modelFromContext(IUpdateContext context,
      Class<T> type) {
    return modelFromContext(context).filter(type::isInstance).map(type::cast);
  }

  private static <T extends InternalPortShape, K extends InternalPort> List<K> internalPortFrom(
      Collection<T> portShapes, Class<K> type) {
    return portShapes.stream().map(InternalPortShape::getModelObject)
        .filter(type::isInstance).map(type::cast).collect(Collectors.toList());
  }

  /**
   * Check if need to update ports shapes basing on ports models.
   *
   * @param <T>        type of ports shape
   * @param <K>        type of ports model
   * @param portShapes collection of ports shapes
   * @param fromModel  collection of ports models
   * @param type       which type of port is being checked
   * @return update needed
   */
  private static <T extends InternalPortShape, K extends InternalPort> boolean updatePortNeeded(
      Collection<T> portShapes, Collection<K> fromModel, Class<K> type) {
    List<K> fromShape = internalPortFrom(portShapes, type);
    return !(fromModel.stream().allMatch(fromShape::contains)
        && fromShape.stream().allMatch(fromModel::contains));
  }

  /**
   * Check if need to update ports shapes.
   *
   * @param context IUpdateContext
   * @return update needed
   */
  private static boolean updatePortNeeded(IUpdateContext context) {
    Optional<TaskShape> taskShape = shapeFromContext(context);
    Optional<Task> task = modelFromContext(context);
    if (taskShape.isPresent() && task.isPresent()) {
      return updatePortNeeded(taskShape.get().getInternalPort(),
          task.get().getInputPort(), InternalInputPort.class)
          || updatePortNeeded(taskShape.get().getInternalPort(),
              task.get().getOutputPort(), InternalOutputPort.class);
    }
    return false;
  }

  private static Optional<PortDiagram> getPortDiagram(String id,
      Class<? extends PortsDescr> pd, PortType portType) {
    return Optional.of(pd).map(Class::getFields).map(Stream::of)
        .orElseGet(Stream::empty)
        .filter(f -> Optional.of(f).map(fp -> fp.getAnnotation(PortInfo.class))
            .filter(Objects::nonNull).filter(pi -> portType == pi.portType())
            .filter((PortInfo pi) -> {
              if (pi.n() == 1) {
                return id.equals(pi.id());
              } else {
                return TasksUtil.isMultiplePortId(id, pi.id());
              }
            }).isPresent())
        .map(f -> f.getAnnotation(PortDiagram.class)).filter(Objects::nonNull)
        .findFirst();
  }

  private static int yOfPostion(TaskShape taskShape, Position position) {
    if (position == Position.LEFT || position == Position.RIGHT) {
      return 1;
    } else if (position == Position.TOP) {
      return 0;
    } else {
      return taskShape.getHeight() - InternalPortShapeGA.SIZE;
    }
  }

  private static int xOfPostion(TaskShape taskShape, Position position) {
    if (position == Position.LEFT) {
      return 0;
    } else if (position == Position.TOP || position == Position.BOTTOM) {
      return 1;
    } else {
      return taskShape.getWidth() - InternalPortShapeGA.SIZE;
    }
  }

  @Override
  public IReason updateNeeded(IUpdateContext context) {
    if (updatePortNeeded(context)) {
      return Reason.createTrueReason();
    } else {
      return Reason.createFalseReason();
    }
  }

  @Override
  public boolean update(IUpdateContext context) {
    if (updatePortNeeded(context)) {
      updatePort(context);
    }

    return true;
  }

  private void updatePort(IUpdateContext context) {
    shapeFromContext(context).ifPresent(taskShape -> modelFromContext(context)
        .filter(task -> updatePortNeeded(taskShape.getInternalPort(),
            task.getInputPort(), InternalInputPort.class))
        .ifPresent(task -> updateInputPort(taskShape, task)));
    shapeFromContext(context).ifPresent(taskShape -> modelFromContext(context)
        .filter(task -> updatePortNeeded(taskShape.getInternalPort(),
            task.getOutputPort(), InternalOutputPort.class))
        .ifPresent(task -> updateOutputPort(taskShape, task)));
    shapeFromContext(context).map(TaskShape::getInternalPort).map(List::stream)
        .orElseGet(Stream::empty).map(UpdateContext::new)
        .filter(ctx -> getFeatureProvider().canUpdate(ctx).toBoolean())
        .forEach(getFeatureProvider()::updateIfPossibleAndNeeded);
  }

  private void updateInputPort(TaskShape taskShape, Task task) {
    List<InternalInputPort> fromModel = task.getInputPort();
    List<InternalInputPort> fromShape = internalPortFrom(
        taskShape.getInternalPort(), InternalInputPort.class);
    fromModel.stream().filter(Predicate.not(fromShape::contains))
        .collect(Collectors.toList()).stream()
        .forEach(p -> addInputPort(p, taskShape));
    fromShape.stream().filter(Predicate.not(fromModel::contains))
        .collect(Collectors.toList()).stream()
        .forEach(p -> removePort(p, taskShape));
  }

  private void updateOutputPort(TaskShape taskShape, Task task) {
    List<InternalOutputPort> fromModel = task.getOutputPort();
    List<InternalOutputPort> fromShape = internalPortFrom(
        taskShape.getInternalPort(), InternalOutputPort.class);
    fromModel.stream().filter(Predicate.not(fromShape::contains))
        .collect(Collectors.toList()).stream()
        .forEach(p -> addOutputPort(p, taskShape));
    fromShape.stream().filter(Predicate.not(fromModel::contains))
        .collect(Collectors.toList()).stream()
        .forEach(p -> removePort(p, taskShape));
  }

  protected Class<? extends PortsDescr> getPortsDescription() {
    return NoPorts.class;
  }

  protected void createInputPort(Task task, PortsDescr pd) {
    Stream.of(getPortsDescription().getDeclaredFields())
        .filter(f -> f.getName().equals(pd.name())).findFirst()
        .ifPresent(f -> TasksUtil.createInputPort(task, f));
  }

  protected void createInputPort(Task task, String name,
      Collection<Class<? extends DataType>> datatypes, boolean asyn, int grp,
      boolean hold, String queue) {
    InternalInputPort in = RuminaqFactory.eINSTANCE.createInternalInputPort();
    in.setId(name);
    datatypes.stream().map(ModelUtil::getName)
        .map(DataTypeManager.INSTANCE::getDataTypeFromName)
        .filter(Objects::nonNull).forEach(in.getDataType()::add);
    in.setAsynchronous(asyn);
    in.setGroup(grp);
    in.setDefaultHoldLast(hold);
    in.setHoldLast(hold);
    in.setDefaultQueueSize(queue);
    task.getInputPort().add(in);
  }

  protected void deleteInputPort(Task task, String id) {
    task.getInputPort().remove(task.getInputPort(id));
  }

  protected void deleteInputPort(InternalInputPort iip) {
    deleteInputPort(iip.getTask(), iip.getId());
  }

  protected void createOutputPort(Task task, PortsDescr pd) {
    Stream.of(getPortsDescription().getDeclaredFields())
        .filter(f -> f.getName().equals(pd.name())).findFirst()
        .ifPresent(f -> TasksUtil.createOutputPort(task, f));
  }

  protected void createOutputPort(Task task, String name,
      Collection<Class<? extends DataType>> datatypes) {
    InternalOutputPort out = RuminaqFactory.eINSTANCE
        .createInternalOutputPort();
    out.setId(name);
    datatypes.stream().map(ModelUtil::getName)
        .map(DataTypeManager.INSTANCE::getDataTypeFromName)
        .filter(Objects::nonNull).forEach(out.getDataType()::add);
    task.getOutputPort().add(out);
  }

  protected void deleteOutputPort(Task task, String id) {
    task.getOutputPort().remove(task.getOutputPort(id));
  }

  protected void deleteOutputPort(InternalOutputPort iop) {
    deleteOutputPort(iop.getTask(), iop.getId());
  }

  private void addInputPort(InternalInputPort p, TaskShape taskShape) {
    Class<? extends PortsDescr> pd = getPortsDescription();

    Optional<PortDiagram> portDiagram = getPortDiagram(p.getId(), pd,
        PortType.IN);
    InternalInputPortShape portShape = DiagramFactory.eINSTANCE
        .createInternalInputPortShape();
    taskShape.getInternalPort().add(portShape);
    portShape.setModelObject(p);
    portShape
        .setShowLabel(portDiagram.map(PortDiagram::label).orElse(Boolean.TRUE));
    Position position = portDiagram.map(PortDiagram::pos).orElse(Position.LEFT);
    portShape.setX(xOfPostion(taskShape, position));
    portShape.setY(yOfPostion(taskShape, position));
    redistributePorts(taskShape, position);
  }

  private void addOutputPort(InternalOutputPort p, TaskShape taskShape) {
    Class<? extends PortsDescr> pd = getPortsDescription();

    Optional<PortDiagram> portDiagram = getPortDiagram(p.getId(), pd,
        PortType.OUT);
    InternalOutputPortShape portShape = DiagramFactory.eINSTANCE
        .createInternalOutputPortShape();
    taskShape.getInternalPort().add(portShape);
    portShape.setModelObject(p);
    portShape
        .setShowLabel(portDiagram.map(PortDiagram::label).orElse(Boolean.TRUE));
    Position position = portDiagram.map(PortDiagram::pos)
        .orElse(Position.RIGHT);
    portShape.setX(xOfPostion(taskShape, position));
    portShape.setY(yOfPostion(taskShape, position));
    redistributePorts(taskShape, position);
  }

  private void removePort(InternalPort p, TaskShape taskShape) {
    Optional<InternalPortShape> ips = taskShape.getInternalPort().stream()
        .filter(ip -> ip.getModelObject().equals(p)).findAny();
    if (ips.isPresent()) {
      Optional<Position> optPosition = Optional
          .ofNullable(GuiUtil.getPosition(taskShape, ips.get()));
      DeleteContext deleteContext = new DeleteContext(ips.get());
      deleteContext.setMultiDeleteInfo(new MultiDeleteInfo(false, false, 1));
      IDeleteFeature deleteFeature = getFeatureProvider()
          .getDeleteFeature(deleteContext);
      if (deleteFeature.canDelete(deleteContext)) {
        deleteFeature.delete(deleteContext);
      }
      optPosition.ifPresent(pos -> redistributePorts(taskShape, pos));
    }
  }

  private static void redistributePorts(TaskShape taskShape, Position pos) {
    Supplier<Stream<InternalPortShape>> ports = () -> taskShape
        .getInternalPort().stream();
    switch (pos) {
      case LEFT, RIGHT:
        distributePortsVertically(taskShape,
            ports.get().filter(p -> pos == GuiUtil.getPosition(taskShape, p))
                .collect(Collectors.toList()));
        break;
      case TOP, BOTTOM:
        distributePortsHorizontally(taskShape,
            ports.get().filter(p -> pos == GuiUtil.getPosition(taskShape, p))
                .collect(Collectors.toList()));
        break;
      default:
        break;
    }
  }

  private static void distributePortsVertically(TaskShape taskShape,
      List<InternalPortShape> ports) {
    ports.sort((p1, p2) -> Integer.compare(p1.getY(), p2.getY()));
    int stepPorts = taskShape.getHeight() / ports.size();
    int startPosition = (stepPorts >> 1) - (InternalPortShapeGA.SIZE >> 1);
    IntStream.range(0, ports.size())
        .forEach(i -> ports.get(i).setY(startPosition + i * stepPorts));
  }

  private static void distributePortsHorizontally(TaskShape taskShape,
      List<InternalPortShape> ports) {
    ports.sort((p1, p2) -> Integer.compare(p1.getX(), p2.getX()));
    int stepPorts = taskShape.getWidth() / ports.size();
    int startPosition = (stepPorts >> 1) - (InternalPortShapeGA.SIZE >> 1);
    IntStream.range(0, ports.size())
        .forEach(i -> ports.get(i).setX(startPosition + i * stepPorts));
  }
}
