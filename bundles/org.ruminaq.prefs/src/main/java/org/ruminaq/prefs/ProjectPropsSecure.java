/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.prefs;

import java.util.Hashtable;

import org.eclipse.core.resources.IProject;

public class ProjectPropsSecure extends Props {

  public static final String PROJECT_SECURE = "org.ruminaq.project.secure";

  private static Hashtable<IProject, Props> instances = new Hashtable<>();

  public static Props getInstance(IProject project) {
    if (!instances.containsKey(project)) {
      instances.put(project, new ProjectPropsSecure(project));
    }
    return instances.get(project);
  }

  private ProjectPropsSecure(IProject project) {
    super(project, PROJECT_SECURE, true);
  }
}
