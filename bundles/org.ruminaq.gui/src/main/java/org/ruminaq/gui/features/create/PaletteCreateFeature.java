/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.create;

/**
 * Features that should be in Palette.
 * 
 * @author Marek Jagielski
 */
public interface PaletteCreateFeature {
  default boolean isTestOnly() {
    return false;
  }

  String getCompartment();

  String getStack();
}
