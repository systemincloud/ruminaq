/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.randomgenerator.gui;

import java.util.Arrays;
import java.util.Collection;

import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.ImageDescriptor;
import org.ruminaq.gui.api.ImagesExtension;
import org.ruminaq.gui.image.ImageDescriptorImpl;

/**
 * Images in RandomGenerator plugin.
 *
 * @author Marek Jagielski
 */
@Component(property = { "service.ranking:Integer=5" })
public class Images implements ImagesExtension {

  public static final String IMG_RANDOMGENERATOR_PALETTE = "/icons/palette.randomgenerator.png";
  public static final String IMG_RANDOMGENERATOR_DIAGRAM = "/icons/diagram.randomgenerator.png";

  @Override
  public Collection<ImageDescriptor> getImageDecriptors() {
    return Arrays.asList(
        new ImageDescriptorImpl(Images.class, IMG_RANDOMGENERATOR_PALETTE),
        new ImageDescriptorImpl(Images.class, IMG_RANDOMGENERATOR_DIAGRAM));
  }
}
