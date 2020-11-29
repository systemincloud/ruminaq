/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.prefs;

import java.util.Hashtable;

import org.eclipse.core.resources.IProject;

/**
 * Project properties.
 *
 * @author Marek Jagielski
 */
public final class ProjectProps extends AbstractProps {

  public static final String PROJECT_PROPS = "org.ruminaq.project";

  public static final String RUMINAQ_VERSION = "ruminaq.version";

  private static Hashtable<IProject, AbstractProps> instances = new Hashtable<>();

  private ProjectProps(IProject project) {
    super(project, PROJECT_PROPS, false);
  }

  public static AbstractProps getInstance(IProject project) {
    if (!instances.containsKey(project)) {
      instances.put(project, new ProjectProps(project));
    }
    return instances.get(project);
  }
}
