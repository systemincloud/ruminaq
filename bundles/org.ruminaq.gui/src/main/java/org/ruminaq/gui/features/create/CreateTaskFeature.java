/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.create;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap.SimpleEntry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.eclipse.emf.ecore.EFactory;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.Version;
import org.ruminaq.logs.ModelerLoggerFactory;
import org.ruminaq.model.desc.IN;
import org.ruminaq.model.desc.NGroup;
import org.ruminaq.model.desc.OUT;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.model.ruminaq.DataType;
import org.ruminaq.model.ruminaq.InternalInputPort;
import org.ruminaq.model.ruminaq.InternalOutputPort;
import org.ruminaq.model.ruminaq.RuminaqFactory;
import org.ruminaq.model.ruminaq.Task;
import org.slf4j.Logger;

/**
 * Common class for all CreateFeautes for Tasks.
 * 
 * @author Marek Jagielski
 */
public abstract class CreateTaskFeature extends AbstractCreateElementFeature {

  private static final Logger LOGGER = ModelerLoggerFactory
      .getLogger(CreateTaskFeature.class);

  public CreateTaskFeature(IFeatureProvider fp, Class<? extends Task> clazz) {
    super(fp, clazz);
  }

  protected Object[] create(ICreateContext context, Task task) {
    LOGGER.trace("{}", task.getClass().getSimpleName());
    Bundle taskBundle = FrameworkUtil.getBundle(task.getClass());
    task.setBundleName(taskBundle.getSymbolicName());
    Version bundleVersion = taskBundle.getVersion();
    task.setVersion(String.format("%d.%d.%d", bundleVersion.getMajor(),
        bundleVersion.getMinor(), bundleVersion.getMicro()));
    setDefaultId(task, context);

    addDefaultPorts(task);

    getRuminaqDiagram().getMainTask().getTask().add(task);

    addGraphicalRepresentation(context, task);
    return new Object[] { task };
  }

  protected abstract Class<? extends PortsDescr> getPortsDescription();

  private void addDefaultPorts(Task task) {
    Optional<Class<? extends PortsDescr>> pdOpt = Optional
        .ofNullable(getPortsDescription());
    Supplier<Stream<Field>> fields = () -> pdOpt.map(pd -> pd.getFields())
        .map(Stream::of).orElseGet(Stream::empty);

    fields.get().map(f -> f.getAnnotation(IN.class)).filter(Objects::nonNull)
        .filter(Predicate.not(IN::opt)).map(i -> new SimpleEntry<>(i, i.n()))
        .forEach((SimpleEntry<IN, Integer> e) -> {
          IntStream.range(0, e.getValue()).forEach((int i) -> {
            InternalInputPort inputPort = RuminaqFactory.eINSTANCE
                .createInternalInputPort();
            String id = e.getKey().name();
            inputPort.setParent(task);
            if (e.getKey().n() > 1) {
              id += " " + i;
            }
            inputPort.setId(id);
            inputPort.setAsynchronous(e.getKey().asynchronous());
            int group = e.getKey().group();
            if (e.getKey().n() > 1) {
              if (e.getKey().ngroup().equals(NGroup.SAME)) {
                inputPort.setGroup(group);
              } else {
                if (group == -1) {
                  inputPort.setGroup(group);
                } else {
                  Integer j = group;
                  boolean free = true;
                  do {
                    free = task.getInputPort().stream()
                        .map(InternalInputPort::getGroup).noneMatch(j::equals);
                    j++;
                  } while (!free);
                  inputPort.setGroup(j - 1);
                }
              }
            } else {
              inputPort.setGroup(group);
            }

            inputPort.setDefaultHoldLast(e.getKey().hold());
            inputPort.setHoldLast(e.getKey().hold());
            inputPort.setDefaultQueueSize(e.getKey().queue());
            inputPort.setQueueSize(e.getKey().queue());

            for (Class<? extends DataType> dt : e.getKey().type()) {
              try {
                EFactory factory = (EFactory) e.getKey().factory()
                    .getDeclaredField("eINSTANCE").get(null);
                Method createMethod = factory.getClass().getMethod(
                    "create" + dt.getSimpleName(), (Class<?>[]) null);
                inputPort.getDataType().add(
                    (DataType) createMethod.invoke(factory, (Object[]) null));
              } catch (SecurityException | NoSuchMethodException
                  | IllegalAccessException | IllegalArgumentException
                  | InvocationTargetException | NoSuchFieldException ex) {
              }
            }

            task.getInputPort().add(inputPort);
          });
        });

    fields.get().map(f -> f.getAnnotation(OUT.class)).filter(Objects::nonNull)
        .filter(Predicate.not(OUT::opt)).map(i -> new SimpleEntry<>(i, i.n()))
        .forEach((SimpleEntry<OUT, Integer> e) -> {
          IntStream.range(0, e.getValue()).forEach((int i) -> {
            InternalOutputPort outputPort = RuminaqFactory.eINSTANCE
                .createInternalOutputPort();
            String id = e.getKey().name();
            outputPort.setParent(task);
            if (e.getKey().n() > 1) {
              id += " " + i;
            }
            outputPort.setId(id);
            for (Class<? extends DataType> dt : e.getKey().type()) {
              try {
                EFactory factory = (EFactory) e.getKey().factory()
                    .getDeclaredField("eINSTANCE").get(null);
                Method createMethod = factory.getClass().getMethod(
                    "create" + dt.getSimpleName(), (Class<?>[]) null);
                outputPort.getDataType().add(
                    (DataType) createMethod.invoke(factory, (Object[]) null));
              } catch (SecurityException | NoSuchMethodException
                  | IllegalAccessException | IllegalArgumentException
                  | InvocationTargetException | NoSuchFieldException ex) {
              }
            }

            task.getOutputPort().add(outputPort);
          });
        });
  }
}
