/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui;

import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.ImageDescriptor;
import org.ruminaq.gui.api.ImagesExtension;

/**
 *
 * @author Marek Jagielski
 */
@Component(property = { "service.ranking:Integer=5" })
public class Images implements ImagesExtension {

  public enum Image implements ImageDescriptor {
    IMG_CONTEXT_SIMPLECONNECTION("/icons/context.simpleconnection.gif"),
    IMG_PALETTE_INPUTPORT("/icons/palette.inputport.png"),
    IMG_PALETTE_OUTPUTPORT("/icons/palette.outputport.png");

    private String path;

    Image(String path) {
      this.path = path;
    }

    @Override
    public String path() {
      return path;
    }

    @Override
    public Class<?> clazz() {
      return Image.class;
    }
  }

  @Override
  public ImageDescriptor[] getImageDecriptors() {
    return Image.values();
  }
}
