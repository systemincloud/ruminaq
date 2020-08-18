/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.gui.features.custom;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
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
import org.ruminaq.model.ruminaq.InternalPort;
import org.ruminaq.tasks.debug.ui.InternalPortBreakpoint;
import org.ruminaq.util.EclipseUtil;

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
    IResource resource = EclipseUtil.emfResourceToIResource(
        getFeatureProvider().getDiagramTypeProvider().getDiagram().eResource());
    Object bo = getFeatureProvider().getBusinessObjectForPictogramElement(
        ((ICustomContext) context).getPictogramElements()[0]);

    try {
      if (bo != null && bo instanceof InternalPort) {
        InternalPort ip = (InternalPort) bo;
        IBreakpoint[] breakpoints = DebugPlugin.getDefault()
            .getBreakpointManager().getBreakpoints(InternalPortBreakpoint.ID);
        for (int i = 0; i < breakpoints.length; i++) {
          IBreakpoint breakpoint = breakpoints[i];
          if (resource.equals(breakpoint.getMarker().getResource())) {
            if (breakpoint.getMarker()
                .getAttribute(InternalPortBreakpoint.TASK_ID)
                .equals(ip.getTask().getId())
                && breakpoint.getMarker()
                    .getAttribute(InternalPortBreakpoint.PORT_ID)
                    .equals(ip.getId())) {
              return true;
            }
          }
        }
      }
    } catch (CoreException e) {
    }
    return false;
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
    IResource resource = EclipseUtil.emfResourceToIResource(
        fp.getDiagramTypeProvider().getDiagram().eResource());
    Object bo = fp.getBusinessObjectForPictogramElement(
        ((ICustomContext) context).getPictogramElements()[0]);

    try {
      if (bo != null && bo instanceof InternalPort) {
        InternalPort ip = (InternalPort) bo;
        IBreakpoint[] breakpoints = DebugPlugin.getDefault()
            .getBreakpointManager().getBreakpoints(InternalPortBreakpoint.ID);
        for (int i = 0; i < breakpoints.length; i++) {
          final IBreakpoint breakpoint = breakpoints[i];
          if (resource.equals(breakpoint.getMarker().getResource())) {
            if (breakpoint.getMarker()
                .getAttribute(InternalPortBreakpoint.TASK_ID)
                .equals(ip.getTask().getId())
                && breakpoint.getMarker()
                    .getAttribute(InternalPortBreakpoint.PORT_ID)
                    .equals(ip.getId())) {
              PropertyDialogAction action = new PropertyDialogAction(
                  new SameShellProvider(PlatformUI.getWorkbench()
                      .getActiveWorkbenchWindow().getShell()),
                  new ISelectionProvider() {
                    public void addSelectionChangedListener(
                        ISelectionChangedListener listener) {
                    }

                    public ISelection getSelection() {
                      return new StructuredSelection(breakpoint);
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
      }
    } catch (CoreException e) {
    }
  }
}
