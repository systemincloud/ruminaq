/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.delete;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.ui.features.DefaultDeleteFeature;
import org.ruminaq.gui.diagram.FeatureProviderRemoveDecorator;

public class RuminaqDeleteFeature extends DefaultDeleteFeature {

  private boolean isUserDecision = true;

  public RuminaqDeleteFeature(IFeatureProvider fp) {
    super(new FeatureProviderRemoveDecorator(fp.getDiagramTypeProvider()));
  }

  public RuminaqDeleteFeature(IFeatureProvider fp, boolean isUserDecision) {
    super(new FeatureProviderRemoveDecorator(fp.getDiagramTypeProvider()));
    this.isUserDecision = isUserDecision;
  }

  @Override
  protected boolean getUserDecision(IDeleteContext context) {
    if (isUserDecision) {
      return super.getUserDecision(context);
    }

    return true;
  }

}
