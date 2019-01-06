package org.ruminaq.tasks.api;

import java.util.List;

public interface TasksUiManagerHandler {

	List<ITaskUiApi> getTasks(String prefix);

}
