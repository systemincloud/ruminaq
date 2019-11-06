/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.graphiti.ui.platform.AbstractImageProvider;
import org.ruminaq.gui.api.ImagesExtension;
import org.ruminaq.util.ServiceUtil;

/**
 * Add all images used in editors.
 *
 * @author Marek Jagielski
 */
public class ImageProvider extends AbstractImageProvider {

  @Override
  protected void addAvailableImages() {
    ServiceUtil
        .getServicesAtLatestVersion(ImageProvider.class, ImagesExtension.class)
        .stream().map(ImagesExtension::getImageKeyPath).map(Map::entrySet)
        .flatMap(Collection::stream)
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        .entrySet().stream()
        .forEach(img -> addImageFilePath(img.getKey(), img.getValue()));
  }
}
