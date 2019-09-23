/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.move;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IMoveAnchorContext;
import org.eclipse.graphiti.features.impl.DefaultMoveAnchorFeature;

public class MoveAnchorForbiddenFeature extends DefaultMoveAnchorFeature {

  public MoveAnchorForbiddenFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canMoveAnchor(IMoveAnchorContext context) {
    return false;
  }
}
