/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.javatask.gui;

import java.util.Optional;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.platform.AbstractPropertySectionFilter;
import org.ruminaq.gui.model.diagram.RuminaqShape;
import org.ruminaq.tasks.javatask.model.javatask.JavaTask;

/**
 * 
 * @author Marek Jagielski
 */
public class PropertyFilter extends AbstractPropertySectionFilter {

  @Override
  protected boolean accept(PictogramElement pe) {
    return Optional.of(pe).filter(RuminaqShape.class::isInstance)
        .map(RuminaqShape.class::cast).map(RuminaqShape::getModelObject)
        .filter(JavaTask.class::isInstance).isPresent();
  }
}
