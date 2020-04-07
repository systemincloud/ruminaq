package org.ruminaq.gui.features.update;

import java.lang.reflect.Field;
import java.util.List;

import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.context.impl.DeleteContext;
import org.eclipse.graphiti.features.context.impl.MultiDeleteInfo;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.algorithms.styles.LineStyle;
import org.eclipse.graphiti.mm.algorithms.styles.Style;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IPeCreateService;
import org.ruminaq.consts.Constants;
import org.ruminaq.eclipse.RuminaqDiagramUtil;
import org.ruminaq.gui.TasksUtil;
import org.ruminaq.gui.features.add.AddTaskFeature;
import org.ruminaq.gui.features.add.AddTaskFeature.InternalPortLabelPosition;
import org.ruminaq.gui.features.update.UpdateBaseElementFeature;
import org.ruminaq.model.DataTypeManager;
import org.ruminaq.model.desc.IN;
import org.ruminaq.model.desc.NGroup;
import org.ruminaq.model.desc.OUT;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.model.desc.Position;
import org.ruminaq.model.ruminaq.DataType;
import org.ruminaq.model.ruminaq.InternalInputPort;
import org.ruminaq.model.ruminaq.InternalOutputPort;
import org.ruminaq.model.ruminaq.InternalPort;
import org.ruminaq.model.ruminaq.RuminaqFactory;
import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.model.util.ModelUtil;

public class UpdateTaskFeature extends UpdateBaseElementFeature {

  private boolean updateNeededChecked = false;

  private boolean superUpdateNeeded = false;
  private boolean onlyLocalUpdateNeeded = false;

  public UpdateTaskFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canUpdate(IUpdateContext context) {
    Object bo = getBusinessObjectForPictogramElement(
        context.getPictogramElement());
    return (bo instanceof Task);
  }

  @Override
  public IReason updateNeeded(IUpdateContext context) {
    this.updateNeededChecked = true;
    superUpdateNeeded = super.updateNeeded(context).toBoolean();

    ContainerShape parent = (ContainerShape) context.getPictogramElement();
    Task task = (Task) getBusinessObjectForPictogramElement(parent);

    boolean onlyLocal = task.isOnlyLocalDefault() ? task.isOnlyLocal()
        : task.isOnlyLocalUser();
    boolean testDiagram = RuminaqDiagramUtil.isTest(getFeatureProvider()
        .getDiagramTypeProvider().getDiagram().eResource().getURI());
    boolean specialColor = false;
//    for (GraphicsAlgorithm c : parent.getGraphicsAlgorithm()
//        .getGraphicsAlgorithmChildren())
//      if (c instanceof RoundedRectangle) {
//        Style s = TaskStyle.getOnlyLocalStyle(getDiagram(), false);
//        if (s != null && c.getStyle() != null
//            && s.getId().equals(c.getStyle().getId()))
//          specialColor = true;
//      }

    if (testDiagram && specialColor)
      onlyLocalUpdateNeeded = true;
    if (!testDiagram && specialColor && !onlyLocal)
      onlyLocalUpdateNeeded = true;
    if (!testDiagram && !specialColor && onlyLocal)
      onlyLocalUpdateNeeded = true;

    boolean updateNeeded = superUpdateNeeded || onlyLocalUpdateNeeded;
    return updateNeeded ? Reason.createTrueReason()
        : Reason.createFalseReason();
  }

  @Override
  public boolean update(IUpdateContext context) {
    if (!updateNeededChecked)
      if (!this.updateNeeded(context).toBoolean())
        return false;

    boolean updated = false;
    if (superUpdateNeeded)
      updated = updated | super.update(context);
    if (onlyLocalUpdateNeeded)
      updated = updated
          | updateOnlyLocal((ContainerShape) context.getPictogramElement());
    return updated;
  }

  protected boolean updateOnlyLocal(ContainerShape pe) {
    Task task = (Task) getBusinessObjectForPictogramElement(pe);
    boolean onlyLocal = task.isOnlyLocalDefault() ? task.isOnlyLocal()
        : task.isOnlyLocalUser();
    boolean testDiagram = RuminaqDiagramUtil.isTest(getFeatureProvider()
        .getDiagramTypeProvider().getDiagram().eResource().getURI());
    boolean specialColor = false;
    for (GraphicsAlgorithm c : pe.getGraphicsAlgorithm()
        .getGraphicsAlgorithmChildren())
      if (c instanceof RoundedRectangle) {
//        Style s = TaskStyle.getOnlyLocalStyle(getDiagram(), false);
//        if (s != null && c.getStyle() != null
//            && s.getId().equals(c.getStyle().getId()))
//          specialColor = true;
      }

    if (testDiagram && specialColor) {
//      for (GraphicsAlgorithm c : pe.getGraphicsAlgorithm()
//          .getGraphicsAlgorithmChildren())
//        if (c instanceof RoundedRectangle)
//          c.setStyle(TaskStyle.getStyle(getDiagram()));
    }
    if (!testDiagram && specialColor && !onlyLocal) {
//      for (GraphicsAlgorithm c : pe.getGraphicsAlgorithm()
//          .getGraphicsAlgorithmChildren())
//        if (c instanceof RoundedRectangle)
//          c.setStyle(TaskStyle.getStyle(getDiagram()));
    }
    if (!testDiagram && !specialColor && onlyLocal) {
//      for (GraphicsAlgorithm c : pe.getGraphicsAlgorithm()
//          .getGraphicsAlgorithmChildren())
//        if (c instanceof RoundedRectangle)
//          c.setStyle(TaskStyle.getOnlyLocalStyle(getDiagram()));
    }
    return true;
  }

  protected void addPort(Task task, ContainerShape parent, PortsDescr pd) {
    if (pd == null)
      return;
    try {
      Field f = pd.getClass().getField(pd.toString());
      IN in = f.getAnnotation(IN.class);
      if (in != null) {
        if (in.n() == 1)
          addInputPort(task, parent, in.name(), in.label(), in.type(),
              in.asynchronous(), in.group(), in.hold(), in.queue(), in.pos());
        else {
          String id = null;
          int k = -1;
          loop: while (id == null) {
            k++;
            if (TasksUtil.getAllMutlipleInternalInputPorts(task, in.name())
                .size() == 0) {
              id = in.name() + " " + 0;
              break;
            }
            for (InternalInputPort ip : task.getInputPort())
              if (ip.getId().equals(in.name() + " " + k))
                continue loop;
            id = in.name() + " " + k;
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
          addInputPort(task, parent, id, in.label(), in.type(),
              in.asynchronous(), grp, in.hold(), in.queue(), in.pos());
        }
        redistributePorts(parent, in.pos());
      }
      OUT out = f.getAnnotation(OUT.class);
      if (out != null) {
        if (out.n() == 1)
          addOutputPort(task, parent, out.name(), out.label(), out.type(),
              out.pos());
        else {
          String id = null;
          int k = -1;
          loop: while (id == null) {
            k++;
            if (TasksUtil.getAllMutlipleInternalOutputPorts(task, out.name())
                .size() == 0) {
              id = out.name() + " " + 0;
              break;
            }
            for (InternalOutputPort op : task.getOutputPort())
              if (op.getId().equals(out.name() + " " + k))
                continue loop;
            id = out.name() + " " + k;
          }
          addOutputPort(task, parent, id, out.label(), out.type(), out.pos());
        }
        redistributePorts(parent, out.pos());
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
    in.setParent(task);
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

    int lineWidth = AddTaskFeature.INPUT_PORT_WIDTH;
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
        x = parent.getGraphicsAlgorithm().getWidth() - AddTaskFeature.PORT_SIZE;
        y = 0;
        break;
      case BOTTOM:
        x = 0;
        y = parent.getGraphicsAlgorithm().getHeight()
            - AddTaskFeature.PORT_SIZE;
        break;
    }
    LineStyle lineStyle = LineStyle.SOLID;
    if (in.isAsynchronous())
      lineStyle = LineStyle.DOT;
    ContainerShape containerShape = AddTaskFeature
        .createPictogramForInternalPort(parent, x, y, AddTaskFeature.PORT_SIZE,
            AddTaskFeature.PORT_SIZE, getDiagram(), lineWidth, lineStyle);
    peCreateService.createChopboxAnchor(containerShape);
    ContainerShape portLabelShape = AddTaskFeature.addInternalPortLabel(
        getDiagram(), parent, in.getId(), AddTaskFeature.PORT_SIZE,
        AddTaskFeature.PORT_SIZE, x, y, InternalPortLabelPosition.RIGHT);

    link(containerShape, new Object[] { in, portLabelShape });
    link(portLabelShape, new Object[] { in, containerShape });

    if (!showLabel)
      portLabelShape.setVisible(false);

    redistributePorts(parent, pos);
  }

  protected void addOutputPort(Task task, ContainerShape parent, String name,
      boolean showLabel, Class<? extends DataType>[] datatypes, Position pos) {
    IPeCreateService peCreateService = Graphiti.getPeCreateService();

    InternalOutputPort out = (InternalOutputPort) RuminaqFactory.eINSTANCE
        .createInternalOutputPort();
    out.setId(name);
    out.setParent(task);
    for (Class<? extends DataType> cdt : datatypes) {
      DataType dt = DataTypeManager.INSTANCE
          .getDataTypeFromName(ModelUtil.getName(cdt));
      if (dt != null)
        out.getDataType().add(dt);
    }
    task.getOutputPort().add(out);

    int lineWidth = AddTaskFeature.OUTPUT_PORT_WIDTH;
    int x, y;
    switch (pos) {
      default:
      case LEFT:
        x = 0;
        y = 0;
        break;
      case TOP:
        x = parent.getGraphicsAlgorithm().getWidth() - AddTaskFeature.PORT_SIZE;
        y = 0;
        break;
      case RIGHT:
        x = parent.getGraphicsAlgorithm().getWidth() - AddTaskFeature.PORT_SIZE;
        y = 0;
        break;
      case BOTTOM:
        x = 0;
        y = parent.getGraphicsAlgorithm().getHeight()
            - AddTaskFeature.PORT_SIZE;
        break;
    }
    ContainerShape containerShape = AddTaskFeature
        .createPictogramForInternalPort(parent, x, y, AddTaskFeature.PORT_SIZE,
            AddTaskFeature.PORT_SIZE, getDiagram(), lineWidth, LineStyle.SOLID);
    peCreateService.createChopboxAnchor(containerShape);
    ContainerShape portLabelShape = AddTaskFeature.addInternalPortLabel(
        getDiagram(), parent, out.getId(), AddTaskFeature.PORT_SIZE,
        AddTaskFeature.PORT_SIZE, x, y, InternalPortLabelPosition.LEFT);

    link(containerShape, new Object[] { out, portLabelShape });
    link(portLabelShape, new Object[] { out, containerShape });

    if (!showLabel)
      portLabelShape.setVisible(false);

    redistributePorts(parent, pos);
  }

  protected void removePortShape(Task task, ContainerShape parent,
      InternalPort internalPort) {
    Shape toRemove = AddTaskFeature.getPictogramElementOfInternalPort(parent,
        internalPort);

    Position pos = AddTaskFeature.getPosition(parent, internalPort);

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
      IN in = f.getAnnotation(IN.class);
      if (in != null) {
        if (in.n() == 1)
          removePortShape(task, shape,
              TasksUtil.getInternalPort(task, in.name()));
        else {
          List<InternalInputPort> ports = TasksUtil
              .getAllMutlipleInternalInputPorts(task, in.name());
          removePortShape(task, shape, TasksUtil.getInternalPort(task,
              in.name() + " " + (ports.size() - 1)));
        }
      }
      OUT out = f.getAnnotation(OUT.class);
      if (out != null) {
        if (out.n() == 1)
          removePortShape(task, shape,
              TasksUtil.getInternalPort(task, out.name()));
        else {
          List<InternalOutputPort> ports = TasksUtil
              .getAllMutlipleInternalOutputPorts(task, out.name());
          removePortShape(task, shape, TasksUtil.getInternalPort(task,
              out.name() + " " + (ports.size() - 1)));
        }
      }
    } catch (NoSuchFieldException | SecurityException e) {
    }
  }

  private void redistributePorts(ContainerShape parent, Position pos) {
    switch (pos) {
      case LEFT:
        AddTaskFeature.distributePortsOnLeft(parent, getFeatureProvider());
        break;
      case TOP:
        AddTaskFeature.distributePortsOnTop(parent, getFeatureProvider());
        break;
      case RIGHT:
        AddTaskFeature.distributePortsOnRight(parent, getFeatureProvider());
        break;
      case BOTTOM:
        AddTaskFeature.distributePortsOnBottom(parent, getFeatureProvider());
        break;
      default:
        break;
    }
  }
}
