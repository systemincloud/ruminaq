/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.update;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.styles.LineStyle;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.ruminaq.gui.model.Position;
import org.ruminaq.gui.model.diagram.TaskShape;
import org.ruminaq.logs.ModelerLoggerFactory;
import org.ruminaq.model.ruminaq.DataType;
import org.ruminaq.model.ruminaq.InternalInputPort;
import org.ruminaq.model.ruminaq.InternalOutputPort;
import org.ruminaq.model.ruminaq.ModelUtil;
import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.model.ruminaq.UserDefinedTask;
import ch.qos.logback.classic.Logger;

public abstract class UpdateUserDefinedTaskFeature extends UpdateTaskFeature {

  private static final Logger LOGGER = ModelerLoggerFactory
      .getLogger(UpdateUserDefinedTaskFeature.class);

  private static Optional<TaskShape> toTaskShape(PictogramElement pe) {
    return Optional.of(pe).filter(TaskShape.class::isInstance)
        .map(TaskShape.class::cast);
  }

  private static Optional<UserDefinedTask> toModel(PictogramElement pe) {
    return toTaskShape(pe).map(TaskShape::getModelObject)
        .filter(UserDefinedTask.class::isInstance)
        .map(UserDefinedTask.class::cast);
  }

  private boolean inputsUpdateNeeded = false;
  private boolean outputsUpdateNeeded = false;
  private boolean atomicUpdateNeeded = false;
  private boolean onlyLocalUpdateNeeded = false;
  private boolean paramsUpdateNeeded = false;

  @SuppressWarnings("unchecked")
  protected final class FileInternalInputPort {
    private String name = null;
    private List<DataType> dataType = null;
    private boolean asynchronous = false;
    private int group = -1;
    private boolean hold = false;
    private String queue = "1";

    public FileInternalInputPort(String name, List<DataType> dataType,
        boolean asynchronous, int group, boolean hold, String queue) {
      this.name = name;
      this.dataType = dataType;
      this.asynchronous = asynchronous;
      this.group = group;
      this.hold = hold;
      this.queue = queue;
    }

    String getName() {
      return name;
    }

    List<DataType> getDataType() {
      return dataType;
    }

    boolean isAsynchronus() {
      return asynchronous;
    }

    int getGroup() {
      return group;
    }

    boolean isHold() {
      return hold;
    }

    String getQueue() {
      return queue;
    }

    Class<? extends DataType>[] getDataTypeClasses() {
      Class<?>[] ret = new Class<?>[dataType.size()];
      int i = 0;
      for (DataType dt : dataType)
        ret[i++] = dt.getClass();
      return (Class<? extends DataType>[]) ret;
    }
  }

  @SuppressWarnings("unchecked")
  protected final class FileInternalOutputPort {
    private String name = null;
    private List<DataType> dataType = null;

    public FileInternalOutputPort(String name, List<DataType> dataType) {
      this.name = name;
      this.dataType = dataType;
    }

    String getName() {
      return name;
    }

    List<DataType> getDataType() {
      return dataType;
    }

    Class<? extends DataType>[] getDataTypeClasses() {
      Class<?>[] ret = new Class<?>[dataType.size()];
      int i = 0;
      for (DataType dt : dataType)
        ret[i++] = dt.getClass();
      return (Class<? extends DataType>[]) ret;
    }
  }

  protected String iconDesc = null;

  protected List<FileInternalInputPort> inputs = null;
  protected List<FileInternalOutputPort> outputs = null;
  protected boolean atomic = true;
  protected boolean onlyLocal = false;
  protected List<InternalInputPort> inputPorts = null;
  protected List<InternalOutputPort> outputPorts = null;

  public UpdateUserDefinedTaskFeature(IFeatureProvider fp) {
    super(fp);
  }

  private boolean iconDescriptionUpdateNeeded(PictogramElement pe) {
//    if (iconDesc != null)
//      for (GraphicsAlgorithm ga : pe.getGraphicsAlgorithm()
//          .getGraphicsAlgorithmChildren())
//        if (Graphiti.getPeService().getProperty(ga,
//            AbstractAddTaskFeature.ICON_DESC_PROPERTY) != null)
//          return iconDesc.equals(((Text) ga).getValue());
    return true;
  }

  private boolean compareInputPorts(List<FileInternalInputPort> inputs,
      List<InternalInputPort> inputPorts) {
    if (inputs.size() != inputPorts.size())
      return false;
    loop: for (FileInternalInputPort fip : inputs) {
      for (InternalInputPort iip : inputPorts)
        if (fip.getName().equals(iip.getId())
            && ModelUtil.areEquals(fip.getDataType(), iip.getDataType())
            && fip.isAsynchronus() == iip.isAsynchronous()
            && fip.getGroup() == iip.getGroup()
            && fip.isHold() == iip.isDefaultHoldLast()
            && fip.getQueue().equals(iip.getDefaultQueueSize()))
          continue loop;
      return false;
    }
    return true;
  }

  private boolean compareOutputPorts(List<FileInternalOutputPort> outputs,
      List<InternalOutputPort> outputPorts) {
    if (outputs.size() != outputPorts.size())
      return false;
    loop: for (FileInternalOutputPort fip : outputs) {
      for (InternalOutputPort iop : outputPorts)
        if (fip.getName().equals(iop.getId())
            && ModelUtil.areEquals(fip.getDataType(), iop.getDataType()))
          continue loop;
      return false;
    }
    return true;
  }

  private boolean compareParams(UserDefinedTask udt) {
    LOGGER.trace("compareParams");
    Map<String, String> shouldBe = getParameters(udt);
    Set<String> is = udt.getParameters().keySet();
    return shouldBe.keySet().equals(is);
  }

  @Override
  public IReason updateNeeded(IUpdateContext context) {
    inputs = new ArrayList<>();
    outputs = new ArrayList<>();
    PictogramElement pictogramElement = context.getPictogramElement();

    Task task = toModel(pictogramElement).get();
    String resource = getResource(task);

    inputPorts = task.getInputPort();
    outputPorts = task.getOutputPort();

    if ("".equals(resource)) {
      loadIconDesc();
      return Reason.createFalseReason();
    }
    if (!load(resource)) {
      loadIconDesc();
      return Reason.createFalseReason();
    }
    loadIconDesc();
    loadInputPorts();
    loadOutputPorts();
    loadAtomic();

    this.inputsUpdateNeeded = !compareInputPorts(inputs, inputPorts);
    this.outputsUpdateNeeded = !compareOutputPorts(outputs, outputPorts);
    this.atomicUpdateNeeded = atomic != task.isAtomic() ? true : false;
    this.paramsUpdateNeeded = !compareParams(((UserDefinedTask) task));

    boolean updateNeeded = this.inputsUpdateNeeded || this.outputsUpdateNeeded
        || this.atomicUpdateNeeded || this.onlyLocalUpdateNeeded
        || this.paramsUpdateNeeded;
    if (super.updateNeeded(context).toBoolean()
        || iconDescriptionUpdateNeeded(pictogramElement) || updateNeeded) {
      return Reason.createTrueReason();
    } else {
      return Reason.createFalseReason();
    }
  }

  protected abstract String getResource(Task task);

  public abstract boolean load(String resource);

  protected void loadIconDesc() {
  }

  protected abstract void loadInputPorts();

  protected abstract void loadOutputPorts();

  protected abstract void loadAtomic();

  protected abstract Map<String, String> getParameters(UserDefinedTask udt);

  @Override
  public boolean update(IUpdateContext context) {
    if (super.updateNeeded(context).toBoolean()) {
      super.update(context);
    }

    ContainerShape parent = (ContainerShape) context.getPictogramElement();
    Task be = (Task) getBusinessObjectForPictogramElement(parent);

    if (iconDescriptionUpdateNeeded(parent)) {
      iconDescriptionUpdate(parent, be);
    }

    if (inputsUpdateNeeded) {
      inputsUpdate(parent, be);
    }

    if (outputsUpdateNeeded) {
      outputsUpdate(parent, be);
    }

    if (atomicUpdateNeeded) {
      atomicUpdate(parent, be);
    }

    if (paramsUpdateNeeded) {
      paramsUpdate(parent, be);
    }

    return true;
  }

  private boolean iconDescriptionUpdate(ContainerShape parent, Task be) {
//    if (iconDesc != null)
//      for (GraphicsAlgorithm ga : parent.getGraphicsAlgorithm()
//          .getGraphicsAlgorithmChildren())
//        if (Graphiti.getPeService().getProperty(ga,
//            AbstractAddTaskFeature.ICON_DESC_PROPERTY) != null) {
//          ((Text) ga).setValue(iconDesc);
//          return true;
//        }
    return false;
  }

  private boolean inputsUpdate(ContainerShape parent, Task task) {
    List<InternalInputPort> inputsToRemove = new ArrayList<>();
    loop: for (InternalInputPort iip : inputPorts) {
      for (FileInternalInputPort fip : inputs)
        if (fip.getName().equals(iip.getId())) {

          if (!ModelUtil.areEquals(fip.getDataType(), iip.getDataType())) {
            while (iip.getDataType().size() > 0)
              iip.getDataType().remove(0);
            iip.getDataType().addAll(fip.getDataType());
          }
          if (fip.isAsynchronus() != iip.isAsynchronous()) {
//            iip.setAsynchronous(fip.isAsynchronus());
//            if (iip.isAsynchronous())
//              AbstractAddTaskFeature
//                  .getPictogramElementOfInternalPort(getDiagram(), iip)
//                  .getGraphicsAlgorithm().getGraphicsAlgorithmChildren().get(0)
//                  .setLineStyle(LineStyle.DOT);
//            else
//              AbstractAddTaskFeature
//                  .getPictogramElementOfInternalPort(getDiagram(), iip)
//                  .getGraphicsAlgorithm().getGraphicsAlgorithmChildren().get(0)
//                  .setLineStyle(LineStyle.SOLID);
          }
          if (fip.getGroup() != iip.getGroup()) {
            iip.setGroup(fip.getGroup());
          }
          if (fip.isHold() != iip.isDefaultHoldLast()) {
            if (iip.isDefaultHoldLast() == iip.isHoldLast())
              iip.setHoldLast(fip.isHold());
            iip.setDefaultHoldLast(fip.isHold());
          }
          if (fip.getQueue() != iip.getDefaultQueueSize()) {
            if (iip.getDefaultQueueSize().equals(iip.getQueueSize()))
              iip.setQueueSize(fip.getQueue());
            iip.setDefaultQueueSize(fip.getQueue());
          }
          continue loop;
        }
      inputsToRemove.add(iip);
    }
//    for (InternalInputPort iip : inputsToRemove)
//      removePortShape(task, parent, iip);

    loop: for (FileInternalInputPort fip : inputs) {
      for (InternalInputPort iip : inputPorts)
        if (fip.getName().equals(iip.getId()))
          continue loop;
      createInputPort(task, parent, fip.getName(), true,
          fip.getDataTypeClasses(), fip.isAsynchronus(), fip.getGroup(),
          fip.isHold(), fip.getQueue(), Position.LEFT);
    }
    return true;
  }

  private boolean outputsUpdate(ContainerShape parent, Task task) {
    List<InternalOutputPort> outputsToRemove = new ArrayList<>();
    loop: for (InternalOutputPort iop : outputPorts) {
      for (FileInternalOutputPort fip : outputs)
        if (fip.getName().equals(iop.getId())) {
          if (!ModelUtil.areEquals(fip.getDataType(), iop.getDataType())) {
            while (iop.getDataType().size() > 0)
              iop.getDataType().remove(0);
            iop.getDataType().addAll(fip.getDataType());
          }
          continue loop;
        }
      outputsToRemove.add(iop);
    }
//    for (InternalOutputPort iop : outputsToRemove)
//      removePortShape(task, parent, iop);

    loop: for (FileInternalOutputPort fip : outputs) {
      for (InternalOutputPort iop : outputPorts)
        if (fip.getName().equals(iop.getId()))
          continue loop;
      createOutputPort(task, parent, fip.getName(), true,
          fip.getDataTypeClasses(), Position.RIGHT);
    }
    return true;
  }

  private boolean atomicUpdate(ContainerShape parent, Task be) {
    be.setAtomic(atomic);
    if (atomic)
      parent.getGraphicsAlgorithm().getGraphicsAlgorithmChildren().get(0)
          .setLineStyle(LineStyle.SOLID);
    else
      parent.getGraphicsAlgorithm().getGraphicsAlgorithmChildren().get(0)
          .setLineStyle(LineStyle.DOT);
    return true;
  }

  private boolean paramsUpdate(ContainerShape parent, Task be) {
    UserDefinedTask udt = (UserDefinedTask) be;
    Map<String, String> shouldBe = getParameters(udt);
    Set<String> is = udt.getParameters().keySet();

    List<String> toRemove = new LinkedList<>();
    for (String s : is)
      if (!shouldBe.keySet().contains(s))
        toRemove.add(s);
    for (String s : toRemove)
      udt.getParameters().remove(s);
    for (String s : toRemove)
      udt.getDefaultParameters().remove(s);

    List<String> toAdd = new LinkedList<>();
    for (String s : shouldBe.keySet())
      if (!is.contains(s))
        toAdd.add(s);
    for (String s : toAdd)
      udt.getParameters().put(s, shouldBe.get(s));
    for (String s : toAdd)
      udt.getDefaultParameters().put(s, shouldBe.get(s));

    return true;
  }
}
