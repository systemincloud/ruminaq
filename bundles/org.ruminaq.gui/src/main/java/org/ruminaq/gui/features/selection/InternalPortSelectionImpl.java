/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.selection;

import java.util.Optional;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.SelectionExtension;
import org.ruminaq.gui.model.diagram.InternalPortLabelShape;

/**
 * Service SelectionExtension implementation.
 *
 * @author Marek Jagielski
 */
@Component(property = { "service.ranking:Integer=5" })
public class InternalPortSelectionImpl implements SelectionExtension {

  @Override
  public boolean forPictogramElement(PictogramElement selection) {
    return Optional.of(selection)
        .filter(InternalPortLabelShape.class::isInstance).isPresent();
  }

  @Override
  public PictogramElement[] getSelections(PictogramElement selection) {
    return new PictogramElement[0];
  }

}
