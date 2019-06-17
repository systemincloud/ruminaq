/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.javatask.features;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.tb.IContextButtonEntry;
import org.ruminaq.consts.Constants;
import org.ruminaq.gui.features.tools.AbstractContextButtonPadTool;

public class ContextButtonPadTool extends AbstractContextButtonPadTool {

  public ContextButtonPadTool(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public int getGenericContextButtons() {
    return Constants.CONTEXT_BUTTON_DELETE | Constants.CONTEXT_BUTTON_UPDATE;
  }

  @Override
  public Collection<IContextButtonEntry> getContextButtonPad(IPictogramElementContext context) {
    List<IContextButtonEntry> contexts = new ArrayList<>();
    return contexts;
  }

}
