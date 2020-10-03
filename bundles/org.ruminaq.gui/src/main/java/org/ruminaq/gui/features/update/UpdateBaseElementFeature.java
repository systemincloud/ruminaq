/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.update;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.AbstractUpdateFeature;
import org.eclipse.graphiti.features.impl.Reason;

/**
 * Common class for all Base Elements.
 *
 * @author Marek Jagielski
 */
public class UpdateBaseElementFeature extends AbstractUpdateFeature {

  public UpdateBaseElementFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canUpdate(IUpdateContext context) {
    return true;
  }

  @Override
  public IReason updateNeeded(IUpdateContext context) {
    return Reason.createFalseReason();
  }

  @Override
  public boolean update(IUpdateContext context) {
    return false;
  }

}
