/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.create;

import java.lang.reflect.Field;
import java.util.AbstractMap.SimpleEntry;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.ruminaq.gui.model.diagram.impl.TasksUtil;
import org.ruminaq.logs.ModelerLoggerFactory;
import org.ruminaq.model.ruminaq.PortInfo;
import org.ruminaq.model.ruminaq.PortType;
import org.ruminaq.model.ruminaq.PortsDescr;
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

  protected AbstractCreateTaskFeature(IFeatureProvider fp,
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

  /**
   * Metadata of ports.
   *
   * @return class containing metadata of ports.
   */
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
        .map(se -> new SimpleEntry<>(se.getKey(), se.getValue().n())).forEach(
            (SimpleEntry<Field, Integer> e) -> IntStream.range(0, e.getValue())
                .forEach(i -> TasksUtil.createInputPort(task, e.getKey())));
  }

  private static void addDefaultOutputPorts(Task task,
      Supplier<Stream<Field>> fields) {
    fields.get().map(f -> new SimpleEntry<>(f, f.getAnnotation(PortInfo.class)))
        .filter(se -> se.getValue() != null)
        .filter(se -> PortType.OUT == se.getValue().portType())
        .filter(se -> !se.getValue().opt())
        .map(se -> new SimpleEntry<>(se.getKey(), se.getValue().n())).forEach(
            (SimpleEntry<Field, Integer> e) -> IntStream.range(0, e.getValue())
                .forEach(i -> TasksUtil.createOutputPort(task, e.getKey())));
  }
}
