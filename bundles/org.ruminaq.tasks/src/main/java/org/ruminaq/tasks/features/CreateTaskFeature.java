package org.ruminaq.tasks.features;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.emf.ecore.EFactory;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.ruminaq.gui.features.create.CreateElementFeature;
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

public abstract class CreateTaskFeature extends CreateElementFeature {

  private final Logger logger = ModelerLoggerFactory
      .getLogger(CreateTaskFeature.class);

  public CreateTaskFeature(IFeatureProvider fp, Class<? extends Task> clazz) {
    super(fp, clazz);
  }

  protected Object[] create(ICreateContext context, Task task) {
    logger.trace("{}", task.getClass().getSimpleName());
//		task.setBundleName(bundleName);
//		task.setVersion(version.getMajor() + "." + version.getMinor() + "."
//		    + version.getMicro());

    setDefaultId(task, context);
    addDefaultPorts(task);

    getRuminaqDiagram().getMainTask().getTask().add(task);

    addGraphicalRepresentation(context, task);
    return new Object[] { task };
  }

  protected abstract Class<? extends PortsDescr> getPortsDescription();

  private void addDefaultPorts(Task task) {
    Class<? extends PortsDescr> pd = getPortsDescription();
    if (pd == null)
      return;
    for (Field f : pd.getFields()) {
      IN in = f.getAnnotation(IN.class);
      if (in != null) {
        if (in.opt())
          continue;
        for (int i = 0; i < in.n(); i++) {
          InternalInputPort inputPort = RuminaqFactory.eINSTANCE
              .createInternalInputPort();
          String id = in.name();
          inputPort.setParent(task);
          if (in.n() > 1)
            id += " " + i;
          inputPort.setId(id);
          inputPort.setAsynchronous(in.asynchronous());
          int group = in.group();
          if (in.n() > 1) {
            if (in.ngroup().equals(NGroup.SAME))
              inputPort.setGroup(group);
            else {
              if (group == -1)
                inputPort.setGroup(group);
              else {
                int j = group;
                boolean free = true;
                do {
                  free = true;
                  for (InternalInputPort iip : task.getInputPort())
                    if (iip.getGroup() == j)
                      free = false;
                  j++;
                } while (!free);
                inputPort.setGroup(j - 1);
              }
            }
          } else
            inputPort.setGroup(group);

          inputPort.setDefaultHoldLast(in.hold());
          inputPort.setHoldLast(in.hold());
          inputPort.setDefaultQueueSize(in.queue());
          inputPort.setQueueSize(in.queue());

          for (Class<? extends DataType> dt : in.type())
            try {
              EFactory factory = (EFactory) in.factory()
                  .getDeclaredField("eINSTANCE").get(null);
              Method createMethod = factory.getClass()
                  .getMethod("create" + dt.getSimpleName(), (Class<?>[]) null);
              inputPort.getDataType().add(
                  (DataType) createMethod.invoke(factory, (Object[]) null));
            } catch (SecurityException | NoSuchMethodException
                | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchFieldException e) {
            }

          task.getInputPort().add(inputPort);
        }
      }
      OUT out = f.getAnnotation(OUT.class);
      if (out != null) {
        if (out.opt())
          continue;
        for (int i = 0; i < out.n(); i++) {
          InternalOutputPort outputPort = RuminaqFactory.eINSTANCE
              .createInternalOutputPort();
          String id = out.name();
          outputPort.setParent(task);
          if (out.n() > 1)
            id += " " + i;
          outputPort.setId(id);

          for (Class<? extends DataType> dt : out.type())
            try {
              EFactory factory = (EFactory) out.factory()
                  .getDeclaredField("eINSTANCE").get(null);
              Method createMethod = factory.getClass()
                  .getMethod("create" + dt.getSimpleName(), (Class<?>[]) null);
              outputPort.getDataType().add(
                  (DataType) createMethod.invoke(factory, (Object[]) null));
            } catch (SecurityException | NoSuchMethodException
                | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchFieldException e) {
            }

          task.getOutputPort().add(outputPort);
        }
      }
    }
  }
}
