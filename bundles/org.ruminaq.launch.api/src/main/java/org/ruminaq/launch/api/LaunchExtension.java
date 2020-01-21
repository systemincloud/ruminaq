/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.launch.api;

import java.util.Collection;
import java.util.Collections;
import java.util.NavigableSet;

import org.eclipse.core.resources.IProject;

/**
 *
 * @author Marek Jagielski
 */
public interface LaunchExtension {

  static final String RUNNER_LOG_LEVEL_PREF = "runner.log.level";

  default Collection<String> getPluginIdsToRunnerClasspath() {
    return Collections.emptyList();
  }

  default String getVMArguments() {
    return "";
  }

  default NavigableSet<String> getProgramArguments(IProject p) {
    return Collections.emptyNavigableSet();
  }
}
