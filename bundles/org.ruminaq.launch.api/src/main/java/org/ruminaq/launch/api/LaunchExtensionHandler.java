/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.launch.api;

import java.util.Collection;
import java.util.LinkedHashSet;

import org.eclipse.core.resources.IProject;

/**
 *
 * @author Marek Jagielski
 */
public interface LaunchExtensionHandler {

  Collection<String> getPluginIdsToRunnerClasspath();

  String getVMArguments();

  LinkedHashSet<String> getProgramArguments(IProject p);
}
