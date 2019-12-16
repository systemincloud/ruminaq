/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.create;

import java.util.Objects;

import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.ruminaq.gui.model.diagram.RuminaqDiagram;
import org.ruminaq.gui.model.diagram.RuminaqShape;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.model.util.ModelUtil;

public abstract class CreateElementFeature extends AbstractCreateFeature {

  public CreateElementFeature(IFeatureProvider fp, String name,
      String description) {
    super(fp, name, description);
  }

  public CreateElementFeature(IFeatureProvider fp,
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
    String name = "My " + ModelUtil.getName(element.getClass());
    if (isPresent(name, context.getTargetContainer().getChildren())) {
      int i = 1;
      while (isPresent(name + " " + i,
          context.getTargetContainer().getChildren())) {
        i++;
      }
      name = name + " " + i;
    }

    element.setId(name);
  }

  public static boolean isPresent(String name, EList<Shape> eList) {
    return eList.stream().filter(RuminaqShape.class::isInstance)
        .map(s -> (RuminaqShape) s).map(RuminaqShape::getModelObject)
        .filter(Objects::nonNull).map(BaseElement::getId)
        .anyMatch(name::equals);
  }
}
