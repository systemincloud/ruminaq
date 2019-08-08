package org.ruminaq.gui.features.doubleclick;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IDoubleClickContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.FeaturePredicate;
import org.ruminaq.gui.features.doubleclick.DoubleClickBaseElementFeature.Filter;
import org.ruminaq.model.ruminaq.BaseElement;

@FeatureFilter(Filter.class)
public class DoubleClickBaseElementFeature extends AbstractCustomFeature {

	public static class Filter implements FeaturePredicate<IContext> {
		@Override
		public boolean test(IContext context, IFeatureProvider fp) {
			IDoubleClickContext doubleClickContext = (IDoubleClickContext) context;
			Object bo = null;
			for (Object o : Graphiti.getLinkService()
			    .getAllBusinessObjectsForLinkedPictogramElement(
			        doubleClickContext.getPictogramElements()[0]))
				if (o instanceof BaseElement) {
					bo = o;
					break;
				}

			return bo instanceof BaseElement;
		}
	}

	public DoubleClickBaseElementFeature(IFeatureProvider fp) {
		super(fp);
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
		BaseElement bo = null;
		for (Object o : Graphiti.getLinkService()
		    .getAllBusinessObjectsForLinkedPictogramElement(
		        context.getPictogramElements()[0]))
			if (o instanceof BaseElement) {
				bo = (BaseElement) o;
				break;
			}
		if (bo == null)
			return;

		IWorkbenchWindow window = PlatformUI.getWorkbench()
		    .getActiveWorkbenchWindow();
		if (window != null) {
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
