/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/


package org.ruminaq.gui.features.custom;

import java.util.Optional;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.ruminaq.util.Result;

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

  @Override
  public boolean isAvailable(IContext context) {
    return InternalPortToggleBreakpointFeature
        .breakpointFromContext(
            Optional.of(context).filter(ICustomContext.class::isInstance)
                .map(ICustomContext.class::cast).orElse(null),
            getFeatureProvider())
        .map(b -> Result.attempt(() -> b.isEnabled()))
        .flatMap(r -> Optional.ofNullable(r.orElse(Boolean.FALSE)))
        .orElse(Boolean.FALSE);
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
    doExecute(context, getFeatureProvider());
  }

  public static void doExecute(ICustomContext context, IFeatureProvider fp) {
    InternalPortToggleBreakpointFeature.breakpointFromContext(context, fp)
        .ifPresent((IBreakpoint b) -> Result.attempt(() -> {
          b.setEnabled(false);
          return true;
        }));
  }
}
