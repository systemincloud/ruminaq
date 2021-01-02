/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.update;

import java.util.Optional;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.Reason;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.model.diagram.DiagramFactory;
import org.ruminaq.gui.model.diagram.InternalPortLabelShape;
import org.ruminaq.gui.model.diagram.InternalPortShape;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.model.ruminaq.InternalPort;

/**
 * IUpdateFeature for InternalPort.
 *
 * @author Marek Jagielski
 */
@FeatureFilter(UpdateInternalPortFeature.Filter.class)
public class UpdateInternalPortFeature extends UpdateBaseElementFeature {

  private static class Filter extends AbstractUpdateFeatureFilter {
    @Override
    public Class<? extends BaseElement> forBusinessObject() {
      return InternalPort.class;
    }
  }

  public UpdateInternalPortFeature(IFeatureProvider fp) {
    super(fp);
  }

  protected static Optional<InternalPortShape> shapeFromContext(
      IUpdateContext context) {
    return Optional.of(context)
        .map(AbstractUpdateFeatureFilter.getPictogramElement)
        .filter(InternalPortShape.class::isInstance)
        .map(InternalPortShape.class::cast);
  }

  protected static Optional<InternalPort> modelFromContext(
      IUpdateContext context) {
    return shapeFromContext(context).map(InternalPortShape::getModelObject)
        .filter(InternalPort.class::isInstance).map(InternalPort.class::cast);
  }

  private static boolean updateLabelNeeded(IUpdateContext context) {
    Optional<InternalPortShape> shape = shapeFromContext(context);
    if (shape.isPresent()) {
      return (shape.get().getInternalPortLabel() == null
          && shape.get().isShowLabel())
          || (shape.get().getInternalPortLabel() != null
              && !shape.get().isShowLabel());
    }
    return false;
  }

  private static void updateLabel(IUpdateContext context) {
    Optional<InternalPortShape> shape = shapeFromContext(context);
    Optional<InternalPort> model = modelFromContext(context);
    if (shape.isPresent() && model.isPresent()) {
      if (shape.get().getInternalPortLabel() == null) {
        InternalPortLabelShape label = DiagramFactory.eINSTANCE
            .createInternalPortLabelShape();
        shape.get().getTask().getTransientChildren().add(label);
        shape.get().setInternalPortLabel(label);
      } else {
        shape.get().getTask().getTransientChildren()
            .remove(shape.get().getInternalPortLabel());
        shape.get().setInternalPortLabel(null);
      }
    }
  }

  @Override
  public IReason updateNeeded(IUpdateContext context) {
    if (super.updateNeeded(context).toBoolean() || updateLabelNeeded(context)) {
      return Reason.createTrueReason();
    } else {
      return Reason.createFalseReason();
    }
  }

  @Override
  public boolean update(IUpdateContext context) {
    if (super.updateNeeded(context).toBoolean()) {
      super.update(context);
    }

    if (updateLabelNeeded(context)) {
      updateLabel(context);
    }

    return true;
  }
}
