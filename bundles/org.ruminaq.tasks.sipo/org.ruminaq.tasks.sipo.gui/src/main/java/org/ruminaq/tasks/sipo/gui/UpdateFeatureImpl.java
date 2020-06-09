/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.sipo.gui;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.Reason;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.TasksUtil;
import org.ruminaq.gui.api.UpdateFeatureExtension;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.update.AbstractUpdateFeatureFilter;
import org.ruminaq.gui.features.update.UpdateTaskFeature;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.tasks.sipo.gui.UpdateFeatureImpl.UpdateFeature.Filter;
import org.ruminaq.tasks.sipo.model.Port;
import org.ruminaq.tasks.sipo.model.sipo.Sipo;

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
   * Constant UpdateFeature.
   */
  @FeatureFilter(Filter.class)
  public static class UpdateFeature extends UpdateTaskFeature {

    public static class Filter extends AbstractUpdateFeatureFilter {
      @Override
      public Class<? extends BaseElement> forBusinessObject() {
        return Sipo.class;
      }
    }

    public UpdateFeature(IFeatureProvider fp) {
      super(fp);
    }

    @Override
    public IReason updateNeeded(IUpdateContext context) {
      Sipo sipo = modelFromContext(context, Sipo.class)
          .orElseThrow(() -> new RuntimeException());

      if (super.updateNeeded(context).toBoolean() || clkUpdateNeeded(sipo)
          || idxUpdateNeeded(sipo) || trgUpdateNeeded(sipo)
          || sizeUpdateNeeded(sipo) || lastUpdateNeeded(sipo)
          || sizeOutUpdateNeeded(sipo)) {
        return Reason.createTrueReason();
      } else {
        return Reason.createFalseReason();
      }
    }

    private boolean clkUpdateNeeded(Sipo sipo) {
      if (sipo.isClock()) {
        return sipo.getInputPort(Port.CLK.getId()) == null;
      } else {
        return sipo.getInputPort(Port.CLK.getId()) != null;
      }
    }

    private boolean idxUpdateNeeded(Sipo sipo) {
      if (sipo.isIndex()) {
        return sipo.getInputPort(Port.IDX.getId()) == null;
      } else {
        return sipo.getInputPort(Port.IDX.getId()) != null;
      }
    }

    private boolean trgUpdateNeeded(Sipo sipo) {
      if (sipo.isTrigger() && !sipo.isIndex()) {
        return sipo.getInputPort(Port.TRIGGER.getId()) == null;
      } else {
        return sipo.getInputPort(Port.TRIGGER.getId()) != null;
      }
    }

    private boolean sizeUpdateNeeded(Sipo sipo) {
      if (sipo.isIndex()) {
        return TasksUtil
            .getAllMutlipleInternalOutputPorts(sipo, Port.OUT.getId())
            .size() != 0;
      } else {
        return TasksUtil
            .getAllMutlipleInternalOutputPorts(sipo, Port.OUT.getId())
            .size() != Integer.parseInt(sipo.getSize());
      }
    }

    private boolean lastUpdateNeeded(Sipo sipo) {
      if (sipo.isIndex()) {
        return sipo.getOutputPort(Port.LOUT.getId()) == null;
      } else {
        return sipo.getOutputPort(Port.LOUT.getId()) != null;
      }
    }

    private boolean sizeOutUpdateNeeded(Sipo sipo) {
      if (sipo.isSizeOut()) {
        return sipo.getOutputPort(Port.SIZE.getId()) == null;
      } else {
        return sipo.getOutputPort(Port.SIZE.getId()) != null;
      }
    }

    @Override
    public boolean update(IUpdateContext context) {
      Sipo sipo = modelFromContext(context, Sipo.class)
          .orElseThrow(() -> new RuntimeException());

      boolean updated = false;

      if (clkUpdateNeeded(sipo)) {
        updated = updated | clkUpdate(sipo);
      }

      if (idxUpdateNeeded(sipo)) {
        updated = updated | idxUpdate(sipo);
      }

      if (trgUpdateNeeded(sipo)) {
        updated = updated | trgUpdate(sipo);
      }

      if (sizeUpdateNeeded(sipo)) {
        updated = updated | sizeUpdate(sipo);
      }

      if (lastUpdateNeeded(sipo)) {
        updated = updated | lastUpdate(sipo);
      }

      if (sizeOutUpdateNeeded(sipo)) {
        updated = updated | sizeOutUpdate(sipo);
      }

      if (super.updateNeeded(context).toBoolean()) {
        updated = updated | super.update(context);
      }

      return updated;
    }

    private boolean clkUpdate(Sipo sipo) {
      if (sipo.isClock()) {
        createInputPort(sipo, Port.CLK);
      } else {
        deleteInputPort(sipo, Port.CLK.getId());
      }
      return true;
    }

    private boolean sizeOutUpdate(Sipo sipo) {
      if (sipo.isSizeOut()) {
        createOutputPort(sipo, Port.SIZE);
      } else {
        deleteOutputPort(sipo, Port.SIZE.getId());
      }
      return true;
    }

    private boolean lastUpdate(Sipo sipo) {
      if (sipo.isIndex()) {
        createOutputPort(sipo, Port.LOUT);
      } else {
        deleteOutputPort(sipo, Port.LOUT.getId());
      }
      return true;
    }

    private boolean trgUpdate(Sipo sipo) {
      if (sipo.isTrigger() && !sipo.isIndex()) {
        createInputPort(sipo, Port.TRIGGER);
      } else {
        deleteInputPort(sipo, Port.TRIGGER.getId());
      }
      return true;
    }

    private boolean sizeUpdate(Sipo sipo) {
      int n = sipo.isIndex() ? -Integer.parseInt(sipo.getSize())
          : Integer.parseInt(sipo.getSize()) - TasksUtil
              .getAllMutlipleInternalOutputPorts(sipo, Port.OUT.getId()).size();
      if (n > 0) {
        IntStream.range(0, n).forEach(i -> createOutputPort(sipo, Port.OUT));
      } else if (n < 0) {
        IntStream.range(0, n)
            .forEach(i -> deleteOutputPort(sipo, Port.OUT.getId()));
      }
      return true;
    }

    private boolean idxUpdate(Sipo sipo) {
      if (sipo.isIndex()) {
        createInputPort(sipo, Port.IDX);
      } else {
        deleteInputPort(sipo, Port.IDX.getId());
      }
      return true;
    }

    @Override
    protected Class<? extends PortsDescr> getPortsDescription() {
      return Port.class;
    }
  }
}
