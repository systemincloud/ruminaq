/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.doubleclick;

import java.util.Optional;
import java.util.stream.Stream;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IDoubleClickContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.FeaturePredicate;
import org.ruminaq.gui.model.diagram.RuminaqShape;
import org.ruminaq.util.Result;

/**
 * BaseElement DoubleClick feature.
 *
 * @author Marek Jagielski
 */
@FeatureFilter(DoubleClickBaseElementFeature.Filter.class)
public class DoubleClickBaseElementFeature extends AbstractCustomFeature {

  private static class Filter implements FeaturePredicate<IContext> {
    @Override
    public boolean test(IContext context, IFeatureProvider fp) {
      return Optional.of(context).filter(IDoubleClickContext.class::isInstance)
          .map(IDoubleClickContext.class::cast)
          .map(IDoubleClickContext::getPictogramElements).map(Stream::of)
          .orElseGet(Stream::empty).findFirst()
          .filter(RuminaqShape.class::isInstance).isPresent();
    }
  }

  public DoubleClickBaseElementFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canExecute(ICustomContext context) {
    return true;
  }

  /**
   * No model changes.
   */
  @Override
  public boolean hasDoneChanges() {
    return false;
  }

  @Override
  public void execute(ICustomContext context) {
    Optional.ofNullable(PlatformUI.getWorkbench().getActiveWorkbenchWindow())
        .map(IWorkbenchWindow::getActivePage).ifPresent(
            p -> Result.attempt(() -> p.showView(IPageLayout.ID_PROP_SHEET)));
  }
}
