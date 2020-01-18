/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.model.diagram.impl;

import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.Shape;

/**
 * Interface for GraphicAlgorithms factories.
 *
 * @author Marek Jagielski
 */
public interface Factory {

  boolean isForThisShape(Shape shape);

  GraphicsAlgorithm getGA(Shape shape);
}
