/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.prefs;

import java.util.Hashtable;

import org.eclipse.core.resources.IProject;

public class ProjectProps extends Props {

  public static final String PROJECT_PROPS = "org.ruminaq.project";

  public static final String RUMINAQ_VERSION = "ruminaq.version";

  private static Hashtable<IProject, Props> instances = new Hashtable<>();

  public static Props getInstance(IProject project) {
    if (!instances.containsKey(project)) {
      instances.put(project, new ProjectProps(project));
    }
    return instances.get(project);
  }

  private ProjectProps(IProject project) {
    super(project, PROJECT_PROPS, false);
  }
}
