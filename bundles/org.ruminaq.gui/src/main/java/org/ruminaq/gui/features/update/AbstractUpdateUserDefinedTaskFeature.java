/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.update;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.ruminaq.gui.model.Position;
import org.ruminaq.gui.model.diagram.TaskShape;
import org.ruminaq.model.ruminaq.DataType;
import org.ruminaq.model.ruminaq.InternalInputPort;
import org.ruminaq.model.ruminaq.InternalOutputPort;
import org.ruminaq.model.ruminaq.ModelUtil;
import org.ruminaq.model.ruminaq.Parameter;
import org.ruminaq.model.ruminaq.RuminaqFactory;
import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.model.ruminaq.UserDefinedTask;

/**
 * CommonUpdateTaskFeature for UserDefinedTask.
 *
 * @author Marek Jagielski
 */
public abstract class AbstractUpdateUserDefinedTaskFeature
    extends UpdateTaskFeature {

  protected static final class FileInternalInputPort {
    private String name;
    private List<DataType> dataType;
    private boolean asynchronous;
    private int group = -1;
    private boolean hold;
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

    Collection<Class<? extends DataType>> getDataTypeClasses() {
      return dataType.stream().map(DataType::getClass)
          .collect(Collectors.toList());
    }
  }

  protected static final class FileInternalOutputPort {
    private String name;
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

    Collection<Class<? extends DataType>> getDataTypeClasses() {
      return dataType.stream().map(DataType::getClass)
          .collect(Collectors.toList());
    }
  }

  protected AbstractUpdateUserDefinedTaskFeature(IFeatureProvider fp) {
    super(fp);
  }

  private static Optional<TaskShape> toTaskShape(PictogramElement pe) {
    return Optional.of(pe).filter(TaskShape.class::isInstance)
        .map(TaskShape.class::cast);
  }

  private static Optional<TaskShape> toTaskShape(IUpdateContext context) {
    return toTaskShape(context.getPictogramElement());
  }

  private static Optional<UserDefinedTask> toModel(PictogramElement pe) {
    return toTaskShape(pe).map(TaskShape::getModelObject)
        .filter(UserDefinedTask.class::isInstance)
        .map(UserDefinedTask.class::cast);
  }

  private static Optional<UserDefinedTask> toModel(IUpdateContext context) {
    return toModel(context.getPictogramElement());
  }

  private boolean iconDescriptionUpdateNeeded(IUpdateContext context) {
    return toTaskShape(context).map(TaskShape::getDescription)
        .filter(iconDesc()::equals).isEmpty();
  }

  private boolean inputPortsUpdateNeeded(IUpdateContext context) {
    if (inputPorts().size() != toModel(context).get().getInputPort().size()) {
      return true;
    }
    return inputPorts().stream()
        .anyMatch(fip -> toModel(context).get().getInputPort().stream()
            .noneMatch(iip -> fip.getName().equals(iip.getId())
                && ModelUtil.areEquals(fip.getDataType(), iip.getDataType())
                && fip.isAsynchronus() == iip.isAsynchronous()
                && fip.getGroup() == iip.getGroup()
                && fip.isHold() == iip.isDefaultHoldLast()
                && fip.getQueue().equals(iip.getDefaultQueueSize())));
  }

  private boolean outputPortsUpdateNeeded(IUpdateContext context) {
    if (outputPorts().size() != toModel(context).get().getOutputPort().size()) {
      return true;
    }
    return outputPorts().stream()
        .anyMatch(fip -> toModel(context).get().getOutputPort().stream()
            .noneMatch(iop -> fip.getName().equals(iop.getId())
                && ModelUtil.areEquals(fip.getDataType(), iop.getDataType())));
  }

  private boolean atomicUpdateNeeded(IUpdateContext context) {
    return toModel(context).map(Task::isAtomic).map(a -> a != isAtomic())
        .orElse(Boolean.FALSE);
  }

  private boolean paramsUpdateNeeded(IUpdateContext context) {
    Map<String, String> shouldBe = getParameters();
    Set<String> is = toModel(context).map(UserDefinedTask::getParameter)
        .map(List::stream).orElseGet(Stream::empty).map(Parameter::getKey)
        .collect(Collectors.toSet());
    return !shouldBe.keySet().equals(is);
  }

  @Override
  public boolean canUpdate(IUpdateContext context) {
    return toModel(context).map(this::getResource)
        .filter(Predicate.not(""::equals)).map(this::load).orElse(false);
  }

  @Override
  public IReason updateNeeded(IUpdateContext context) {
    return toModel(context).map(this::getResource)
        .filter(r -> iconDescriptionUpdateNeeded(context)
            || inputPortsUpdateNeeded(context)
            || outputPortsUpdateNeeded(context) || atomicUpdateNeeded(context)
            || paramsUpdateNeeded(context)
            || super.updateNeeded(context).toBoolean())
        .map(r -> Reason.createTrueReason())
        .orElseGet(Reason::createFalseReason);
  }

  protected String getResource(Task task) {
    return Optional.of(task).filter(UserDefinedTask.class::isInstance)
        .map(UserDefinedTask.class::cast)
        .map(UserDefinedTask::getImplementationPath).orElse("");
  }

  public abstract boolean load(String resource);

  protected String iconDesc() {
    return "";
  }

  protected abstract List<FileInternalInputPort> inputPorts();

  protected abstract List<FileInternalOutputPort> outputPorts();

  protected abstract boolean isAtomic();

  protected abstract Map<String, String> getParameters();

  @Override
  public boolean update(IUpdateContext context) {
    if (iconDescriptionUpdateNeeded(context)) {
      iconDescriptionUpdate(context);
    }

    if (inputPortsUpdateNeeded(context)) {
      inputsUpdate(context);
    }

    if (outputPortsUpdateNeeded(context)) {
      outputsUpdate(context);
    }

    if (atomicUpdateNeeded(context)) {
      atomicUpdate(context);
    }

    if (paramsUpdateNeeded(context)) {
      paramsUpdate(context);
    }

    if (super.updateNeeded(context).toBoolean()) {
      super.update(context);
    }

    getDiagramBehavior().refreshContent();

    return true;
  }

  private void iconDescriptionUpdate(IUpdateContext context) {
    toTaskShape(context).ifPresent(ts -> ts.setDescription(iconDesc()));
  }

  private boolean inputsUpdate(IUpdateContext context) {
    List<InternalInputPort> inputsToRemove = new ArrayList<>();
    loop: for (InternalInputPort iip : toModel(context).get().getInputPort()) {
      for (FileInternalInputPort fip : inputPorts())
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

    loop: for (FileInternalInputPort fip : inputPorts()) {
      for (InternalInputPort iip : toModel(context).get().getInputPort())
        if (fip.getName().equals(iip.getId()))
          continue loop;
      createInputPort(toModel(context).get(), toTaskShape(context).get(),
          fip.getName(), true, fip.getDataTypeClasses(), fip.isAsynchronus(),
          fip.getGroup(), fip.isHold(), fip.getQueue(), Position.LEFT);
    }
    return true;
  }

  private boolean outputsUpdate(IUpdateContext context) {
    List<InternalOutputPort> outputsToRemove = new ArrayList<>();
    loop: for (InternalOutputPort iop : toModel(context).get()
        .getOutputPort()) {
      for (FileInternalOutputPort fip : outputPorts())
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

    loop: for (FileInternalOutputPort fip : outputPorts()) {
      for (InternalOutputPort iop : toModel(context).get().getOutputPort())
        if (fip.getName().equals(iop.getId()))
          continue loop;
      createOutputPort(toModel(context).get(), toTaskShape(context).get(),
          fip.getName(), true, fip.getDataTypeClasses(), Position.RIGHT);
    }
    return true;
  }

  private void atomicUpdate(IUpdateContext context) {
    toModel(context).ifPresent(t -> t.setAtomic(isAtomic()));
  }

  private boolean paramsUpdate(IUpdateContext context) {
    toModel(context).ifPresent((UserDefinedTask udt) -> {
      Map<String, String> shouldBe = getParameters();
      udt.getParameter().removeIf(p -> !shouldBe.containsKey(p.getKey()));
      shouldBe
          .keySet().stream().filter(p -> udt.getParameter().stream()
              .map(Parameter::getKey).noneMatch(p::equals))
          .forEach(p -> createParameter(udt, p));
      shouldBe.entrySet().stream()
          .forEach(e -> udt.getParameter().stream()
              .filter(p -> p.getKey().equals(e.getKey())).findAny()
              .ifPresent(p -> p.setDefaultValue(e.getValue())));
    });

    return true;
  }

  private static void createParameter(UserDefinedTask task,
      String parameterKey) {
    Parameter parameter = RuminaqFactory.eINSTANCE.createParameter();
    parameter.setKey(parameterKey);
    task.getParameter().add(parameter);
  }
}
