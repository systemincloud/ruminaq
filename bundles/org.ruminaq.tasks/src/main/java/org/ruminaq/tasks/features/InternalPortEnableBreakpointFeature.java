package org.ruminaq.tasks.features;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.ruminaq.model.ruminaq.InternalPort;
import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.tasks.debug.ui.InternalPortBreakpoint;
import org.ruminaq.util.EclipseUtil;

public class InternalPortEnableBreakpointFeature extends AbstractCustomFeature {

	public static final String NAME = "Enable Breakpoint";

	public InternalPortEnableBreakpointFeature(IFeatureProvider fp) {
        super(fp);
    }

    @Override public String getName() { return NAME; }

    @Override public boolean isAvailable(IContext context) {
		IResource resource = EclipseUtil.emfResourceToIResource(getFeatureProvider().getDiagramTypeProvider().getDiagram().eResource());
		Object bo = getFeatureProvider().getBusinessObjectForPictogramElement(((ICustomContext) context).getPictogramElements()[0]);

		try {
			if(bo != null && bo instanceof InternalPort) {
				InternalPort ip = (InternalPort) bo;
				IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager().getBreakpoints(InternalPortBreakpoint.ID);
				for(int i = 0; i < breakpoints.length; i++) {
					IBreakpoint breakpoint = breakpoints[i];
					if(resource.equals(breakpoint.getMarker().getResource())) {
						if(breakpoint.getMarker().getAttribute(InternalPortBreakpoint.TASK_ID).equals(ip.getParent().getId()) &&
						   breakpoint.getMarker().getAttribute(InternalPortBreakpoint.PORT_ID).equals(ip.getId())) {
							return !breakpoint.isEnabled();
						}
					}
				}
			}
		} catch (CoreException e) { }
    	return false;
    }

	@Override public boolean canExecute    (ICustomContext context) { return true; }
	@Override public boolean hasDoneChanges()                       { return false; }

	@Override
	public void execute(ICustomContext context) {
		doExecute(context, getFeatureProvider());
	}

	public static void doExecute(ICustomContext context, IFeatureProvider fp) {
		IResource resource = EclipseUtil.emfResourceToIResource(fp.getDiagramTypeProvider().getDiagram().eResource());
		Object bo = fp.getBusinessObjectForPictogramElement(context.getPictogramElements()[0]);

		try {
			if(bo != null && bo instanceof InternalPort) {
				InternalPort ip = (InternalPort) bo;
				IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager().getBreakpoints(InternalPortBreakpoint.ID);
				for(int i = 0; i < breakpoints.length; i++) {
					IBreakpoint breakpoint = breakpoints[i];
					if(resource.equals(breakpoint.getMarker().getResource())) {
						if(breakpoint.getMarker().getAttribute(Task.class.getSimpleName()).equals(ip.getParent().getId()) &&
						   breakpoint.getMarker().getAttribute(InternalPort.class.getSimpleName()).equals(ip.getId())) {
							breakpoint.setEnabled(true);
						}
					}
				}
			}
		} catch (CoreException e) { }
	}
}
