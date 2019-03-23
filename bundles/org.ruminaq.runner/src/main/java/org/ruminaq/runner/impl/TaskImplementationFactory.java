package org.ruminaq.runner.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import org.ruminaq.model.config.CommonConfig;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.model.ruminaq.EmbeddedTask;
import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.runner.RunnerLoggerFactory;
import org.ruminaq.runner.service.RunnerServiceManager;
import org.slf4j.Logger;

public enum TaskImplementationFactory {

	INSTANCE;

	private final Logger logger = RunnerLoggerFactory.getLogger(TaskImplementationFactory.class);

	public TaskI getImplementation(EmbeddedTaskI parent, Task task, String basePath, boolean inCloud, boolean runOnlyLocalTasks) {
		logger.trace("Creating Task {}", task.getId());
		TaskI taskI = RunnerServiceManager.INSTANCE.getImplementation(parent, task);
		if(taskI != null) return taskI;
		else if(task instanceof EmbeddedTask) return new EmbeddedTaskI(parent, task, basePath + ((EmbeddedTask)task).getImplementationTask(), ((EmbeddedTask)task).getParameters(), inCloud, runOnlyLocalTasks);
		else {
			List<Class<? extends BaseElement>> list = CommonConfig.getInstance().getAllClasses();
			if(list.containsAll(Arrays.asList(task.getClass().getGenericInterfaces()))) {
				String name = task.getClass().getSimpleName();
				if(name.endsWith("Impl")) name = name.substring(0, name.length() - 4);
				Class<?> clazz;
				try { clazz = Class.forName(CommonImplementation.class.getPackage().getName() + "."  + name + "I");
				} catch (ClassNotFoundException e) { return null; }

				if(TaskI.class.isAssignableFrom(clazz)) {
					try {
					    return (TaskI) clazz.getConstructor(EmbeddedTaskI.class, Task.class).newInstance(parent, task);
					} catch (InvocationTargetException | InstantiationException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException e) {
						e.printStackTrace();
					}
				}
			}
		}

		return null;
	}

}
