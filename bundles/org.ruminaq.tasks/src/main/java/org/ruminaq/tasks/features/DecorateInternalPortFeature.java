package org.ruminaq.tasks.features;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.tb.BorderDecorator;
import org.eclipse.graphiti.tb.IBorderDecorator;
import org.eclipse.graphiti.tb.IDecorator;
import org.eclipse.graphiti.tb.ImageDecorator;
import org.eclipse.graphiti.util.IColorConstant;
import org.ruminaq.model.ruminaq.InternalInputPort;
import org.ruminaq.model.ruminaq.InternalPort;
import org.ruminaq.tasks.Images;
import org.ruminaq.tasks.debug.ui.InternalPortBreakpoint;
import org.ruminaq.util.EclipseUtil;
import org.ruminaq.validation.ValidationStatusAdapter;

public class DecorateInternalPortFeature {

	public List<IDecorator> getDecorators(IFeatureProvider fp, PictogramElement pe) {
		List<IDecorator> decorators = new LinkedList<>();

		Object bo = fp.getBusinessObjectForPictogramElement(pe);

		decorators.addAll(validationDecorators(fp, pe, bo));
		decorators.addAll(breakpointDecorators(fp, pe, bo));
		decorators.addAll(debugDecorators(fp, pe, bo));

		return decorators;
	}

	private Collection<? extends IDecorator> validationDecorators(IFeatureProvider fp, PictogramElement pe, Object bo) {
		List<IDecorator> decorators = new LinkedList<>();

		if(bo != null && bo instanceof InternalInputPort && pe instanceof AnchorContainer) {
			AnchorContainer ac = (AnchorContainer) pe;
			if(ac.getAnchors().size() > 0 && ac.getAnchors().get(0).getIncomingConnections().size() > 0) {
				Object boo = fp.getBusinessObjectForPictogramElement(ac.getAnchors().get(0).getIncomingConnections().get(0));

				ValidationStatusAdapter statusAdapter = (ValidationStatusAdapter) EcoreUtil.getRegisteredAdapter((EObject) boo, ValidationStatusAdapter.class);
				if(statusAdapter == null) return decorators;
				final IBorderDecorator decorator;
				final IStatus status = statusAdapter.getValidationStatus();
				switch (status.getSeverity()) {
					case IStatus.INFO:    decorator = new BorderDecorator(IColorConstant.BLUE,   2, 3); break;
				    case IStatus.WARNING: decorator = new BorderDecorator(IColorConstant.YELLOW, 2, 3); break;
				    case IStatus.ERROR:   decorator = new BorderDecorator(IColorConstant.RED,    2, 3); break;
				    default:              decorator = null;                                             break;
				}

				if(decorator != null) {
				    decorator.setMessage(status.getMessage());
				    decorators.add(decorator);
				}
			}
		}

		return decorators;
	}

	private Collection<? extends IDecorator> breakpointDecorators(IFeatureProvider fp, PictogramElement pe, Object bo) {
		List<IDecorator> decorators = new LinkedList<>();

		IResource resource = EclipseUtil.emfResourceToIResource(fp.getDiagramTypeProvider().getDiagram().eResource());
		try {
			if(bo != null && bo instanceof InternalPort) {
				InternalPort ip = (InternalPort) bo;
				IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager().getBreakpoints(InternalPortBreakpoint.ID);
				for(int i = 0; i < breakpoints.length; i++) {
					IBreakpoint breakpoint = breakpoints[i];
					if(resource.equals(breakpoint.getMarker().getResource())) {
						if(breakpoint.getMarker().getAttribute(InternalPortBreakpoint.TASK_ID).equals(ip.getParent().getId()) &&
						   breakpoint.getMarker().getAttribute(InternalPortBreakpoint.PORT_ID).equals(ip.getId())) {
							ImageDecorator bp = breakpoint.isEnabled() ? new ImageDecorator(Images.K.IMG_TOGGLE_BREAKPOINT_S.name()) :
								                                         new ImageDecorator(Images.K.IMG_TOGGLE_BREAKPOINT_D.name());
							bp.setX(1);
							bp.setY(1);
							decorators.add(bp);
						}
					}
				}
			}
		} catch (CoreException e) { }

		return decorators;
	}

	private Collection<? extends IDecorator> debugDecorators(IFeatureProvider fp, PictogramElement pe, Object bo) {
		List<IDecorator> decorators = new LinkedList<>();

		return decorators;
	}

}
