package org.ruminaq.tasks.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IDoubleClickContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.ruminaq.model.model.ruminaq.InternalPort;

public class InternalPortDoubleClickFeature extends AbstractCustomFeature {

	public InternalPortDoubleClickFeature(IDoubleClickContext context, InternalPort ip, IFeatureProvider fp) {
		super(fp);
	}

    @Override public boolean canExecute    (ICustomContext context) { return true; }
	@Override public boolean hasDoneChanges()                       { return false; }

	@Override
	public void execute(ICustomContext context) {
		InternalPortToggleBreakpointFeature.doExecute(context, getFeatureProvider());

		InternalPort bo = null;
        for(Object o : Graphiti.getLinkService().getAllBusinessObjectsForLinkedPictogramElement(context.getPictogramElements()[0]))
            if(o instanceof InternalPort) { bo = (InternalPort) o; break; }
        if(bo == null) return;

        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if(window != null) {
            IWorkbenchPage page = window.getActivePage();
            if (page != null) {
                try {
                    page.showView(IPageLayout.ID_PROP_SHEET);
                } catch (PartInitException e) {
                    e.printStackTrace();
                }
            }
        }
	}
}
