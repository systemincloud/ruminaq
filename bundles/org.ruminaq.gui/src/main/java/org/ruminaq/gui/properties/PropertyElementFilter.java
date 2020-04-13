/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.properties;

import java.util.Optional;
import java.util.function.Predicate;

import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.platform.AbstractPropertySectionFilter;
import org.ruminaq.gui.model.diagram.LabelShape;
import org.ruminaq.gui.model.diagram.RuminaqShape;

/**
 * Ruminaq Shape properties filter.
 * 
 * @author Marek Jagielski
 */
public class PropertyElementFilter extends AbstractPropertySectionFilter {

  @Override
  protected boolean accept(PictogramElement pe) {
    return Optional.of(pe).filter(
        RuminaqShape.class::isInstance)
        .filter(Predicate.not(LabelShape.class::isInstance)).isPresent();
  }

}
