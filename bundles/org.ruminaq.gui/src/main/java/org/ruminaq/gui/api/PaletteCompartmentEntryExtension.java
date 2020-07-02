/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.api;

import java.util.Collection;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.palette.IPaletteCompartmentEntry;

/**
 * Service api providing Graphiti IPaletteCompartmentEntries.
 *
 * @author Marek Jagielski
 */
public interface PaletteCompartmentEntryExtension {

  /**
   * Get all palette entries.
   *
   * @param fp IFeatureProvider of Graphiti
   * @return collections of IPaletteCompartmentEntry
   */
  Collection<IPaletteCompartmentEntry> getPalette(IFeatureProvider fp);
}
