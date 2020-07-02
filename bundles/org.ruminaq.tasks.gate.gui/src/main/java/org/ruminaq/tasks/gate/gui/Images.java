/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.gate.gui;

import java.util.Arrays;
import java.util.Collection;

import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.ImageDescriptor;
import org.ruminaq.gui.api.ImagesExtension;
import org.ruminaq.gui.image.ImageDescriptorImpl;

/**
 * Images in Gate plugin.
 *
 * @author Marek Jagielski
 */
@Component(property = { "service.ranking:Integer=5" })
public class Images implements ImagesExtension {

  public static final String IMG_AND_PALETTE = "/icons/palette.and.png";
  public static final String IMG_AND_DIAGRAM = "/icons/diagram.and.png";
  public static final String IMG_NOT_PALETTE = "/icons/palette.not.png";
  public static final String IMG_NOT_DIAGRAM = "/icons/diagram.not.png";
  public static final String IMG_OR_PALETTE = "/icons/palette.or.png";
  public static final String IMG_OR_DIAGRAM = "/icons/diagram.or.png";
  public static final String IMG_XOR_PALETTE = "/icons/palette.xor.png";
  public static final String IMG_XOR_DIAGRAM = "/icons/diagram.xor.png";

  @Override
  public Collection<ImageDescriptor> getImageDecriptors() {
    return Arrays.asList(new ImageDescriptorImpl(Images.class, IMG_AND_PALETTE),
        new ImageDescriptorImpl(Images.class, IMG_AND_DIAGRAM),
        new ImageDescriptorImpl(Images.class, IMG_NOT_PALETTE),
        new ImageDescriptorImpl(Images.class, IMG_NOT_DIAGRAM),
        new ImageDescriptorImpl(Images.class, IMG_OR_PALETTE),
        new ImageDescriptorImpl(Images.class, IMG_OR_DIAGRAM),
        new ImageDescriptorImpl(Images.class, IMG_XOR_PALETTE),
        new ImageDescriptorImpl(Images.class, IMG_XOR_DIAGRAM));
  }
}
