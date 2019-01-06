package org.ruminaq.tasks.javatask.extension;

import java.util.List;

import org.ruminaq.tasks.javatask.client.data.Data;

public interface JavaTaskExtension {
	List<Class<? extends Data>> getJavaTaskDatas();
}
