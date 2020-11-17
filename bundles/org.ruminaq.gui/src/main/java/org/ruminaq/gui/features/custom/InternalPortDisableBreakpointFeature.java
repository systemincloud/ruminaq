/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.custom;

import java.util.Optional;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.ruminaq.util.Result;
import org.ruminaq.util.Try;

/**
 * InternalPortDisableBreakpoint toggle.
 *
 * @author Marek Jagielski
 */
public class InternalPortDisableBreakpointFeature
    extends AbstractCustomFeature {

  public static final String NAME = "Disable Breakpoint";

  public InternalPortDisableBreakpointFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public String getName() {
    return NAME;
  }

  /**
   * Is available when there is a breakpoint and is enabled.
   */
  @Override
  public boolean isAvailable(IContext context) {
    return InternalPortToggleBreakpointFeature
        .breakpointFromContext(
            Optional.of(context).filter(ICustomContext.class::isInstance)
                .map(ICustomContext.class::cast).orElse(null),
            getFeatureProvider())
        .map(b -> Result.attempt(() -> isAvailable(b)))
        .map(r -> r.orElse(Boolean.FALSE)).orElse(Boolean.FALSE);
  }

  protected boolean isAvailable(IBreakpoint breakpoint) throws CoreException {
    return breakpoint.isEnabled();
  }

  @Override
  public boolean canExecute(ICustomContext context) {
    return true;
  }

  @Override
  public boolean hasDoneChanges() {
    return false;
  }

  @Override
  public void execute(ICustomContext context) {
    InternalPortToggleBreakpointFeature
        .breakpointFromContext(context, getFeatureProvider())
        .ifPresent(b -> Try.check(() -> b.setEnabled(false)));
  }
}
