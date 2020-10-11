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
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.model.ruminaq.PortsDescr;
import org.ruminaq.tasks.mux.gui.UpdateDemuxFeature.Filter;
import org.ruminaq.tasks.mux.model.DemuxPort;
import org.ruminaq.tasks.mux.model.mux.Demux;

/**
 * Service UpdateFeatureExtension implementation.
 *
 * @author Marek Jagielski
 */
@FeatureFilter(Filter.class)
public class UpdateDemuxFeature extends UpdateTaskFeature {

  public static class Filter extends AbstractUpdateFeatureFilter {
    @Override
    public Class<? extends BaseElement> forBusinessObject() {
      return Demux.class;
    }
  }

  public UpdateDemuxFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public IReason updateNeeded(IUpdateContext context) {
    Demux demux = modelFromContext(context, Demux.class)
        .orElseThrow(() -> new RuntimeException());

    if (super.updateNeeded(context).toBoolean() || outputsUpdateNeeded(demux)) {
      return Reason.createTrueReason();
    } else {
      return Reason.createFalseReason();
    }
  }

  private boolean outputsUpdateNeeded(Demux demux) {
    return demux.getSize() != demux.getOutputPort().size();
  }

  @Override
  public boolean update(IUpdateContext context) {
    Demux demux = modelFromContext(context, Demux.class)
        .orElseThrow(() -> new RuntimeException());

    boolean updated = false;

    if (outputsUpdateNeeded(demux)) {
      updated = updated | outputsUpdate(demux);
    }

    if (super.updateNeeded(context).toBoolean()) {
      updated = updated | super.update(context);
    }

    return updated;
  }

  private boolean outputsUpdate(Demux demux) {
    int n = demux.getSize() - demux.getOutputPort().size();
    if (n > 0) {
      IntStream.range(0, n)
          .forEach(i -> createOutputPort(demux, DemuxPort.OUT));
    } else if (n < 0) {
      IntStream.range(0, n)
          .forEach(i -> deleteOutputPort(demux, DemuxPort.OUT.getId()));
    }
    return true;
  }

  @Override
  protected Class<? extends PortsDescr> getPortsDescription() {
    return DemuxPort.class;
  }
}
