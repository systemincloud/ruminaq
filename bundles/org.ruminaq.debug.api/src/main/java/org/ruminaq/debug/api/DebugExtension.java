/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.debug.api;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.resources.IProject;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.ruminaq.debug.api.dispatcher.EventDispatchJob;

/**
 *
 * @author Marek Jagielski
 */
public interface DebugExtension {
  default Collection<? extends IDebugTarget> getDebugTargets(ILaunch launch,
      IProject project, EventDispatchJob dispatcher) {
    return Collections.emptyList();
  }
}
