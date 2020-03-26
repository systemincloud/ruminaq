/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.api;

/**
 * Image definition that can be used in UI.
 * 
 * @author Marek Jagielski
 */
public interface ImageDescriptor {

  /**
   * Key of image.
   */
  String name();

  /**
   * File of bundle path to image.
   */
  String path();

  /**
   * Class that belongs to bundle where image is.
   */
  Class<?> clazz();
}
