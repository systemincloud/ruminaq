/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.create;

import java.lang.reflect.Field;
import java.util.AbstractMap.SimpleEntry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.ruminaq.gui.TasksUtil;
import org.ruminaq.logs.ModelerLoggerFactory;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.model.ruminaq.DataType;
import org.ruminaq.model.ruminaq.InternalInputPort;
import org.ruminaq.model.ruminaq.InternalOutputPort;
import org.ruminaq.model.ruminaq.NGroup;
import org.ruminaq.model.ruminaq.PortData;
import org.ruminaq.model.ruminaq.PortInfo;
import org.ruminaq.model.ruminaq.PortType;
import org.ruminaq.model.ruminaq.RuminaqFactory;
import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.util.Result;
import org.slf4j.Logger;

/**
 * Common class for all CreateFeautes for Tasks.
 * 
 * @author Marek Jagielski
 */
public abstract class AbstractCreateTaskFeature
    extends AbstractCreateElementFeature {

  private static final Logger LOGGER = ModelerLoggerFactory
      .getLogger(AbstractCreateTaskFeature.class);

  public AbstractCreateTaskFeature(IFeatureProvider fp,
      Class<? extends Task> clazz) {
    super(fp, clazz);
  }

  protected Object[] create(ICreateContext context, Task task) {
    LOGGER.trace("{}", task.getClass().getSimpleName());
    setDefaultId(task, context);

    addDefaultPorts(task);

    getRuminaqDiagram().getMainTask().getTask().add(task);

    addGraphicalRepresentation(context, task);
    return new Object[] { task };
  }

  protected abstract Class<? extends PortsDescr> getPortsDescription();

  private void addDefaultPorts(Task task) {
    Supplier<Stream<Field>> fields = () -> Optional
        .ofNullable(getPortsDescription()).map(Class::getFields).map(Stream::of)
        .orElseGet(Stream::empty);
    addDefaultInputPorts(task, fields);
    addDefaultOutputPorts(task, fields);
  }

  private static void addDefaultInputPorts(Task task,
      Supplier<Stream<Field>> fields) {
    fields.get().map(f -> new SimpleEntry<>(f, f.getAnnotation(PortInfo.class)))
        .filter(se -> se.getValue() != null)
        .filter(se -> PortType.IN == se.getValue().portType())
        .filter(se -> !se.getValue().opt())
        .map(se -> new SimpleEntry<>(se.getKey(), se.getValue().n()))
        .forEach((SimpleEntry<Field, Integer> e) -> IntStream
            .range(0, e.getValue()).forEach((int i) -> {
              createInputPort(task, e.getKey());
            }));
  }

  public static void createInputPort(Task task, Field field) {
    InternalInputPort inputPort = RuminaqFactory.eINSTANCE
        .createInternalInputPort();
    PortInfo pi = field.getAnnotation(PortInfo.class);

    inputPort.setId(Optional.of(pi).filter(p -> p.n() > 1).map(p -> p.id()
        + (TasksUtil.getAllMutlipleInternalInputPorts(task, p.id()).size() + 1))
        .orElseGet(() -> pi.id()));
    inputPort.setAsynchronous(pi.asynchronous());
    inputPort.setGroup(portGroup(pi, task));
    inputPort.setDefaultHoldLast(pi.hold());
    inputPort.setHoldLast(pi.hold());
    inputPort.setDefaultQueueSize(pi.queue());
    inputPort.setQueueSize(pi.queue());

    getDataTypes(field).forEach(inputPort.getDataType()::add);

    task.getInputPort().add(inputPort);
  }

  private static int portGroup(PortInfo pi, Task task) {
    return Optional.of(pi)
        .filter(p -> p.n() > 1 && pi.ngroup() != NGroup.SAME && p.group() != -1)
        .map((PortInfo p) -> {
          Integer j = p.group();
          boolean free = true;
          do {
            free = task.getInputPort().stream().map(InternalInputPort::getGroup)
                .noneMatch(j::equals);
            j++;
          } while (!free);
          return j - 1;
        }).orElseGet(() -> pi.group());
  }

  private static void addDefaultOutputPorts(Task task,
      Supplier<Stream<Field>> fields) {
    fields.get().map(f -> new SimpleEntry<>(f, f.getAnnotation(PortInfo.class)))
        .filter(se -> se.getValue() != null)
        .filter(se -> PortType.OUT == se.getValue().portType())
        .filter(se -> !se.getValue().opt())
        .map(se -> new SimpleEntry<>(se.getKey(), se.getValue().n()))
        .forEach((SimpleEntry<Field, Integer> e) -> IntStream
            .range(0, e.getValue()).forEach((int i) -> {
              createOutputPort(task, e.getKey());
            }));
  }

  public static void createOutputPort(Task task, Field field) {
    InternalOutputPort outputPort = RuminaqFactory.eINSTANCE
        .createInternalOutputPort();
    PortInfo pi = field.getAnnotation(PortInfo.class);

    outputPort.setId(Optional.of(pi).filter(p -> p.n() > 1).map(p -> p.id()
        + (TasksUtil.getAllMutlipleInternalInputPorts(task, p.id()).size() + 1))
        .orElseGet(() -> pi.id()));
    getDataTypes(field).forEach(outputPort.getDataType()::add);

    task.getOutputPort().add(outputPort);
  }

  private static Stream<DataType> getDataTypes(Field field) {
    return Stream.of(field.getAnnotationsByType(PortData.class))
        .map((PortData pd) -> Result.attempt(() -> {
          EClassifier classif = ((EPackage) pd.dataPackage()
              .getDeclaredField("eINSTANCE").get(null))
                  .getEClassifier(pd.type().getSimpleName());
          return ((EFactory) pd.dataFactory().getDeclaredField("eINSTANCE")
              .get(null)).create((EClass) classif);
        })).map(r -> r.orElse(null)).filter(Objects::nonNull)
        .filter(DataType.class::isInstance).map(DataType.class::cast);
  }
}
