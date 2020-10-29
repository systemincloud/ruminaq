/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.directediting;

import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.ruminaq.gui.features.AbstractFeatureFilter;

/**
 * Inheriting classes can be used in @FeatureFilter annotations. Used on
 * implementations of AbstractDirectEditingFeature.
 *
 * @author Marek Jagielski
 */
public abstract class AbstractDirectEditingFeatureFilter
    extends AbstractFeatureFilter<IDirectEditingContext> {

  public static final GetPictogramElement getPictogramElement = ctx -> ((IDirectEditingContext) ctx)
      .getPictogramElement();

  protected AbstractDirectEditingFeatureFilter() {
    super(IDirectEditingContext.class, getPictogramElement);
  }

}
