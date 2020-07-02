/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.mux.gui;

import java.util.Arrays;
import java.util.Collection;

import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.ImageDescriptor;
import org.ruminaq.gui.api.ImagesExtension;
import org.ruminaq.gui.image.ImageDescriptorImpl;

/**
 * Images in Mux plugin.
 *
 * @author Marek Jagielski
 */
@Component(property = { "service.ranking:Integer=5" })
public class Images implements ImagesExtension {

  public static final String IMG_MUX_PALETTE = "/icons/palette.mux.png";
  public static final String IMG_MUX_DIAGRAM = "/icons/diagram.mux.png";
  public static final String IMG_DEMUX_PALETTE = "/icons/palette.demux.png";
  public static final String IMG_DEMUX_DIAGRAM = "/icons/diagram.demux.png";

  @Override
  public Collection<ImageDescriptor> getImageDecriptors() {
    return Arrays.asList(new ImageDescriptorImpl(Images.class, IMG_MUX_PALETTE),
        new ImageDescriptorImpl(Images.class, IMG_MUX_DIAGRAM),
        new ImageDescriptorImpl(Images.class, IMG_DEMUX_PALETTE),
        new ImageDescriptorImpl(Images.class, IMG_DEMUX_DIAGRAM));
  }
}
