/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse;

import org.ruminaq.util.IImage;

/**
 * Image.
 *
 * @author Marek Jagielski
 */
public enum Image implements IImage {

  RUMINAQ_LOGO_16X16("icons/logo/ruminaq.logo.16x16.png"),
  RUMINAQ_LOGO_32X32("icons/logo/ruminaq.logo.32x32.png"),
  RUMINAQ_LOGO_48X48("icons/logo/ruminaq.logo.48x48.png"),
  RUMINAQ_LOGO_64X64("icons/logo/ruminaq.logo.64x64.png"),
  RUMINAQ_LOGO_128X128("icons/logo/ruminaq.logo.128x128.png"),

  RUMINAQ_DIAGRAM("icons/system.png"),
  RUMINAQ_TEST("icons/test.png");

  private final String path;

  Image(String path) {
    this.path = path;
  }

  @Override
  public String getPath() {
    return path;
  }
}
