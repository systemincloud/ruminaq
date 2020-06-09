/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.mux.gui;

import java.util.stream.IntStream;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.Reason;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.update.AbstractUpdateFeatureFilter;
import org.ruminaq.gui.features.update.UpdateTaskFeature;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.tasks.mux.gui.UpdateMuxFeature.Filter;
import org.ruminaq.tasks.mux.model.MuxPort;
import org.ruminaq.tasks.mux.model.mux.Mux;

/**
 * Service UpdateFeatureExtension implementation.
 *
 * @author Marek Jagielski
 */
@FeatureFilter(Filter.class)
public class UpdateMuxFeature extends UpdateTaskFeature {

  public static class Filter extends AbstractUpdateFeatureFilter {
    @Override
    public Class<? extends BaseElement> forBusinessObject() {
      return Mux.class;
    }
  }

  public UpdateMuxFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public IReason updateNeeded(IUpdateContext context) {
    Mux mux = modelFromShape(UpdateTaskFeature.shapeFromContext(context),
        Mux.class).orElseThrow(() -> new RuntimeException());

    if (super.updateNeeded(context).toBoolean() || inputsUpdateNeeded(mux)) {
      return Reason.createTrueReason();
    } else {
      return Reason.createFalseReason();
    }
  }

  private boolean inputsUpdateNeeded(Mux mux) {
    return mux.getSize() != mux.getInputPort().size() - 1;
  }

  @Override
  public boolean update(IUpdateContext context) {
    Mux mux = modelFromShape(UpdateTaskFeature.shapeFromContext(context),
        Mux.class).orElseThrow(() -> new RuntimeException());

    boolean updated = false;

    if (inputsUpdateNeeded(mux)) {
      updated = updated | inputsUpdate(mux);
    }

    if (super.updateNeeded(context).toBoolean()) {
      updated = updated | super.update(context);
    }

    return updated;
  }

  private boolean inputsUpdate(Mux mux) {
    int n = mux.getSize() - mux.getInputPort().size() + 1;
    if (n > 0) {
      IntStream.range(0, n).forEach(i -> createInputPort(mux, MuxPort.IN));
    } else if (n < 0) {
      IntStream.range(0, n)
          .forEach(i -> deleteInputPort(mux, MuxPort.IN.getId()));
    }
    return true;
  }

  @Override
  protected Class<? extends PortsDescr> getPortsDescription() {
    return MuxPort.class;
  }
}
