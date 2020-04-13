/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.gate.and;

import java.util.Arrays;
import java.util.Collection;

import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.ImageDescriptor;
import org.ruminaq.gui.api.ImagesExtension;
import org.ruminaq.gui.image.ImageDescriptorImpl;

/**
 * Images in And plugin.
 *
 * @author Marek Jagielski
 */
@Component(property = { "service.ranking:Integer=5" })
public class Images implements ImagesExtension {

  public static final String IMG_AND_PALETTE = "/icons/palette.and.png";
  public static final String IMG_AND_DIAGRAM = "/icons/diagram.and.png";

  @Override
  public Collection<ImageDescriptor> getImageDecriptors() {
    return Arrays.asList(new ImageDescriptorImpl(Images.class, IMG_AND_PALETTE),
        new ImageDescriptorImpl(Images.class, IMG_AND_DIAGRAM));
  }
}
