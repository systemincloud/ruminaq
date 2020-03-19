/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.api;

import org.eclipse.graphiti.datatypes.IRectangle;

/**
 * Service api providing ContextButtonPadLocation.
 *
 * @author Marek Jagielski
 */
public interface ContextButtonPadLocationExtension {
  
  /**
   * Return a new position of pad icon menu based on actual position.
   * 
   * @param rectangle initial position of pad menu
   * @return position of pad menu
   */
  IRectangle getPadLocation(IRectangle rectangle);
}
