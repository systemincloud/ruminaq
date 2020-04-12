/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.constant;

import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.ImageDescriptor;
import org.ruminaq.gui.api.ImagesExtension;

/**
 * Images in Constant plugin.
 *
 * @author Marek Jagielski
 */
@Component(property = { "service.ranking:Integer=5" })
public class Images implements ImagesExtension {

  public enum Image implements ImageDescriptor {
    IMG_CONSTANT_PALETTE("/icons/palette.constant.png");

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
