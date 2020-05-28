package org.ruminaq.gui.features.update;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.context.impl.DeleteContext;
import org.eclipse.graphiti.features.context.impl.MultiDeleteInfo;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IPeCreateService;
import org.ruminaq.consts.Constants;
import org.ruminaq.gui.TasksUtil;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.add.AbstractAddFeatureFilter;
import org.ruminaq.gui.features.add.AbstractAddTaskFeature;
import org.ruminaq.gui.features.update.UpdateTaskFeature.Filter;
import org.ruminaq.gui.model.Position;
import org.ruminaq.gui.model.diagram.InternalInputPortShape;
import org.ruminaq.gui.model.diagram.InternalOutputPortShape;
import org.ruminaq.gui.model.diagram.TaskShape;
import org.ruminaq.model.DataTypeManager;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.model.ruminaq.DataType;
import org.ruminaq.model.ruminaq.InternalInputPort;
import org.ruminaq.model.ruminaq.InternalOutputPort;
import org.ruminaq.model.ruminaq.InternalPort;
import org.ruminaq.model.ruminaq.ModelUtil;
import org.ruminaq.model.ruminaq.NGroup;
import org.ruminaq.model.ruminaq.PortInfo;
import org.ruminaq.model.ruminaq.RuminaqFactory;
import org.ruminaq.model.ruminaq.Task;

/**
 * 
 * @author Marek Jagielski
 */
@FeatureFilter(Filter.class)
public class UpdateTaskFeature extends UpdateBaseElementFeature {

  public static class Filter extends AbstractAddFeatureFilter {
    @Override
    public Class<? extends BaseElement> forBusinessObject() {
      return Task.class;
    }
  }

  public UpdateTaskFeature(IFeatureProvider fp) {
    super(fp);
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
    Optional<TaskShape> taskShape = Optional.of(context)
        .map(AbstractUpdateFeatureFilter.getPictogramElement)
        .filter(TaskShape.class::isInstance).map(TaskShape.class::cast);
    Optional<Task> task = taskShape.map(TaskShape::getModelObject)
        .filter(Task.class::isInstance).map(Task.class::cast);
    if (taskShape.isPresent() && task.isPresent()) {
      return updateInputPortNeeded(taskShape.get(), task.get())
          || updateOutputPortNeeded(taskShape.get(), task.get());
    }
    return false;
  }

  private boolean updateInputPortNeeded(TaskShape taskShape, Task task) {
    List<InternalInputPort> fromModel = task.getInputPort();
    List<InternalInputPort> fromShape = taskShape.getInputPort().stream()
        .map(InternalInputPortShape::getModelObject)
        .filter(InternalInputPort.class::isInstance)
        .map(InternalInputPort.class::cast).collect(Collectors.toList());
    return !(fromModel.stream().allMatch(fromShape::contains)
        && fromShape.stream().allMatch(fromModel::contains));
  }

  private boolean updateOutputPortNeeded(TaskShape taskShape, Task task) {
    List<InternalOutputPort> fromModel = task.getOutputPort();
    List<InternalOutputPort> fromShape = taskShape.getOutputPort().stream()
        .map(InternalOutputPortShape::getModelObject)
        .filter(InternalOutputPort.class::isInstance)
        .map(InternalOutputPort.class::cast).collect(Collectors.toList());
    return !(fromModel.stream().allMatch(fromShape::contains)
        && fromShape.stream().allMatch(fromModel::contains));
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
    Optional<TaskShape> taskShape = Optional.of(context)
        .map(AbstractUpdateFeatureFilter.getPictogramElement)
        .filter(TaskShape.class::isInstance).map(TaskShape.class::cast);
    Optional<Task> task = taskShape.map(TaskShape::getModelObject)
        .filter(Task.class::isInstance).map(Task.class::cast);
    if (taskShape.isPresent() && task.isPresent()) {
      boolean updated = false;
      if (updateInputPortNeeded(taskShape.get(), task.get())) {
        updated = updated | updateInputPort(context);
      }
      if (updateInputPortNeeded(taskShape.get(), task.get())) {
        updated = updated | updateOutputPort(context);
      }
      return updated;
    }
    return false;
  }

  private boolean updateInputPort(IUpdateContext context) {
    return false;
  }

  private boolean updateOutputPort(IUpdateContext context) {
    return false;
  }

  protected void addPort(Task task, ContainerShape parent, PortsDescr pd) {
    if (pd == null)
      return;
    try {
      Field f = pd.getClass().getField(pd.toString());
      PortInfo in = f.getAnnotation(PortInfo.class);
      if (in != null) {
        if (in.n() == 1) {
//          addInputPort(task, parent, in.id(), in.label(), in.type(),
//              in.asynchronous(), in.group(), in.hold(), in.queue(), in.pos());
        } else {
          String id = null;
          int k = -1;
          loop: while (id == null) {
            k++;
            if (TasksUtil.getAllMutlipleInternalInputPorts(task, in.id())
                .size() == 0) {
              id = in.id() + " " + 0;
              break;
            }
            for (InternalInputPort ip : task.getInputPort())
              if (ip.getId().equals(in.id() + " " + k))
                continue loop;
            id = in.id() + " " + k;
          }
          int grp = in.group();
          if (in.ngroup().equals(NGroup.DIFFERENT)) {
            if (grp != -1) {
              int j = grp;
              boolean free = true;
              do {
                free = true;
                for (InternalInputPort iip : task.getInputPort())
                  if (iip.getGroup() == j)
                    free = false;
                j++;
              } while (!free);
              grp = j - 1;
            }
          }
//          addInputPort(task, parent, id, in.label(), in.type(),
//              in.asynchronous(), grp, in.hold(), in.queue(), in.pos());
        }
//        redistributePorts(parent, in.pos());
      }
      PortInfo out = f.getAnnotation(PortInfo.class);
      if (out != null) {
        if (out.n() == 1) {
//          addOutputPort(task, parent, out.id(), out.label(), out.type(),
//              out.pos());
        } else {
          String id = null;
          int k = -1;
          loop: while (id == null) {
            k++;
            if (TasksUtil.getAllMutlipleInternalOutputPorts(task, out.id())
                .size() == 0) {
              id = out.id() + " " + 0;
              break;
            }
            for (InternalOutputPort op : task.getOutputPort())
              if (op.getId().equals(out.id() + " " + k))
                continue loop;
            id = out.id() + " " + k;
          }
//          addOutputPort(task, parent, id, out.label(), out.type(), out.pos());
        }
//        redistributePorts(parent, out.pos());
      }
    } catch (NoSuchFieldException | SecurityException e) {
    }
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

    int x, y;
    switch (pos) {
      default:
      case LEFT:
        x = 0;
        y = 0;
        break;
      case TOP:
        x = 0;
        y = 0;
        break;
      case RIGHT:
//        x = parent.getGraphicsAlgorithm().getWidth() - AbstractAddTaskFeature.PORT_SIZE;
//        y = 0;
        break;
      case BOTTOM:
//        x = 0;
//        y = parent.getGraphicsAlgorithm().getHeight()
//            - AbstractAddTaskFeature.PORT_SIZE;
        break;
    }
//    ContainerShape containerShape = AbstractAddTaskFeature
//        .createPictogramForInternalPort(parent, x, y, getDiagram());
//    peCreateService.createChopboxAnchor(containerShape);
//    ContainerShape portLabelShape = AbstractAddTaskFeature.addInternalPortLabel(
//        getDiagram(), parent, in.getId(), AbstractAddTaskFeature.PORT_SIZE,
//        AbstractAddTaskFeature.PORT_SIZE, x, y, InternalPortLabelPosition.RIGHT);
//
//    link(containerShape, new Object[] { in, portLabelShape });
//    link(portLabelShape, new Object[] { in, containerShape });

//    if (!showLabel)
//      portLabelShape.setVisible(false);

    redistributePorts(parent, pos);
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

//    int lineWidth = AbstractAddTaskFeature.OUTPUT_PORT_WIDTH;
    int x, y;
    switch (pos) {
      default:
      case LEFT:
        x = 0;
        y = 0;
        break;
      case TOP:
//        x = parent.getGraphicsAlgorithm().getWidth() - AbstractAddTaskFeature.PORT_SIZE;
//        y = 0;
        break;
      case RIGHT:
//        x = parent.getGraphicsAlgorithm().getWidth() - AbstractAddTaskFeature.PORT_SIZE;
//        y = 0;
        break;
      case BOTTOM:
//        x = 0;
//        y = parent.getGraphicsAlgorithm().getHeight()
//            - AbstractAddTaskFeature.PORT_SIZE;
        break;
    }
//    ContainerShape containerShape = AbstractAddTaskFeature
//        .createPictogramForInternalPort(parent, x, y, AbstractAddTaskFeature.PORT_SIZE,
//            AbstractAddTaskFeature.PORT_SIZE, getDiagram(), lineWidth, LineStyle.SOLID);
//    peCreateService.createChopboxAnchor(containerShape);
//    ContainerShape portLabelShape = AbstractAddTaskFeature.addInternalPortLabel(
//        getDiagram(), parent, out.getId(), AbstractAddTaskFeature.PORT_SIZE,
//        AbstractAddTaskFeature.PORT_SIZE, x, y, InternalPortLabelPosition.LEFT);
//
//    link(containerShape, new Object[] { out, portLabelShape });
//    link(portLabelShape, new Object[] { out, containerShape });

//    if (!showLabel)
//      portLabelShape.setVisible(false);

    redistributePorts(parent, pos);
  }

  protected void removePortShape(Task task, ContainerShape parent,
      InternalPort internalPort) {
    Shape toRemove = AbstractAddTaskFeature
        .getPictogramElementOfInternalPort(parent, internalPort);

    Position pos = AbstractAddTaskFeature.getPosition(parent, internalPort);

    Graphiti.getPeService().setPropertyValue(toRemove, Constants.CAN_DELETE,
        "true");
    DeleteContext deleteContext = new DeleteContext(toRemove);
    deleteContext.setMultiDeleteInfo(new MultiDeleteInfo(false, false, 1));
    IDeleteFeature deleteFeature = getFeatureProvider()
        .getDeleteFeature(deleteContext);
    if (deleteFeature.canDelete(deleteContext))
      deleteFeature.delete(deleteContext);

    if (pos != null)
      redistributePorts(parent, pos);
  }

  protected void removePort(Task task, ContainerShape shape, PortsDescr pd) {
    if (pd == null)
      return;
    try {
      Field f = pd.getClass().getField(pd.toString());
      PortInfo in = f.getAnnotation(PortInfo.class);
      if (in != null) {
        if (in.n() == 1)
          removePortShape(task, shape,
              TasksUtil.getInternalPort(task, in.id()));
        else {
          List<InternalInputPort> ports = TasksUtil
              .getAllMutlipleInternalInputPorts(task, in.id());
          removePortShape(task, shape, TasksUtil.getInternalPort(task,
              in.id() + " " + (ports.size() - 1)));
        }
      }
      PortInfo out = f.getAnnotation(PortInfo.class);
      if (out != null) {
        if (out.n() == 1)
          removePortShape(task, shape,
              TasksUtil.getInternalPort(task, out.id()));
        else {
          List<InternalOutputPort> ports = TasksUtil
              .getAllMutlipleInternalOutputPorts(task, out.id());
          removePortShape(task, shape, TasksUtil.getInternalPort(task,
              out.id() + " " + (ports.size() - 1)));
        }
      }
    } catch (NoSuchFieldException | SecurityException e) {
    }
  }

  private void redistributePorts(ContainerShape parent, Position pos) {
    switch (pos) {
      case LEFT:
        AbstractAddTaskFeature.distributePortsOnLeft(parent,
            getFeatureProvider());
        break;
      case TOP:
        AbstractAddTaskFeature.distributePortsOnTop(parent,
            getFeatureProvider());
        break;
      case RIGHT:
        AbstractAddTaskFeature.distributePortsOnRight(parent,
            getFeatureProvider());
        break;
      case BOTTOM:
        AbstractAddTaskFeature.distributePortsOnBottom(parent,
            getFeatureProvider());
        break;
      default:
        break;
    }
  }
}
