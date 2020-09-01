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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.SameShellProvider;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PropertyDialogAction;

public class InternalPortBreakpointPropertiesFeature
    extends AbstractCustomFeature {

  public static final String NAME = "Breakpoint Properties...";

  public InternalPortBreakpointPropertiesFeature(IFeatureProvider fp) {
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
        .isPresent();
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
    Optional<IBreakpoint> bp = InternalPortToggleBreakpointFeature
        .breakpointFromContext(
            Optional.of(context).filter(ICustomContext.class::isInstance)
                .map(ICustomContext.class::cast).orElse(null),
            fp);

    if (bp.isPresent()) {
      PropertyDialogAction action = new PropertyDialogAction(
          new SameShellProvider(
              PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()),
          new ISelectionProvider() {
            public void addSelectionChangedListener(
                ISelectionChangedListener listener) {
            }

            public ISelection getSelection() {
              return new StructuredSelection(bp.get());
            }

            public void removeSelectionChangedListener(
                ISelectionChangedListener listener) {
            }

            public void setSelection(ISelection selection) {
            }
          });
      action.run();
    }
  }
}
