/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.api;

import org.eclipse.graphiti.mm.pictograms.PictogramElement;

/**
 * Service api providing Graphiti SelectionExtension.
 *
 * @author Marek Jagielski
 */
public interface SelectionExtension {

  boolean forPictogramElement(PictogramElement selection);

  PictogramElement[] getSelections(PictogramElement selection);

}
