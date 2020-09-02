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
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.FeaturePredicate;
import org.ruminaq.gui.features.custom.InternalPortToggleBreakpointFeature;
import org.ruminaq.gui.features.doubleclick.InternalPortDoubleClickFeature.Filter;
import org.ruminaq.gui.model.diagram.InternalPortShape;

@FeatureFilter(Filter.class)
public class InternalPortDoubleClickFeature
    extends DoubleClickBaseElementFeature {

  public static class Filter implements FeaturePredicate<IContext> {
    @Override
    public boolean test(IContext context, IFeatureProvider fp) {
      return Optional.of(context).filter(IDoubleClickContext.class::isInstance)
          .map(IDoubleClickContext.class::cast)
          .map(IDoubleClickContext::getPictogramElements).map(Stream::of)
          .orElseGet(Stream::empty).findFirst().map(Object::getClass)
          .filter(InternalPortShape.class::isAssignableFrom)
          .isPresent();
    }
  }

  public InternalPortDoubleClickFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public void execute(ICustomContext context) {
    InternalPortToggleBreakpointFeature.doExecute(context,
        getFeatureProvider());
    super.execute(context);
  }
}
