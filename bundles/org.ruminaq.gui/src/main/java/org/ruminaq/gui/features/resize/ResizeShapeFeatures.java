/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.resize;

import java.util.Arrays;
import java.util.List;

import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.ResizeShapeFeatureExtension;

/**
 *
 * @author Marek Jagielski
 */
@Component(property = { "service.ranking:Integer=5" })
public class ResizeShapeFeatures implements ResizeShapeFeatureExtension {

  @Override
  public List<Class<? extends IResizeShapeFeature>> getFeatures() {
    return Arrays.asList(ResizeSimpleConnectionPointFeature.class,
        ResizeLabelFeature.class, ResizePortFeature.class,
        ResizeInternalPortFeature.class, ResizeShapeTaskFeature.class);
  }
}
