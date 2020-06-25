package org.ruminaq.tasks.javatask.gui.api;

import java.util.Collection;

import org.ruminaq.tasks.javatask.client.data.Data;

/**
 *
 * @author Marek Jagielski
 */
public interface JavaTaskExtensionHandler {

  Collection<? extends Class<? extends Data>> getJavaTaskDatas();

}
