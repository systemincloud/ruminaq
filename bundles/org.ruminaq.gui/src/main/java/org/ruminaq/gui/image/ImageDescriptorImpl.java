/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.image;

import org.ruminaq.gui.api.ImageDescriptor;

public class ImageDescriptorImpl implements ImageDescriptor {

  private Class<?> clazz;
  
  private String name;

  private String path;
  
  public ImageDescriptorImpl(Class<?> clazz,
      String path) {
    this.clazz = clazz;
    this.name = path;
    this.path = path;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public String path() {
    return path;
  }

  @Override
  public Class<?> clazz() {
    return clazz;
  }

}
