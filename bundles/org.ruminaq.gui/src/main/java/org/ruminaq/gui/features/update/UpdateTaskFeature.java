/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.update;

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
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IPeCreateService;
import org.ruminaq.gui.TasksUtil;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.update.UpdateTaskFeature.Filter;
import org.ruminaq.gui.model.PortDiagram;
import org.ruminaq.gui.model.Position;
import org.ruminaq.gui.model.diagram.DiagramFactory;
import org.ruminaq.gui.model.diagram.InternalInputPortShape;
import org.ruminaq.gui.model.diagram.InternalOutputPortShape;
import org.ruminaq.gui.model.diagram.InternalPortShape;
import org.ruminaq.gui.model.diagram.TaskShape;
import org.ruminaq.gui.model.diagram.impl.task.InternalPortShapeGA;
import org.ruminaq.model.DataTypeManager;
import org.ruminaq.model.desc.NoPorts;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.model.ruminaq.DataType;
import org.ruminaq.model.ruminaq.InternalInputPort;
import org.ruminaq.model.ruminaq.InternalOutputPort;
import org.ruminaq.model.ruminaq.InternalPort;
import org.ruminaq.model.ruminaq.ModelUtil;
import org.ruminaq.model.ruminaq.PortInfo;
import org.ruminaq.model.ruminaq.PortType;
import org.ruminaq.model.ruminaq.RuminaqFactory;
import org.ruminaq.model.ruminaq.Task;

/**
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

  protected static Optional<TaskShape> shapeFromContext(IUpdateContext context) {
    return Optional.of(context)
        .map(AbstractUpdateFeatureFilter.getPictogramElement)
        .filter(TaskShape.class::isInstance).map(TaskShape.class::cast);
  }

  protected static Optional<Task> modelFromShape(Optional<TaskShape> taskShape) {
    return taskShape.map(TaskShape::getModelObject)
        .filter(Task.class::isInstance).map(Task.class::cast);
  }

  private static <T extends InternalPortShape, K extends InternalPort> List<K> internalPortFromShape(
      List<T> portShapes, Class<K> type) {
    return portShapes.stream().map(InternalPortShape::getModelObject)
        .filter(type::isInstance).map(type::cast).collect(Collectors.toList());
  }

  private static <T extends InternalPortShape, K extends InternalPort> boolean updateInternalPortNeeded(
      List<T> portShapes, List<K> fromModel, Class<K> type) {
    List<K> fromShape = internalPortFromShape(portShapes, type);
    return !(fromModel.stream().allMatch(fromShape::contains)
        && portShapes.stream().map(InternalPortShape::getModelObject)
            .allMatch(Objects::nonNull));
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
        .map(f -> f.getAnnotation(PortDiagram.class)).findFirst();
  }

  private static int yOfPostion(TaskShape taskShape, Position position) {
    switch (position) {
      default:
      case LEFT:
        return 0;
      case TOP:
        return 0;
      case RIGHT:
        return 0;
      case BOTTOM:
        return taskShape.getHeight() - InternalPortShapeGA.SIZE;
    }
  }

  private static int xOfPostion(TaskShape taskShape, Position position) {
    switch (position) {
      default:
      case LEFT:
        return 0;
      case TOP:
        return 0;
      case RIGHT:
        return taskShape.getWidth() - InternalPortShapeGA.SIZE;
      case BOTTOM:
        return 0;
    }
  }

  public static Position getPosition(TaskShape taskShape, InternalPortShape p) {
    int x = p.getX();
    int y = p.getY();
    int W = taskShape.getWidth();
    int H = taskShape.getHeight();
    int w = p.getWidth();
    int h = p.getHeight();

    if (x == 0) {
      return Position.LEFT;
    } else if (x == W - w) {
      return Position.RIGHT;
    } else if (y == 0) {
      return Position.TOP;
    } else if (y == H - h) {
      return Position.BOTTOM;
    }

    return null;
  }

  @Override
  public IReason updateNeeded(IUpdateContext context) {
    if (super.updateNeeded(context).toBoolean() || updatePortNeeded(context)) {
      return Reason.createTrueReason();
    } else {
      return Reason.createFalseReason();
    }
  }

  private boolean updatePortNeeded(IUpdateContext context) {
    Optional<TaskShape> taskShape = shapeFromContext(context);
    Optional<Task> task = modelFromShape(taskShape);
    if (taskShape.isPresent() && task.isPresent()) {
      return updateInternalPortNeeded(taskShape.get().getInputPort(),
          task.get().getInputPort(), InternalInputPort.class)
          || updateInternalPortNeeded(taskShape.get().getOutputPort(),
              task.get().getOutputPort(), InternalOutputPort.class);
    }
    return false;
  }

  @Override
  public boolean update(IUpdateContext context) {
    boolean updated = false;

    if (super.updateNeeded(context).toBoolean()) {
      updated = updated | super.update(context);
    }

    if (updatePortNeeded(context)) {
      updated = updated | updatePort(context);
    }

    return updated;
  }

  private boolean updatePort(IUpdateContext context) {
    Optional<TaskShape> taskShape = shapeFromContext(context);
    Optional<Task> task = modelFromShape(taskShape);
    if (taskShape.isPresent() && task.isPresent()) {
      boolean updated = false;
      if (updateInternalPortNeeded(taskShape.get().getInputPort(),
          task.get().getInputPort(), InternalInputPort.class)) {
        updated = updated | updateInputPort(taskShape.get(), task.get());
      }
      if (updateInternalPortNeeded(taskShape.get().getOutputPort(),
          task.get().getOutputPort(), InternalOutputPort.class)) {
        updated = updated | updateOutputPort(taskShape.get(), task.get());
      }
      return updated;
    }
    return false;
  }

  private boolean updateInputPort(TaskShape taskShape, Task task) {
    List<InternalInputPort> fromModel = task.getInputPort();
    List<InternalInputPort> fromShape = internalPortFromShape(
        taskShape.getInputPort(), InternalInputPort.class);
    List<InternalInputPort> portsToAdd = fromModel.stream()
        .filter(Predicate.not(fromShape::contains))
        .collect(Collectors.toList());
    List<InternalInputPortShape> portsToRemove = taskShape.getInputPort()
        .stream()
        .filter(ips -> Optional.ofNullable(ips.getModelObject()).isEmpty())
        .collect(Collectors.toList());
    portsToAdd.stream().forEach(p -> addInputPort(p, fromModel, taskShape));
    portsToRemove.stream().forEach(p -> removePort(p, taskShape));
    return !portsToAdd.isEmpty() || !portsToRemove.isEmpty();
  }

  private boolean updateOutputPort(TaskShape taskShape, Task task) {
    List<InternalOutputPort> fromModel = task.getOutputPort();
    List<InternalOutputPort> fromShape = internalPortFromShape(
        taskShape.getInputPort(), InternalOutputPort.class);
    List<InternalOutputPort> portsToAdd = fromModel.stream()
        .filter(Predicate.not(fromShape::contains))
        .collect(Collectors.toList());
    List<InternalOutputPortShape> portsToRemove = taskShape.getOutputPort()
        .stream()
        .filter(ips -> Optional.ofNullable(ips.getModelObject()).isEmpty())
        .collect(Collectors.toList());
    portsToAdd.stream().forEach(p -> addOutputPort(p, fromModel, taskShape));
    portsToRemove.stream().forEach(p -> removePort(p, taskShape));
    return !portsToAdd.isEmpty() || !portsToRemove.isEmpty();
  }

  protected Class<? extends PortsDescr> getPortsDescription() {
    return NoPorts.class;
  }

  private void addInputPort(InternalInputPort p,
      List<InternalInputPort> fromModel, TaskShape taskShape) {
    Class<? extends PortsDescr> pd = getPortsDescription();

    Optional<PortDiagram> portDiagram = getPortDiagram(p.getId(), pd,
        PortType.IN);
    Position position = portDiagram.map(PortDiagram::pos).orElse(Position.LEFT);
    InternalInputPortShape portShape = DiagramFactory.eINSTANCE
        .createInternalInputPortShape();
    portShape.setContainer(taskShape);
    taskShape.getInputPort().add(portShape);
    portShape.setModelObject(p);
    portShape.setX(xOfPostion(taskShape, position));
    portShape.setY(yOfPostion(taskShape, position));

    redistributePorts(taskShape, position);

//          String id = null;
//          int k = -1;
//          loop: while (id == null) {
//            k++;
//            if (TasksUtil.SgetAllMutlipleInternalInputPorts(task, in.id())
//                .size() == 0) {
//              id = in.id() + " " + 0;
//              break;
//            }
//            for (InternalInputPort ip : task.getInputPort())
//              if (ip.getId().equals(in.id() + " " + k))
//                continue loop;
//            id = in.id() + " " + k;
//          }
//          int grp = in.group();
//          if (in.ngroup().equals(NGroup.DIFFERENT)) {
//            if (grp != -1) {
//              int j = grp;
//              boolean free = true;
//              do {
//                free = true;
//                for (InternalInputPort iip : task.getInputPort())
//                  if (iip.getGroup() == j)
//                    free = false;
//                j++;
//              } while (!free);
//              grp = j - 1;
//            }
//          }
//          addInputPort(task, parent, id, in.label(), in.type(),
//              in.asynchronous(), grp, in.hold(), in.queue(), in.pos());
//        }
//        redistributePorts(parent, in.pos());
//      }
//          String id = null;
//          int k = -1;
//          loop: while (id == null) {
//            k++;
//            if (TasksUtil.getAllMutlipleInternalOutputPorts(task, out.id())
//                .size() == 0) {
//              id = out.id() + " " + 0;
//              break;
//            }
//            for (InternalOutputPort op : task.getOutputPort())
//              if (op.getId().equals(out.id() + " " + k))
//                continue loop;
//            id = out.id() + " " + k;
//          }
//          addOutputPort(task, parent, id, out.label(), out.type(), out.pos());
//        }
//        redistributePorts(parent, out.pos());
//      }
  }

  private void addOutputPort(InternalOutputPort p,
      List<InternalOutputPort> fromModel, TaskShape taskShape) {
    Class<? extends PortsDescr> pd = getPortsDescription();

    Optional<PortDiagram> portDiagram = getPortDiagram(p.getId(), pd,
        PortType.OUT);
    Position position = portDiagram.map(PortDiagram::pos).orElse(Position.RIGHT);
    InternalOutputPortShape portShape = DiagramFactory.eINSTANCE
        .createInternalOutputPortShape();
    portShape.setContainer(taskShape);
    taskShape.getOutputPort().add(portShape);
    portShape.setModelObject(p);
    portShape.setX(xOfPostion(taskShape, position));
    portShape.setY(yOfPostion(taskShape, position));
    redistributePorts(taskShape, position);
  }

  protected void addInputPort(Task task, ContainerShape parent, String name,
      boolean showLabel, Class<? extends DataType>[] datatypes, boolean asyn,
      int grp, boolean hold, String queue, Position pos) {
    IPeCreateService peCreateService = Graphiti.getPeCreateService();

    InternalInputPort in = (InternalInputPort) RuminaqFactory.eINSTANCE
        .createInternalInputPort();
    in.setId(name);
    for (Class<? extends DataType> cdt : datatypes) {
      DataType dt = DataTypeManager.INSTANCE
          .getDataTypeFromName(ModelUtil.getName(cdt));
      if (dt != null)
        in.getDataType().add(dt);
    }
    in.setAsynchronous(asyn);
    in.setGroup(grp);
    in.setDefaultHoldLast(hold);
    in.setHoldLast(hold);
    in.setDefaultQueueSize(queue);
    in.setQueueSize(queue);
    task.getInputPort().add(in);

//    ContainerShape portLabelShape = AbstractAddTaskFeature.addInternalPortLabel(
//        getDiagram(), parent, in.getId(), AbstractAddTaskFeature.PORT_SIZE,
//        AbstractAddTaskFeature.PORT_SIZE, x, y, InternalPortLabelPosition.RIGHT);
//
//    link(portLabelShape, new Object[] { in, containerShape });
//    if (!showLabel)
//      portLabelShape.setVisible(false);

  }

  protected void addOutputPort(Task task, ContainerShape parent, String name,
      boolean showLabel, Class<? extends DataType>[] datatypes, Position pos) {
    IPeCreateService peCreateService = Graphiti.getPeCreateService();

    InternalOutputPort out = (InternalOutputPort) RuminaqFactory.eINSTANCE
        .createInternalOutputPort();
    out.setId(name);
    for (Class<? extends DataType> cdt : datatypes) {
      DataType dt = DataTypeManager.INSTANCE
          .getDataTypeFromName(ModelUtil.getName(cdt));
      if (dt != null)
        out.getDataType().add(dt);
    }
    task.getOutputPort().add(out);

//    ContainerShape portLabelShape = AbstractAddTaskFeature.addInternalPortLabel(
//        getDiagram(), parent, out.getId(), AbstractAddTaskFeature.PORT_SIZE,
//        AbstractAddTaskFeature.PORT_SIZE, x, y, InternalPortLabelPosition.LEFT);
//
//    link(portLabelShape, new Object[] { out, containerShape });

//    if (!showLabel)
//      portLabelShape.setVisible(false);

  }

  private void removePort(InternalPortShape p, TaskShape taskShape) {
    Optional<Position> optPosition = Optional
        .ofNullable(getPosition(taskShape, p));
//Graphiti.getPeService().setPropertyValue(toRemove, Constants.CAN_DELETE,
//  "true");
    DeleteContext deleteContext = new DeleteContext(p);
    deleteContext.setMultiDeleteInfo(new MultiDeleteInfo(false, false, 1));
    IDeleteFeature deleteFeature = getFeatureProvider()
        .getDeleteFeature(deleteContext);
    if (deleteFeature.canDelete(deleteContext)) {
      deleteFeature.delete(deleteContext);
    }
    optPosition.ifPresent(pos -> redistributePorts(taskShape, pos));
  }

  private void redistributePorts(TaskShape taskShape, Position pos) {
    Supplier<Stream<InternalPortShape>> ports = () -> Stream.concat(
        taskShape.getInputPort().stream(), taskShape.getOutputPort().stream());
    switch (pos) {
      case LEFT:
      case RIGHT:
        distributePortsVertically(taskShape,
            ports.get().filter(p -> pos == getPosition(taskShape, p))
                .collect(Collectors.toList()),
            getFeatureProvider());
        break;
      case TOP:
      case BOTTOM:
        distributePortsHorizontally(taskShape,
            ports.get().filter(p -> pos == getPosition(taskShape, p))
                .collect(Collectors.toList()),
            getFeatureProvider());
        break;
      default:
        break;
    }
  }

  private static void distributePortsVertically(TaskShape taskShape,
      List<InternalPortShape> ports, IFeatureProvider fp) {
    ports.sort((p1, p2) -> Integer.compare(p1.getY(), p2.getY()));
    int stepPorts = taskShape.getHeight() / ports.size();
    int startPosition = (stepPorts >> 1) - (InternalPortShapeGA.SIZE >> 1);
    IntStream.range(0, ports.size()).forEach((int i) -> {
      ports.get(i).setY(startPosition + i * stepPorts);
    });
  }

  private static void distributePortsHorizontally(TaskShape taskShape,
      List<InternalPortShape> ports, IFeatureProvider fp) {
    ports.sort((p1, p2) -> Integer.compare(p1.getX(), p2.getX()));
    int stepPorts = taskShape.getWidth() / ports.size();
    int startPosition = (stepPorts >> 1) - (InternalPortShapeGA.SIZE >> 1);
    IntStream.range(0, ports.size()).forEach((int i) -> {
      ports.get(i).setX(startPosition + i * stepPorts);
    });
  }
}
