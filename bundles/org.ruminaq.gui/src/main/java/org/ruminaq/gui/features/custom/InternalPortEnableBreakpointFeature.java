/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.custom;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.ruminaq.util.Result;

/**
 * InternalPortEnableBreakpoint toggle.
 *
 * @author Marek Jagielski
 */
public class InternalPortEnableBreakpointFeature
    extends InternalPortDisableBreakpointFeature {

  public static final String NAME = "Enable Breakpoint";

  public InternalPortEnableBreakpointFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  protected boolean isAvailable(IBreakpoint breakpoint) throws CoreException {
    return !breakpoint.isEnabled();
  }

  @Override
  public void execute(ICustomContext context) {
    InternalPortToggleBreakpointFeature
        .breakpointFromContext(context, getFeatureProvider())
        .ifPresent((IBreakpoint b) -> Result.attempt(() -> {
          b.setEnabled(true);
          return Boolean.TRUE;
        }));
  }
}
