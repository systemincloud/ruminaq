/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.prefs;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import org.eclipse.core.resources.IProject;

/**
 * Project properties.
 *
 * @author Marek Jagielski
 */
public final class ProjectProps extends AbstractProps {

  public static final String PROJECT_PROPS = "org.ruminaq.project";

  public static final String RUMINAQ_VERSION = "ruminaq.version";

  private static Map<IProject, AbstractProps> instances = new WeakHashMap<>();

  private ProjectProps(IProject project) {
    super(project, PROJECT_PROPS, false);
  }

  /**
   * Get project properties.
   *
   * @param project eclipse project
   * @return properties wrapper
   */
  public static AbstractProps getInstance(IProject project) {
    instances.putIfAbsent(project, new ProjectProps(project));
    return instances.get(project);
  }
}
