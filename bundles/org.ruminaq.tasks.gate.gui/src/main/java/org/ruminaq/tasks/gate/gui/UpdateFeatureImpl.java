/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.gate.gui;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.Reason;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.UpdateFeatureExtension;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.update.AbstractUpdateFeatureFilter;
import org.ruminaq.gui.features.update.UpdateTaskFeature;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.tasks.gate.gui.UpdateFeatureImpl.UpdateFeature.Filter;
import org.ruminaq.tasks.gate.model.gate.Gate;

/**
 * Service UpdateFeatureExtension implementation.
 *
 * @author Marek Jagielski
 */
@Component(property = { "service.ranking:Integer=10" })
public class UpdateFeatureImpl implements UpdateFeatureExtension {

  @Override
  public List<Class<? extends IUpdateFeature>> getFeatures() {
    return Collections.singletonList(UpdateFeature.class);
  }

  /**
   * Gate UpdateFeature.
   */
  @FeatureFilter(Filter.class)
  public static class UpdateFeature extends UpdateTaskFeature {

    public static class Filter extends AbstractUpdateFeatureFilter {
      @Override
      public Class<? extends BaseElement> forBusinessObject() {
        return Gate.class;
      }
    }

    public UpdateFeature(IFeatureProvider fp) {
      super(fp);
    }

    @Override
    public IReason updateNeeded(IUpdateContext context) {
      Gate gate = modelFromContext(context, Gate.class)
          .orElseThrow(RuntimeException::new);

      if (super.updateNeeded(context).toBoolean() || inputsUpdateNeeded(gate)) {
        return Reason.createTrueReason();
      } else {
        return Reason.createFalseReason();
      }
    }

    private static boolean inputsUpdateNeeded(Gate gate) {
      return gate.getInputNumber() != gate.getInputPort().size();
    }

    @Override
    public boolean update(IUpdateContext context) {
      Gate gate = modelFromContext(context, Gate.class)
          .orElseThrow(RuntimeException::new);

      boolean updated = false;

      if (inputsUpdateNeeded(gate)) {
        updated = updated | inputsUpdate(gate);
      }

      if (super.updateNeeded(context).toBoolean()) {
        updated = updated | super.update(context);
      }

      return updated;
    }

    private boolean inputsUpdate(Gate gate) {
      int n = gate.getInputNumber() - gate.getInputPort().size();
      if (n > 0) {
        IntStream.range(0, n).forEach(i -> createInputPort(gate, Port.IN));
      } else {
        IntStream.range(0, n)
            .forEach(i -> deleteInputPort(gate, Port.IN.getId()));
      }
      return true;
    }

    @Override
    protected Class<? extends PortsDescr> getPortsDescription() {
      return Port.class;
    }
  }
}
