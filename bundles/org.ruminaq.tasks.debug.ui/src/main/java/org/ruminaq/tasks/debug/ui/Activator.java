/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.debug.ui;

import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractUIPlugin {

  private InternalPortBreakpointWorkbenchAdapterFactory breakpointAdapterFactory;

  @Override
  public void start(BundleContext context) throws Exception {
    IAdapterManager manager = Platform.getAdapterManager();
    breakpointAdapterFactory = new InternalPortBreakpointWorkbenchAdapterFactory();
    manager.registerAdapters(breakpointAdapterFactory,
        InternalPortBreakpoint.class);
  }

  @Override
  public void stop(BundleContext context) throws Exception {
    IAdapterManager manager = Platform.getAdapterManager();
    manager.unregisterAdapters(breakpointAdapterFactory);
  }
}
