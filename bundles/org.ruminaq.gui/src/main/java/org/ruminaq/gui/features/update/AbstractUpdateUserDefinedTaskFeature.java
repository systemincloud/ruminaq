/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.update;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.Collections;
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
      this.dataType = Collections.unmodifiableList(dataType);
      this.asynchronous = asynchronous;
      this.group = group;
      this.hold = hold;
      this.queue = queue;
    }

    String getName() {
      return name;
    }

    List<DataType> getDataType() {
      return Collections.unmodifiableList(dataType);
    }

    boolean isAsynchronous() {
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
    private List<DataType> dataType;

    public FileInternalOutputPort(String name, List<DataType> dataType) {
      this.name = name;
      this.dataType = Collections.unmodifiableList(dataType);
    }

    String getName() {
      return name;
    }

    List<DataType> getDataType() {
      return Collections.unmodifiableList(dataType);
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

  private static Stream<InternalInputPort> modelInputPorts(
      IUpdateContext context) {
    return toModel(context).map(UserDefinedTask::getInputPort).map(List::stream)
        .orElseGet(Stream::empty);
  }

  private static Stream<InternalOutputPort> modelOutputPorts(
      IUpdateContext context) {
    return toModel(context).map(UserDefinedTask::getOutputPort)
        .map(List::stream).orElseGet(Stream::empty);
  }

  private Stream<SimpleEntry<InternalInputPort, FileInternalInputPort>> modelFileInputPorts(
      IUpdateContext context) {
    return modelInputPorts(context)
        .map(iop -> new SimpleEntry<>(iop,
            inputPorts().stream()
                .filter(fip -> fip.getName().equals(iop.getId())).findFirst()))
        .filter(e -> e.getValue().isPresent())
        .map(e -> new SimpleEntry<>(e.getKey(), e.getValue().get()));
  }

  private Stream<SimpleEntry<InternalOutputPort, FileInternalOutputPort>> modelFileOutputPorts(
      IUpdateContext context) {
    return modelOutputPorts(context)
        .map(iop -> new SimpleEntry<>(iop,
            outputPorts().stream()
                .filter(fip -> fip.getName().equals(iop.getId())).findFirst()))
        .filter(e -> e.getValue().isPresent())
        .map(e -> new SimpleEntry<>(e.getKey(), e.getValue().get()));
  }

  private boolean iconDescriptionUpdateNeeded(IUpdateContext context) {
    return toTaskShape(context).map(TaskShape::getDescription)
        .filter(iconDesc()::equals).isEmpty();
  }

  private boolean inputPortsUpdateNeeded(IUpdateContext context) {
    if (inputPorts().size() != modelInputPorts(context).count()) {
      return true;
    }
    return inputPorts().stream().anyMatch(fip -> modelInputPorts(context)
        .filter(iip -> fip.getName().equals(iip.getId()))
        .filter(
            iip -> ModelUtil.areEquals(fip.getDataType(), iip.getDataType()))
        .filter(iip -> fip.isAsynchronous() == iip.isAsynchronous())
        .filter(iip -> fip.getGroup() == iip.getGroup())
        .filter(iip -> fip.isHold() == iip.isDefaultHoldLast())
        .noneMatch(iip -> fip.getQueue().equals(iip.getDefaultQueueSize())));
  }

  private boolean outputPortsUpdateNeeded(IUpdateContext context) {
    if (outputPorts().size() != modelOutputPorts(context).count()) {
      return true;
    }
    return outputPorts().stream()
        .anyMatch(fip -> modelOutputPorts(context)
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
        .filter(Predicate.not(""::equals)).map(this::load)
        .orElse(Boolean.FALSE);
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
    modelInputPorts(context)
        .filter(iop -> inputPorts().stream().map(FileInternalInputPort::getName)
            .noneMatch(iop.getId()::equals))
        .collect(Collectors.toList()).stream().forEach(this::deleteInputPort);
    modelFileInputPorts(context)
        .filter(e -> !ModelUtil.areEquals(e.getKey().getDataType(),
            e.getValue().getDataType()))
        .forEach((SimpleEntry<InternalInputPort, FileInternalInputPort> e) -> {
          e.getKey().getDataType().clear();
          e.getKey().getDataType().addAll(e.getValue().getDataType());
        });
    modelFileInputPorts(context)
        .filter(
            e -> e.getKey().isAsynchronous() != e.getValue().isAsynchronous())
        .forEach(
            e -> e.getKey().setAsynchronous(e.getValue().isAsynchronous()));
    modelFileInputPorts(context)
        .filter(e -> e.getKey().getGroup() != e.getValue().getGroup())
        .forEach(e -> e.getKey().setGroup(e.getValue().getGroup()));
    modelFileInputPorts(context)
        .filter(e -> e.getKey().isDefaultHoldLast() != e.getValue().isHold())
        .forEach(e -> e.getKey().setDefaultHoldLast(e.getValue().isHold()));
    modelFileInputPorts(context).filter(
        e -> !e.getKey().getDefaultQueueSize().equals(e.getValue().getQueue()))
        .forEach(e -> e.getKey().setDefaultQueueSize(e.getValue().getQueue()));
    inputPorts().stream()
        .filter(fip -> modelInputPorts(context).map(InternalInputPort::getId)
            .noneMatch(fip.getName()::equals))
        .forEach(fip -> createInputPort(toModel(context).get(), fip.getName(),
            fip.getDataTypeClasses(), fip.isAsynchronous(), fip.getGroup(),
            fip.isHold(), fip.getQueue()));
    return true;
  }

  private boolean outputsUpdate(IUpdateContext context) {
    modelOutputPorts(context)
        .filter(
            iop -> outputPorts().stream().map(FileInternalOutputPort::getName)
                .noneMatch(iop.getId()::equals))
        .collect(Collectors.toList()).stream().forEach(this::deleteOutputPort);
    modelFileOutputPorts(context).filter(e -> !ModelUtil
        .areEquals(e.getKey().getDataType(), e.getValue().getDataType()))
        .forEach(
            (SimpleEntry<InternalOutputPort, FileInternalOutputPort> e) -> {
              e.getKey().getDataType().clear();
              e.getKey().getDataType().addAll(e.getValue().getDataType());
            });
    outputPorts().stream()
        .filter(fip -> modelOutputPorts(context).map(InternalOutputPort::getId)
            .noneMatch(fip.getName()::equals))
        .forEach(fip -> createOutputPort(toModel(context).get(), fip.getName(),
            fip.getDataTypeClasses()));
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
