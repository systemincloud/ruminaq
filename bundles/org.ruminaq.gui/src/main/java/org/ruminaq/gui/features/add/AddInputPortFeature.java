/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.add;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.mm.algorithms.styles.LineStyle;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.add.AddInputPortFeature.Filter;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.model.ruminaq.InputPort;
import org.ruminaq.model.ruminaq.Port;

@FeatureFilter(Filter.class)
public class AddInputPortFeature extends AddPortFeature {

  public static class Filter extends AddFeatureFilter {
    @Override
    public Class<? extends BaseElement> forBusinessObject() {
      return InputPort.class;
    }
  }

  public AddInputPortFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canAdd(IAddContext context) {
    return context.getTargetContainer() instanceof Diagram;
  }

  @Override
  protected int getWidth() {
    return 1;
  }

  @Override
  protected LineStyle getLineStyle(Port port) {
    if (((InputPort) port).isAsynchronous()) {
      return LineStyle.DOT;
    } else {
      return LineStyle.SOLID;
    }
  }
}
