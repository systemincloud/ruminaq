/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.resize;

import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.ruminaq.gui.features.AbstractFeatureFilter;

/**
 * Inheriting classes can be used in @FeatureFilter annotations. Used on
 * implementations of AbstractUpdateElementFeature.
 *
 * @author Marek Jagielski
 */
public abstract class AbstractResizeFeatureFilter
    extends AbstractFeatureFilter<IResizeShapeContext> {

  public static final GetPictogramElement getPictogramElement = ctx -> ((IResizeShapeContext) ctx)
      .getPictogramElement();

  public AbstractResizeFeatureFilter() {
    super(IResizeShapeContext.class, getPictogramElement);
  }

}
