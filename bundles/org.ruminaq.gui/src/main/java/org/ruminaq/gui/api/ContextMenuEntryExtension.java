/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.api;

import java.util.function.Predicate;

import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;

/**
 * Service api providing ContextMenuEntry.
 *
 * @author Marek Jagielski
 */
public interface ContextMenuEntryExtension {

  /**
   * Is item in context menu available.
   *
   * @param context ICustomContext
   * @return predicate that determine
   */
  Predicate<ICustomFeature> isAvailable(ICustomContext context);
}
