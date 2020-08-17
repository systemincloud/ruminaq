/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.image;

import java.util.Arrays;
import java.util.Collection;

import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.ImageDescriptor;
import org.ruminaq.gui.api.ImagesExtension;

/**
 * Images in gui plugin.
 *
 * @author Marek Jagielski
 */
@Component(property = { "service.ranking:Integer=5" })
public class Images implements ImagesExtension {

  public static final String IMG_CONTEXT_SIMPLECONNECTION = "/icons/context.simpleconnection.gif";
  public static final String IMG_PALETTE_INPUTPORT = "/icons/palette.inputport.png";
  public static final String IMG_PALETTE_OUTPUTPORT = "/icons/palette.outputport.png";
  public static final String IMG_EMBEDDEDTASK_PALETTE = "/icons/palette.embeddedtask_main.png";
  public static final String IMG_EMBEDDEDTASK_DIAGRAM_MAIN = "/icons/diagram.embeddedtask_main.png";
  public static final String IMG_EMBEDDEDTASK_DIAGRAM_TEST = "/icons/diagram.embeddedtask_test.png";
  public static final String IMG_TOGGLE_BREAKPOINT = "/icons/toggle.breakpoint.gif";

  @Override
  public Collection<ImageDescriptor> getImageDecriptors() {
    return Arrays.asList(
        new ImageDescriptorImpl(Images.class, IMG_CONTEXT_SIMPLECONNECTION),
        new ImageDescriptorImpl(Images.class, IMG_PALETTE_INPUTPORT),
        new ImageDescriptorImpl(Images.class, IMG_PALETTE_OUTPUTPORT),
        new ImageDescriptorImpl(Images.class, IMG_EMBEDDEDTASK_PALETTE),
        new ImageDescriptorImpl(Images.class, IMG_EMBEDDEDTASK_DIAGRAM_MAIN),
        new ImageDescriptorImpl(Images.class, IMG_EMBEDDEDTASK_DIAGRAM_TEST),
        new ImageDescriptorImpl(Images.class, IMG_TOGGLE_BREAKPOINT));
  }
}
