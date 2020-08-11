/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.model.diagram.impl;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EPackage;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.model.ruminaq.DataType;
import org.ruminaq.model.ruminaq.InternalInputPort;
import org.ruminaq.model.ruminaq.InternalOutputPort;
import org.ruminaq.model.ruminaq.NGroup;
import org.ruminaq.model.ruminaq.PortData;
import org.ruminaq.model.ruminaq.PortInfo;
import org.ruminaq.model.ruminaq.RuminaqFactory;
import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.util.Result;

public class TasksUtil {

  public static void createInputPort(Task task, Field field) {
    InternalInputPort inputPort = RuminaqFactory.eINSTANCE
        .createInternalInputPort();
    PortInfo pi = field.getAnnotation(PortInfo.class);

    inputPort.setId(Optional.of(pi).filter(p -> p.n() > 1 || p.n() == -1)
        .map(p -> String.format("%s %d", p.id(),
            task.getMutlipleInternalInputPorts(p.id()).size() + 1))
        .orElseGet(pi::id));
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
        }).orElseGet(pi::group);
  }

  public static void createOutputPort(Task task, Field field) {
    InternalOutputPort outputPort = RuminaqFactory.eINSTANCE
        .createInternalOutputPort();
    PortInfo pi = field.getAnnotation(PortInfo.class);

    outputPort.setId(Optional.of(pi).filter(p -> p.n() > 1 || p.n() == -1)
        .map(p -> String.format("%s %d", p.id(),
            task.getMutlipleInternalOutputPorts(p.id()).size() + 1))
        .orElseGet(pi::id));
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

  public static boolean isMultiplePortId(String id, String prefix) {
    return id.matches(prefix + " [1-9][0-9]*");
  }

  public static boolean isMultiplePortId(String id, PortsDescr pd) {
    return isMultiplePortId(id, pd.getId());
  }

  public static int getMultiplePortIdIdx(String id, PortsDescr pd) {
    return Integer.parseInt(id.substring(pd.getId().length() + 1));
  }

}
