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

  private void addDefaultInputPorts(Task task, Supplier<Stream<Field>> fields) {
    fields.get().map(f -> new SimpleEntry<>(f, f.getAnnotation(PortInfo.class)))
        .filter(se -> se.getValue() != null)
        .filter(se -> PortType.IN.equals(se.getValue().portType()))
        .filter(se -> !se.getValue().opt())
        .map(se -> new SimpleEntry<>(se, se.getValue().n())).forEach(
            (SimpleEntry<SimpleEntry<Field, PortInfo>, Integer> e) -> IntStream
                .range(0, e.getValue()).forEach((int i) -> {
                  InternalInputPort inputPort = RuminaqFactory.eINSTANCE
                      .createInternalInputPort();
                  String id = e.getKey().getValue().id();
                  if (e.getKey().getValue().n() > 1) {
                    id += " " + i;
                  }
                  inputPort.setId(id);
                  inputPort
                      .setAsynchronous(e.getKey().getValue().asynchronous());
                  int group = e.getKey().getValue().group();
                  if (e.getKey().getValue().n() > 1) {
                    if (e.getKey().getValue().ngroup().equals(NGroup.SAME)) {
                      inputPort.setGroup(group);
                    } else {
                      if (group == -1) {
                        inputPort.setGroup(group);
                      } else {
                        Integer j = group;
                        boolean free = true;
                        do {
                          free = task.getInputPort().stream()
                              .map(InternalInputPort::getGroup)
                              .noneMatch(j::equals);
                          j++;
                        } while (!free);
                        inputPort.setGroup(j - 1);
                      }
                    }
                  } else {
                    inputPort.setGroup(group);
                  }

                  inputPort.setDefaultHoldLast(e.getKey().getValue().hold());
                  inputPort.setHoldLast(e.getKey().getValue().hold());
                  inputPort.setDefaultQueueSize(e.getKey().getValue().queue());
                  inputPort.setQueueSize(e.getKey().getValue().queue());

                  Stream
                      .of(e.getKey().getKey()
                          .getAnnotationsByType(PortData.class))
                      .map((PortData pd) -> {
                        try {
                          EClassifier classif = ((EPackage) pd.dataPackage()
                              .getDeclaredField("eINSTANCE").get(null))
                                  .getEClassifier(pd.type().getSimpleName());
                          return ((EFactory) pd.dataFactory()
                              .getDeclaredField("eINSTANCE").get(null))
                                  .create((EClass) classif);
                        } catch (IllegalArgumentException
                            | IllegalAccessException | NoSuchFieldException
                            | SecurityException e1) {
                          LOGGER.error("Can't create datatype"
                              + pd.type().getSimpleName(), e1);
                          return null;
                        }
                      }).filter(Objects::nonNull)
                      .filter(DataType.class::isInstance)
                      .map(DataType.class::cast)
                      .forEach(inputPort.getDataType()::add);
                  task.getInputPort().add(inputPort);
                }));
  }

  private void addDefaultOutputPorts(Task task,
      Supplier<Stream<Field>> fields) {
    fields.get().map(f -> new SimpleEntry<>(f, f.getAnnotation(PortInfo.class)))
        .filter(se -> se.getValue() != null)
        .filter(se -> PortType.OUT.equals(se.getValue().portType()))
        .filter(se -> !se.getValue().opt())
        .map(se -> new SimpleEntry<>(se, se.getValue().n())).forEach(
            (SimpleEntry<SimpleEntry<Field, PortInfo>, Integer> e) -> IntStream
                .range(0, e.getValue()).forEach((int i) -> {
                  InternalOutputPort outputPort = RuminaqFactory.eINSTANCE
                      .createInternalOutputPort();
                  String id = e.getKey().getValue().id();
                  if (e.getKey().getValue().n() > 1) {
                    id += " " + i;
                  }
                  outputPort.setId(id);
                  Stream
                      .of(e.getKey().getKey()
                          .getAnnotationsByType(PortData.class))
                      .map((PortData pd) -> {
                        try {
                          EClassifier classif = ((EPackage) pd.dataPackage()
                              .getDeclaredField("eINSTANCE").get(null))
                                  .getEClassifier(pd.type().getSimpleName());
                          return ((EFactory) pd.dataFactory()
                              .getDeclaredField("eINSTANCE").get(null))
                                  .create((EClass) classif);
                        } catch (IllegalArgumentException
                            | IllegalAccessException | NoSuchFieldException
                            | SecurityException e1) {
                          LOGGER.error("Can't create datatype"
                              + pd.type().getSimpleName(), e1);
                          return null;
                        }
                      }).filter(Objects::nonNull)
                      .filter(DataType.class::isInstance)
                      .map(DataType.class::cast)
                      .forEach(outputPort.getDataType()::add);
                  task.getOutputPort().add(outputPort);
                }));
  }

}
