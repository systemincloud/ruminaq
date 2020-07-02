/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.console.gui;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IDoubleClickContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.modeler.tasks.console.ui.views.ConsoleViewPart;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.DoubleClickFeatureExtension;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.FeaturePredicate;
import org.ruminaq.gui.model.diagram.RuminaqShape;
import org.ruminaq.tasks.AbstractTaskViewPart;
import org.ruminaq.tasks.console.gui.DoubleClickFeatureImpl.DoubleClickFeature.Filter;
import org.ruminaq.tasks.console.model.console.Console;

@Component(property = { "service.ranking:Integer=10" })
public class DoubleClickFeatureImpl implements DoubleClickFeatureExtension {

  @Override
  public List<Class<? extends ICustomFeature>> getFeatures() {
    return Arrays.asList(DoubleClickFeature.class);
  }

  @FeatureFilter(Filter.class)
  public static class DoubleClickFeature extends AbstractCustomFeature {

    public static class Filter implements FeaturePredicate<IContext> {
      @Override
      public boolean test(IContext context) {
        return Optional.of(context)
            .filter(IDoubleClickContext.class::isInstance)
            .map(IDoubleClickContext.class::cast)
            .map(IDoubleClickContext::getPictogramElements).map(Stream::of)
            .orElseGet(Stream::empty).findFirst()
            .filter(RuminaqShape.class::isInstance)
            .map(RuminaqShape.class::cast).map(RuminaqShape::getModelObject)
            .filter(Console.class::isInstance).isPresent();
      }
    }

    public DoubleClickFeature(IFeatureProvider fp) {
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
      Console bo = null;
      for (Object o : Graphiti.getLinkService()
          .getAllBusinessObjectsForLinkedPictogramElement(
              context.getPictogramElements()[0]))
        if (o instanceof Console) {
          bo = (Console) o;
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
            String secondaryId = bo.eResource().getURI().toString().replace(":",
                "#") + "#" + bo.getId();
            page.showView("org.ruminaq.tasks.console.views.ConsoleView",
                secondaryId, IWorkbenchPage.VIEW_ACTIVATE);
            AbstractTaskViewPart cv = ((AbstractTaskViewPart) page
                .getActivePart());
            cv.init(bo, ConsoleViewPart.class,
                getDiagramBehavior().getEditingDomain());
          } catch (PartInitException e) {
            e.printStackTrace();
          }
        }
      }
    }
  }
}
