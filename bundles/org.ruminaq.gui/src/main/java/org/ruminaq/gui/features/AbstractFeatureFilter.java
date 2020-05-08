/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features;

import java.util.Optional;
import java.util.function.Function;

import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.ruminaq.gui.model.diagram.RuminaqShape;
import org.ruminaq.model.ruminaq.BaseElement;

/**
 * Inheriting classes can be used in @FeatureFilter annotations. Used on
 * implementations of AbstractElementFeature.
 *
 * @author Marek Jagielski
 */
public abstract class AbstractFeatureFilter<T extends IContext>
    implements FeaturePredicate<IContext> {

  private Class<T> contextClass;
  
  private GetPictogramElement getPictogramElement;

  public AbstractFeatureFilter(Class<T> contextClass, GetPictogramElement getPictogramElement) {
    this.contextClass = contextClass;
    this.getPictogramElement = getPictogramElement;
  }

  @Override
  public boolean test(IContext context) {
    return Optional.of(context).filter(contextClass::isInstance)
        .map(contextClass::cast).map(getPictogramElement)
        .filter(pe -> forShape().isAssignableFrom(pe.getClass()))
        .map(RuminaqShape.class::cast).map(RuminaqShape::getModelObject)
        .map(Object::getClass).filter(forBusinessObject()::isAssignableFrom)
        .isPresent();
  }

  public Class<? extends RuminaqShape> forShape() {
    return RuminaqShape.class;
  }

  public Class<? extends BaseElement> forBusinessObject() {
    return BaseElement.class;
  }
  
  public interface GetPictogramElement extends Function<IContext, PictogramElement> {
    PictogramElement apply(IContext context);
  }
  
}
