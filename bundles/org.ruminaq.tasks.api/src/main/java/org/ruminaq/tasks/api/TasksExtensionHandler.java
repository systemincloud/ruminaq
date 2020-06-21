/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.tasks.api;

import org.eclipse.core.resources.IProject;
import org.eclipse.debug.core.ILaunch;
import org.osgi.framework.BundleContext;
import org.ruminaq.debug.api.dispatcher.EventDispatchJob;

/**
 *
 * @author Marek Jagielski
 */
public interface TasksExtensionHandler {

  void init(BundleContext ctx);

  Object getDebugTargets(ILaunch launch, IProject project,
      EventDispatchJob dispatcher);
}
