/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.constant;

import java.util.Collection;
import java.util.Collections;

import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.ImageDescriptor;
import org.ruminaq.gui.api.ImagesExtension;
import org.ruminaq.gui.image.ImageDescriptorImpl;

/**
 * Images in Constant plugin.
 *
 * @author Marek Jagielski
 */
@Component(property = { "service.ranking:Integer=5" })
public class Images implements ImagesExtension {

  public static final String IMG_CONSTANT_PALETTE = "/icons/palette.constant.png";

  @Override
  public Collection<ImageDescriptor> getImageDecriptors() {
    return Collections.singletonList(
        new ImageDescriptorImpl(Images.class, IMG_CONSTANT_PALETTE));
  }
}
