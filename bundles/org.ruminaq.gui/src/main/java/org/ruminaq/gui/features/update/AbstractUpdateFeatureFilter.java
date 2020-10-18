/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.update;

import org.eclipse.graphiti.features.context.IUpdateContext;
import org.ruminaq.gui.features.AbstractFeatureFilter;

/**
 * Inheriting classes can be used in @FeatureFilter annotations. Used on
 * implementations of AbstractUpdateElementFeature.
 *
 * @author Marek Jagielski
 */
public abstract class AbstractUpdateFeatureFilter
    extends AbstractFeatureFilter<IUpdateContext> {

  public static final GetPictogramElement getPictogramElement = ctx -> ((IUpdateContext) ctx)
      .getPictogramElement();

  protected AbstractUpdateFeatureFilter() {
    super(IUpdateContext.class, getPictogramElement);
  }

}
