/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.add;

import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.IContext;
import org.ruminaq.gui.features.FeaturePredicate;
import org.ruminaq.model.ruminaq.BaseElement;

/**
 * Inheriting classess can be used in @FeatureFilter
 * annotations. Used on implmentations of AbstractAddElementFeature.
 *
 * @author Marek Jagielski
 */
public abstract class AbstractAddFeatureFilter implements FeaturePredicate<IContext> {

  @Override
  public boolean test(IContext context) {
    IAddContext addContext = (IAddContext) context;
    return forBusinessObject()
        .isAssignableFrom(addContext.getNewObject().getClass());
  }

  public abstract Class<? extends BaseElement> forBusinessObject();
}
