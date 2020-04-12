/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.demux;

import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.ImageDescriptor;
import org.ruminaq.gui.api.ImagesExtension;

/**
 * Images in Demux plugin.
 *
 * @author Marek Jagielski
 */
@Component(property = { "service.ranking:Integer=5" })
public class Images implements ImagesExtension {

  public enum Image implements ImageDescriptor {
    IMG_DEMUX_PALETTE("/icons/palette.demux.png"),
    IMG_DEMUX_DIAGRAM("/icons/diagram.demux.png");

    public String path;

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
