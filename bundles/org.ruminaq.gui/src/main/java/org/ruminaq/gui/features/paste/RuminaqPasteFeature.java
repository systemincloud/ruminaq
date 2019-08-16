/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.paste;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.features.AbstractPasteFeature;

public abstract class RuminaqPasteFeature extends AbstractPasteFeature {

	public RuminaqPasteFeature(IFeatureProvider fp) {
		super(fp);
	}

	protected List<PictogramElement> newPes = new LinkedList<>();

	public abstract List<PictogramElement> getNewPictogramElements();
}
