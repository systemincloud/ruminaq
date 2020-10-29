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
import org.ruminaq.gui.model.diagram.RuminaqShape;

/**
 * UserDefinedTask DoubleClickFeature.
 *
 * @author Marek Jagielski
 */
public abstract class AbstractUserDefinedTaskDoubleClickFeature
    extends AbstractCustomFeature {

  protected AbstractUserDefinedTaskDoubleClickFeature(IFeatureProvider fp) {
    super(fp);
  }

  protected static <T> Optional<T> toModel(IContext context, Class<T> type) {
    return Optional.of(context).filter(IDoubleClickContext.class::isInstance)
        .map(IDoubleClickContext.class::cast)
        .map(IDoubleClickContext::getPictogramElements).map(Stream::of)
        .orElseGet(Stream::empty).findFirst()
        .filter(RuminaqShape.class::isInstance).map(RuminaqShape.class::cast)
        .map(RuminaqShape::getModelObject).filter(type::isInstance)
        .map(type::cast);
  }

  @Override
  public boolean canExecute(ICustomContext context) {
    return true;
  }

  @Override
  public boolean hasDoneChanges() {
    return false;
  }
}
