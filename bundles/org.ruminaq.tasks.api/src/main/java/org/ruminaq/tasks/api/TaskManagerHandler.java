package org.ruminaq.tasks.api;

import java.util.Collection;

import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.swt.widgets.Composite;
import org.ruminaq.model.ruminaq.Task;

public interface TaskManagerHandler {

	Collection<ITaskApi> getProjectVersionTasks();

	Collection<ITaskApi> getTasks();

	void addToGeneralTab(Composite composite, final Task task, final TransactionalEditingDomain ed);

}
