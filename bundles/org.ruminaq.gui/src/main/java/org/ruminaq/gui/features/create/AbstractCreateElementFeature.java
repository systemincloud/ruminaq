/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.create;

import java.util.Collection;
import java.util.Objects;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.ruminaq.gui.model.diagram.RuminaqDiagram;
import org.ruminaq.gui.model.diagram.RuminaqShape;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.model.util.ModelUtil;

/**
 * Base class for creating Ruminaq elements.
 * 
 * @author Marek Jagielski
 */
public abstract class AbstractCreateElementFeature
    extends AbstractCreateFeature {

  protected AbstractCreateElementFeature(IFeatureProvider fp,
      Class<? extends BaseElement> clazz) {
    super(fp, ModelUtil.getName(clazz), "New " + clazz.getSimpleName());
  }

  @Override
  public boolean canCreate(ICreateContext context) {
    return context.getTargetContainer().equals(getDiagram());
  }

  protected RuminaqDiagram getRuminaqDiagram() {
    return (RuminaqDiagram) getDiagram();
  }

  protected void setDefaultId(BaseElement element, ICreateContext context) {
    String id = "My " + ModelUtil.getName(element.getClass());
    if (isPresent(id, context.getTargetContainer().getChildren())) {
      int i = 1;
      while (isPresent(id + " " + i,
          context.getTargetContainer().getChildren())) {
        i++;
      }
      id = id + " " + i;
    }

    element.setId(id);
  }

  /**
   * Does list of shapes contain shape of object with id.
   * 
   * @param id of shape
   * @param shapes collection of shapes
   */
  public static boolean isPresent(String id, Collection<Shape> shapes) {
    return shapes.stream().filter(RuminaqShape.class::isInstance)
        .map(RuminaqShape.class::cast).map(RuminaqShape::getModelObject)
        .filter(Objects::nonNull).map(BaseElement::getId)
        .anyMatch(id::equals);
  }
}
