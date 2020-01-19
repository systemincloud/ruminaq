/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.model.diagram.impl.label;

import java.util.WeakHashMap;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.ruminaq.gui.model.diagram.LabelShape;
import org.ruminaq.gui.model.diagram.LabeledRuminaqShape;
import org.ruminaq.gui.model.diagram.impl.Factory;

/**
 * Factory for creating Label.
 *
 * @author Marek Jagielski
 */
public class LabelShapeFactory implements Factory {

  public static final LabelShapeFactory INSTANCE = new LabelShapeFactory();

  private WeakHashMap<EObject, LabelShapeGA> cacheGraphicsAlgorithms = new WeakHashMap<>();

  @Override
  public boolean isForThisShape(Shape shape) {
    return LabelShape.class.isInstance(shape);
  }

  @Override
  public GraphicsAlgorithm getGA(Shape shape) {
    if (shape instanceof LabelShape) {
      if (!cacheGraphicsAlgorithms.containsKey(shape)) {
        cacheGraphicsAlgorithms.put(shape,
            new LabelShapeGA((LabelShape) shape));
      }
      return cacheGraphicsAlgorithms.get(shape);
    } else {
      return null;
    }
  }
}
