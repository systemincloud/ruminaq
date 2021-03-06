/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.paste;

import java.util.Map;

import org.eclipse.graphiti.mm.pictograms.Anchor;

/**
 * Return pasted Anchors.
 *
 * @author Marek Jagielski
 */
public interface PasteAnchorTracker {
  Map<Anchor, Anchor> getAnchors();
}
