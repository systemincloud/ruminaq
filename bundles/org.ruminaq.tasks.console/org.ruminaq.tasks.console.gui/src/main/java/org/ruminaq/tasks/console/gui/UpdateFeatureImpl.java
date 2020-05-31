/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.console.gui;

import java.util.Collections;
import java.util.List;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.UpdateFeatureExtension;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.update.AbstractUpdateFeatureFilter;
import org.ruminaq.gui.features.update.UpdateTaskFeature;
import org.ruminaq.model.desc.PortsDescr;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.tasks.console.gui.UpdateFeatureImpl.UpdateFeature.Filter;
import org.ruminaq.tasks.console.model.Port;
import org.ruminaq.tasks.console.model.console.Console;
import org.ruminaq.tasks.console.model.console.ConsoleType;

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
   * Console UpdateFeature.
   */
  @FeatureFilter(Filter.class)
  public static class UpdateFeature extends UpdateTaskFeature {

    public static class Filter extends AbstractUpdateFeatureFilter {
      @Override
      public Class<? extends BaseElement> forBusinessObject() {
        return Console.class;
      }
    }

    private boolean inputUpdateNeeded = false;
    private boolean outputUpdateNeeded = false;

    public UpdateFeature(IFeatureProvider fp) {
      super(fp);
    }

    @Override
    public IReason updateNeeded(IUpdateContext context) {
      ContainerShape parent = (ContainerShape) context.getPictogramElement();
      Console console = (Console) getBusinessObjectForPictogramElement(parent);
      switch (console.getConsoleType()) {
        case IN:
          if (console.getInputPort().size() != 1)
            inputUpdateNeeded = true;
          if (console.getOutputPort().size() != 0)
            outputUpdateNeeded = true;
          break;
        case OUT:
          if (console.getInputPort().size() != 0)
            inputUpdateNeeded = true;
          if (console.getOutputPort().size() != 1)
            outputUpdateNeeded = true;
          break;
        case INOUT:
          if (console.getInputPort().size() != 1)
            inputUpdateNeeded = true;
          if (console.getOutputPort().size() != 1)
            outputUpdateNeeded = true;
          break;
        default:
          break;
      }

      if (inputUpdateNeeded
          || outputUpdateNeeded | super.updateNeeded(context).toBoolean()) {
        return Reason.createTrueReason();
      } else {
        return Reason.createFalseReason();
      }
    }

    @Override
    public boolean update(IUpdateContext context) {
      boolean updated = false;

      Console console = (Console) getBusinessObjectForPictogramElement(
          context.getPictogramElement());

      if (inputUpdateNeeded)
        updated = updated | updateInput(console,
            (ContainerShape) context.getPictogramElement());
      if (outputUpdateNeeded)
        updated = updated | updateOutput(console,
            (ContainerShape) context.getPictogramElement());

      if (super.updateNeeded(context).toBoolean()) {
        updated = updated | super.update(context);
      }

      return updated;
    }

    private boolean updateInput(Console console, ContainerShape parent) {
      if ((console.getConsoleType().equals(ConsoleType.IN)
          || console.getConsoleType().equals(ConsoleType.INOUT))
          && console.getInputPort().size() < 1) {
//        addPort(console, parent, Port.IN);
      } else if ((console.getConsoleType().equals(ConsoleType.OUT)
          && console.getInputPort().size() == 1)) {
//        removePort(console, parent, Port.IN);
      }
      return true;
    }

    private boolean updateOutput(Console console, ContainerShape parent) {
      if ((console.getConsoleType().equals(ConsoleType.OUT)
          || console.getConsoleType().equals(ConsoleType.INOUT))
          && console.getOutputPort().size() < 1) {
//        addPort(console, parent, Port.OUT);
      } else if ((console.getConsoleType().equals(ConsoleType.IN)
          && console.getOutputPort().size() == 1)) {
//        removePort(console, parent, Port.OUT);
      }
      return true;
    }

    @Override
    protected Class<? extends PortsDescr> getPortsDescription() {
      return Port.class;
    }
  }
}
