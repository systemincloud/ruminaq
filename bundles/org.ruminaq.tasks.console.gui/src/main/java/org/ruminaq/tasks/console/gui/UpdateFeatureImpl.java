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

    public UpdateFeature(IFeatureProvider fp) {
      super(fp);
    }

    @Override
    public IReason updateNeeded(IUpdateContext context) {
      if (inputUpdateNeeded(context) || outputUpdateNeeded(context)
          || super.updateNeeded(context).toBoolean()) {
        return Reason.createTrueReason();
      }
      return Reason.createFalseReason();
    }

    private boolean inputUpdateNeeded(IUpdateContext context) {
      Console console = modelFromContext(context, Console.class)
          .orElseThrow(() -> new RuntimeException());
      ConsoleType type = console.getConsoleType();
      if (type == ConsoleType.IN || type == ConsoleType.INOUT) {
        return console.getInputPort().size() != 1;
      } else {
        return console.getInputPort().size() != 0;
      }
    }

    private boolean outputUpdateNeeded(IUpdateContext context) {
      Console console = modelFromContext(context, Console.class)
          .orElseThrow(() -> new RuntimeException());
      ConsoleType type = console.getConsoleType();
      if (type == ConsoleType.OUT || type == ConsoleType.INOUT) {
        return console.getOutputPort().size() != 1;
      } else {
        return console.getOutputPort().size() != 0;
      }
    }

    @Override
    public boolean update(IUpdateContext context) {
      Console console = modelFromShape(
          UpdateTaskFeature.shapeFromContext(context), Console.class)
              .orElseThrow(() -> new RuntimeException());

      boolean updated = false;

      if (inputUpdateNeeded(context)) {
        updated = updated | updateInput(console);
      }
      if (outputUpdateNeeded(context)) {
        updated = updated | updateOutput(console);
      }

      if (super.updateNeeded(context).toBoolean()) {
        updated = updated | super.update(context);
      }

      return updated;
    }

    private boolean updateInput(Console console) {
      ConsoleType type = console.getConsoleType();
      if ((type == ConsoleType.IN || type == ConsoleType.INOUT)
          && console.getInputPort().size() < 1) {
        createInputPort(console, Port.IN);
      } else if (type == ConsoleType.OUT
          && console.getInputPort().size() == 1) {
        console.getInputPort().clear();
      }
      return true;
    }

    private boolean updateOutput(Console console) {
      ConsoleType type = console.getConsoleType();
      if ((type == ConsoleType.OUT || type == ConsoleType.INOUT)
          && console.getOutputPort().size() < 1) {
        createOutputPort(console, Port.OUT);
      } else if (type == ConsoleType.IN
          && console.getOutputPort().size() == 1) {
        console.getOutputPort().clear();
      }
      return true;
    }

    @Override
    protected Class<? extends PortsDescr> getPortsDescription() {
      return Port.class;
    }
  }
}
