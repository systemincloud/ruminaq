/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.delete;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.ui.features.DefaultDeleteFeature;
import org.ruminaq.gui.diagram.FeatureProviderRemoveDecorator;

/**
 * Common class for DeleteFeature.
 *
 * @author Marek Jagielski
 */
public class RuminaqDeleteFeature extends DefaultDeleteFeature {

  public RuminaqDeleteFeature(IFeatureProvider fp) {
    super(new FeatureProviderRemoveDecorator(fp.getDiagramTypeProvider()));
  }

}
