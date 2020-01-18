/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.model.diagram.impl.port;

import java.util.Optional;
import java.util.WeakHashMap;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.ruminaq.gui.model.diagram.PortShape;
import org.ruminaq.gui.model.diagram.impl.Factory;
import org.ruminaq.gui.model.diagram.impl.GuiModelException;
import org.ruminaq.model.ruminaq.BaseElement;

/**
 * Factory for creating Port.
 *
 * @author Marek Jagielski
 */
public class PortShapeFactory implements Factory {

  public static final PortShapeFactory INSTANCE = new PortShapeFactory();

  private WeakHashMap<EObject, PortShapeGA> cacheGraphicsAlgorithms = new WeakHashMap<>();

  @Override
  public boolean isForThisShape(Shape shape) {
    return PortShape.class.isInstance(shape);
  }

  @Override
  public GraphicsAlgorithm getGA(Shape shape) {
    Optional<BaseElement> mo = Optional.of(shape).map(s -> (PortShape) shape)
        .map(PortShape::getModelObject);
    if (mo.isPresent()) {
      if (!cacheGraphicsAlgorithms.containsKey(mo.get())) {
        cacheGraphicsAlgorithms.put(mo.get(), new PortShapeGA((PortShape) shape));
      }
      return cacheGraphicsAlgorithms.get(mo.get());
    }
    throw new GuiModelException("No model object for PortShape");
  }
}
